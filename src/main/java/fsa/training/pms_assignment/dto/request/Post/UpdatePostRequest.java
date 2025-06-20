package fsa.training.pms_assignment.dto.request.Post;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest {

    private UUID id;

    private String title;

    private String content;

    private String author;

    private boolean published;

    private LocalDateTime publishedAt;

    private UUID categoryId;
}
