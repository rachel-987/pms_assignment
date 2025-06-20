package fsa.training.pms_assignment.dto.response.category;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CategorySummaryResponse {
    private UUID id;
    private String name;
}
