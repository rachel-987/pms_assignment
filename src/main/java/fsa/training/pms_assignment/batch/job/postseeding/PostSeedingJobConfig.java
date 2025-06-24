package fsa.training.pms_assignment.batch.job.postseeding;

import com.github.javafaker.Faker;
import fsa.training.pms_assignment.entity.Category;
import fsa.training.pms_assignment.entity.Post;
import fsa.training.pms_assignment.repository.CategoryRepository;
import fsa.training.pms_assignment.repository.PostRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@EnableBatchProcessing
@Configuration
public class PostSeedingJobConfig {
    private final JobRepository jobRepository;
    private final CategoryRepository categoryRepository;
    private final PlatformTransactionManager transactionManager;

    public PostSeedingJobConfig(JobRepository jobRepository, CategoryRepository categoryRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.categoryRepository = categoryRepository;
        this.transactionManager = transactionManager;
    }

    @Bean("jobPostSeeding")
    public Job jobPostSeeding(JobRepository jobRepository,
                              @Qualifier("createFolder_Step1") Step step1,
                              @Qualifier("fakeData_Step2") Step step2,
                              @Qualifier("processData_Step3") Step step3,
                              @Qualifier("removeFile_Step4") Step step4,
                              @Qualifier("writeReport_Step5") Step step5)
    {
        return new JobBuilder(("jobPostSeeding"), jobRepository)
                .start(step1)
                .next(step2)
                .next(step3)
                .next(step4)
                .next(step5)
                .build();
    }

    // region Step 1 ~ Create new folder
    @Bean("createFolder_Step1")
    public Step createFolderStep() {
        return new StepBuilder("createFolder_Step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String folderPath = (String) chunkContext.getStepContext()
                            .getJobParameters()
                            .get("folderPath");

                    File folder = new File(folderPath);
                    if (!folder.exists()) {
                        boolean created = folder.mkdirs();
                        if (!created) {
                            throw new RuntimeException("Cannot create a new folder: " + folderPath);
                        }
                    }

                    System.out.println("Created new folder: " + folderPath);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
    // endregion

    // region Step 2 ~ Fake data
    @Bean("fakeData_Step2")
    public Step fakeDataStep() {
        return new StepBuilder("fakeData_Step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Faker faker = new Faker();
                    Random rand = new Random();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    String folderPath = (String) chunkContext.getStepContext()
                            .getJobParameters()
                            .get("folderPath");

                    File file = new File(folderPath + "/posts.csv");

                    try (FileWriter writer = new FileWriter(file)) {
                        // Ghi header
                        writer.write("title;content;author;categoryId;published;publishedAt\n");

                        List<UUID> listCategoryIds = categoryRepository.findAll()
                                .stream()
                                .map(Category::getId)
                                .toList();

                        for (int i = 1; i <= 1000; i++) {
                            String title = faker.book().title().replaceAll("[;,\\n]", " ");
                            String content = faker.lorem().sentence(15).replaceAll("[;,\\n]", " ");
                            String author = faker.name().username();
                            UUID categoryId = listCategoryIds.get(rand.nextInt(listCategoryIds.size()));
                            boolean published = rand.nextBoolean();
                            String publishedAt   = LocalDateTime.now().minusDays(rand.nextInt(30)).format(formatter);

                            writer.write(String.format("%s;%s;%s;%s;%b;%s\n",
                                    title, content, author, categoryId, published, publishedAt));
                        }

                        System.out.println("Created file with 1000 records!");
                    } catch (IOException e) {
                        throw new RuntimeException("Cannot write to file!", e);
                    }

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    // endregion

    // region Step 3 ~ Process data (content length < 500)
    @Bean("step3_itemReader")
    public ItemReader<Post> step3_itemReader(
            @Value("${job.seeding-job.step3.in}") Resource resource,
            CategoryRepository categoryRepository) {

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(";");
        tokenizer.setStrict(false);
        tokenizer.setNames("title", "content", "author", "categoryId", "published", "publishedAt");

        return new FlatFileItemReaderBuilder<Post>()
                .name("step3_itemReader")
                .resource(resource)
                .linesToSkip(1)
                .lineTokenizer(tokenizer)
                .fieldSetMapper(fieldSet -> {
                    String title = fieldSet.readString("title");
                    String content = fieldSet.readString("content");
                    String author = fieldSet.readString("author");

                    // categoryId
                    String categoryIdStr = fieldSet.readString("categoryId");
                    UUID categoryId = UUID.fromString(categoryIdStr.trim());
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Cannot file categoryId: " + categoryId));

                    // published
                    String publishedStr = fieldSet.readString("published");
                    boolean published = Boolean.parseBoolean(publishedStr.trim());

                    // publishedAt
                    String publishedAtStr = fieldSet.readString("publishedAt");
                    LocalDateTime publishedAt = null;
                    if (!publishedAtStr.trim().isEmpty()) {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            publishedAt = LocalDateTime.parse(publishedAtStr.trim(), formatter);
                        } catch (DateTimeParseException e) {
                            throw new RuntimeException("Wrong format of publishedAt: " + publishedAtStr, e);
                        }
                    }

                    return Post.builder()
                            .title(title)
                            .content(content)
                            .author(author)
                            .category(category)
                            .published(published)
                            .publishedAt(publishedAt)
                            .build();
                })
                .build();
    }

    @Bean("step3_itemProcessor")
    public ItemProcessor<Post, Post> step3_itemProcessor(){
        return new ItemProcessor<Post, Post>() {
            @Override
            public Post process(Post item) {
                if (item.getContent().length() > 500)
                    return null;
                return item;
            }
        };
    }

    @Bean("step3_itemWriter")
    public JpaItemWriter<Post> step3_itemWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<Post>()
                .entityManagerFactory(emf)
                .usePersist(true)
                .build();
    }

    // endregion

    // region Combine Step 3 ~ Read - Process - Write data to File
    @Bean("processData_Step3") // Tên Bean là processData_Step3
    public Step processDataStep3(JobRepository jobRepository,
                                 @Qualifier("step3_itemReader") ItemReader<Post> reader,
                                 @Qualifier("step3_itemProcessor") ItemProcessor<Post, Post> processor,
                                 @Qualifier("step3_itemWriter") ItemWriter<Post> writer,
                                 PlatformTransactionManager transactionManager) {
        return new StepBuilder("processData_Step3", jobRepository) // Tên Step ~ processData_Step3
                .<Post, Post>chunk(50, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // endregion

    // region Step 4 ~ Remove mock File
    @Bean("removeFile_Step4")
    public Step removeMockFile(@Value("${job.seeding-job.step3.in}") Resource resource) {
        return new StepBuilder("removeFile_Step4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    File file = resource.getFile();

                    if (file.exists()) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            System.out.println("Deleted file: " + file.getAbsolutePath());
                        } else {
                            throw new RuntimeException("Cannot delete file: " + file.getAbsolutePath());
                        }
                    } else {
                        System.out.println("File does not exist: " + file.getAbsolutePath());
                    }

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    // endregion

    // region Step 5: ghi file report với định dạng: "Tổng số posts trong DB: 1000"
    @Bean("writeReport_Step5")
    public Step writeReportStep(PostRepository postRepository) {
        return new StepBuilder("writeReport_Step5", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    // Lấy folderPath từ Job Parameters
                    String folderPath = (String) chunkContext.getStepContext()
                            .getJobParameters()
                            .get("folderPath");

                    if (folderPath == null || folderPath.isBlank()) {
                        throw new RuntimeException("Missing 'folderPath' in Job Parameters");
                    }

                    // Đếm số lượng Post trong DB
                    long totalPosts = postRepository.count();
                    String reportContent = "Tổng số posts trong DB: " + totalPosts;

                    // Ghi file report.txt
                    File reportFile = new File(folderPath + "/report.txt");

                    try (FileWriter writer = new FileWriter(reportFile)) {
                        writer.write(reportContent);
                        System.out.println("Writed to report: " + reportContent);
                    } catch (IOException e) {
                        throw new RuntimeException("Cannot write to this file report.txt", e);
                    }

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    // endregion

    // region Job 1 + 2
    @Bean("jobStep1and2")
    public Job jobStep1and2(JobRepository jobRepository,
                            @Qualifier("createFolder_Step1") Step step1,
                            @Qualifier("fakeData_Step2") Step step2) {
        return new JobBuilder("jobStep1and2", jobRepository)
                .start(step1)
                .next(step2)
                .build();
    }

    // endregion

    // region Job 3
    @Bean("jobStep3")
    public Job jobStep3(JobRepository jobRepository,
                        @Qualifier("processData_Step3") Step step3) {
        return new JobBuilder("jobStep3", jobRepository)
                .start(step3)
                .build();
    }
    // endregion

    // region Job 4
    @Bean("jobStep4")
    public Job jobStep4(JobRepository jobRepository,
                        @Qualifier("removeFile_Step4") Step step4) {
        return new JobBuilder("jobStep4", jobRepository)
                .start(step4)
                .build();
    }

    // endregion

    // region Job 5
    @Bean("jobStep5")
    public Job jobStep5(JobRepository jobRepository,
                        @Qualifier("writeReport_Step5") Step step5) {
        return new JobBuilder("jobStep5", jobRepository)
                .start(step5)
                .build();
    }

    // endregion


}
