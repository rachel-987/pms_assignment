package fsa.training.pms_assignment.service.impl;

import fsa.training.pms_assignment.dto.request.category.CreateCategoryRequest;
import fsa.training.pms_assignment.dto.request.category.UpdateCategoryRequest;
import fsa.training.pms_assignment.dto.response.category.CategoryResponse;
import fsa.training.pms_assignment.entity.Category;
import fsa.training.pms_assignment.mapper.CategoryMapper;
import fsa.training.pms_assignment.repository.CategoryRepository;
import fsa.training.pms_assignment.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public void seedCategory() {
        for (int i = 1; i <= 5; i++)
        {
            CreateCategoryRequest categoryRequest = new CreateCategoryRequest("Category " + i, "Description " + i, true);
            Category category = categoryMapper.toEntity(categoryRequest);
            categoryRepository.save(category);
        }
    }

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByActive(true)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElse(null);
    }

    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id).orElse(null);
        if (existingCategory != null){
            categoryMapper.updateEntity(request, existingCategory);
            categoryRepository.save(existingCategory);
            return categoryMapper.toResponse(existingCategory);
        }
        return null;
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    public void changeCategoryToActive(UUID id, boolean isActive) {
        Category existingCategory = categoryRepository.findById(id).orElse(null);
        if (existingCategory != null){
            existingCategory.setActive(isActive);
            categoryRepository.save(existingCategory);
        }
    }
}
