package fsa.training.pms_assignment.dto.response.post;

import fsa.training.pms_assignment.dto.response.category.CategorySummaryResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private UUID id;
    private String title;
    private String content;
    private String author;
    private boolean published;
    private LocalDateTime publishedAt;
    private CategorySummaryResponse category;}
