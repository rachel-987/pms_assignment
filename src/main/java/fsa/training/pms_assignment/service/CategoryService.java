package fsa.training.pms_assignment.service;

import fsa.training.pms_assignment.dto.request.category.CreateCategoryRequest;
import fsa.training.pms_assignment.dto.request.category.UpdateCategoryRequest;
import fsa.training.pms_assignment.dto.response.category.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    void seedCategory();

    CategoryResponse createCategory(CreateCategoryRequest request);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getActiveCategories();

    CategoryResponse getCategoryById(UUID id);

    CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);

    void deleteCategory(UUID id);

    void changeCategoryToActive(UUID id, boolean isActive);
}
