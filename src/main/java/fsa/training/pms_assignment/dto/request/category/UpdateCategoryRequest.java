package fsa.training.pms_assignment.dto.request.category;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryRequest {
    private UUID id;

    private String name;

    private String description;

    private boolean active;
}
