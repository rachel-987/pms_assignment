package fsa.training.pms_assignment.mapper;

import fsa.training.pms_assignment.dto.request.Post.CreatePostRequest;
import fsa.training.pms_assignment.dto.request.Post.UpdatePostRequest;
import fsa.training.pms_assignment.dto.response.category.CategorySummaryResponse;
import fsa.training.pms_assignment.dto.response.post.PostResponse;
import fsa.training.pms_assignment.entity.Category;
import fsa.training.pms_assignment.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

        public PostResponse toResponse(Post post) {
            if (post == null) return null;

            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setAuthor(post.getAuthor());
            response.setPublished(post.isPublished());
            response.setPublishedAt(post.getPublishedAt());

            Category category = post.getCategory();
            if (category != null) {
                CategorySummaryResponse categorySummary = new CategorySummaryResponse(category.getId(),category.getName());
                categorySummary.setId(category.getId());
                categorySummary.setName(category.getName());
                response.setCategory(categorySummary);
            }

            return response;
        }

        public Post toEntity(CreatePostRequest request, Category category) {
            Post post = new Post();
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setAuthor(request.getAuthor());
            post.setPublished(request.isPublished());
            post.setPublishedAt(request.isPublished() ? request.getPublishedAt() : null);
            post.setCategory(category);
            return post;
        }

        public void updateEntity(UpdatePostRequest request, Post post, Category category) {
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setAuthor(request.getAuthor());
            post.setPublished(request.isPublished());
            post.setPublishedAt(request.isPublished() ? request.getPublishedAt() : null);
            post.setCategory(category);
        }
}
