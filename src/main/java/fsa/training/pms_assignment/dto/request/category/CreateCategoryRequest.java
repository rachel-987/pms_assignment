package fsa.training.pms_assignment.dto.request.category;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {
    private String name;

    private String description;

    private boolean active;
}
