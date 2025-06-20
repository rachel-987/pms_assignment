package fsa.training.pms_assignment.dto.response.category;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private UUID id;

    private String name;

    private String description;

    private boolean active;
}
