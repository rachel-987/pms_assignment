package fsa.training.pms_assignment.web;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobLauncherController {

    private final JobLauncher jobLauncher;
    private final Job jobStep1and2;
    private final Job jobStep3;
    private final Job jobStep4;
    private final Job jobStep5;
    private final Job jobPostSeeding;

    // region Test full job
    @GetMapping("/test-full-job")
    public String testFullJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("folderPath", "D:/Programming/Java/pms_assignment/data")
                    .addLong("timestamp", System.currentTimeMillis()) // tránh bị trùng job
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(jobPostSeeding, params);
            return "Job runs";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error when run Step 1 and 2: " + e.getMessage();
        }
    }
    // endregion

    // region test job for Step 1 & 2
    @GetMapping("/test-job-step1-and-2")
    public String runStep1and2() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("folderPath", "D:/Programming/Java/pms_assignment/data")
                    .addLong("timestamp", System.currentTimeMillis()) // tránh bị trùng job
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(jobStep1and2, params);
            return "Step 1 and 2 is OK!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error when run Step 1 and 2: " + e.getMessage();
        }
    }
    // endregion

    // region test job for Step 3
    @GetMapping("/test-job-step3")
    public String runStep3() {
        try {
            JobExecution jobExecution = jobLauncher.run(jobStep3, new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters());
            return "Step 3 is OK!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in Step 3: " + e.getMessage();
        }
    }
    // endregion

    // region test job for Step 4
    @GetMapping("/test-job-step4")
    public String runStep4() {
        try {
            JobExecution jobExecution = jobLauncher.run(jobStep4, new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters());
            return "Step 4 is OK!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in Step 4: " + e.getMessage();
        }
    }
    // endregion

    // region test job for Step 5
    @GetMapping("/test-job-step5")
    public String runStep5() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("folderPath", "D:/Programming/Java/pms_assignment/data")
                    .addLong("timestamp", System.currentTimeMillis()) // tránh bị trùng job
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(jobStep5, params);
            return "Step 5 OK!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Step 5: " + e.getMessage();
        }
    }
    // endregion

}
