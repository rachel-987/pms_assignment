package fsa.training.pms_assignment.mapper;

import fsa.training.pms_assignment.dto.request.category.CreateCategoryRequest;
import fsa.training.pms_assignment.dto.request.category.UpdateCategoryRequest;
import fsa.training.pms_assignment.dto.response.category.CategoryResponse;
import fsa.training.pms_assignment.entity.Category;
import org.springframework.stereotype.Component;


@Component
public class CategoryMapper {
    public Category toEntity(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setActive(request.isActive());
        return category;
    }

    public CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setActive(category.isActive());
        return response;
    }

    public void updateEntity(UpdateCategoryRequest request, Category category) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setActive(request.isActive());
    }
}
