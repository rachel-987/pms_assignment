package fsa.training.pms_assignment.web;

import fsa.training.pms_assignment.dto.request.category.CreateCategoryRequest;
import fsa.training.pms_assignment.dto.request.category.UpdateCategoryRequest;
import fsa.training.pms_assignment.dto.response.category.CategoryResponse;
import fsa.training.pms_assignment.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.ok(response);
    }

    // GET ALL
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // GET ACTIVE
    @GetMapping("/active")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoryResponse>> getActiveCategories() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    // GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable UUID id, @RequestBody UpdateCategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    // DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // TOGGLE ACTIVE
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/set-active")
    public ResponseEntity<Void> toggleActive(@PathVariable UUID id, @RequestParam boolean active) {
        categoryService.changeCategoryToActive(id, active);
        return ResponseEntity.noContent().build();
    }
}
