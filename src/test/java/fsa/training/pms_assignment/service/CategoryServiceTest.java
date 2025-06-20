package fsa.training.pms_assignment.service;

import fsa.training.pms_assignment.dto.request.category.CreateCategoryRequest;
import fsa.training.pms_assignment.dto.request.category.UpdateCategoryRequest;
import fsa.training.pms_assignment.dto.response.category.CategoryResponse;
import fsa.training.pms_assignment.entity.Category;
import fsa.training.pms_assignment.mapper.CategoryMapper;
import fsa.training.pms_assignment.repository.CategoryRepository;
import fsa.training.pms_assignment.service.impl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID categoryId;

    // ========== TEST 1 ==========
    @Nested
    class CreateCategoryTest {
        private CreateCategoryRequest request;
        private Category category;
        private CategoryResponse response;

        @BeforeEach
        void setUp() {
            categoryId = UUID.randomUUID();
            request = CreateCategoryRequest.builder().name("Tech").build();
            category = Category.builder().name("Tech").active(true).build();
            category.setId(categoryId);
            response = CategoryResponse.builder().id(categoryId).name("Tech").active(true).build();
        }

        @Test
        void shouldCreateCategorySuccessfully() {
            when(categoryMapper.toEntity(request)).thenReturn(category);
            when(categoryMapper.toResponse(category)).thenReturn(response);

            CategoryResponse result = categoryService.createCategory(request);

            assertEquals("Tech", result.getName());
            verify(categoryRepository).save(category);
            verify(categoryMapper).toEntity(request);
        }
    }

    // ========== TEST 2 ==========
    @Nested
    class GetAllCategoriesTest {
        private Category category;
        private CategoryResponse response;

        @BeforeEach
        void setUp() {
            categoryId = UUID.randomUUID();
            category = Category.builder().name("Tech").active(true).build();
            category.setId(categoryId);
            response = CategoryResponse.builder().id(categoryId).name("Tech").active(true).build();
        }

        @Test
        void shouldReturnAllCategories() {
            when(categoryRepository.findAll()).thenReturn(List.of(category));
            when(categoryMapper.toResponse(category)).thenReturn(response);

            List<CategoryResponse> result = categoryService.getAllCategories();

            assertEquals(1, result.size());
            assertEquals("Tech", result.get(0).getName());
        }
    }

    // ========== TEST 3 ==========
    @Nested
    class GetActiveCategoriesTest {
        private Category category;
        private CategoryResponse response;

        @BeforeEach
        void setUp() {
            categoryId = UUID.randomUUID();
            category = Category.builder().name("Tech").active(true).build();
            category.setId(categoryId);
            response = CategoryResponse.builder().id(categoryId).name("Tech").active(true).build();
        }

        @Test
        void shouldReturnOnlyActiveCategories() {
            when(categoryRepository.findByActive(true)).thenReturn(List.of(category));
            when(categoryMapper.toResponse(category)).thenReturn(response);

            List<CategoryResponse> result = categoryService.getActiveCategories();

            assertEquals(1, result.size());
            assertTrue(result.get(0).isActive());
        }
    }

    // ========== TEST 4 ==========
    @Nested
    class GetCategoryByIdTest {
        private Category category;
        private CategoryResponse response;

        @BeforeEach
        void setUp() {
            categoryId = UUID.randomUUID();
            category = Category.builder().name("Tech").active(true).build();
            category.setId(categoryId);
            response = CategoryResponse.builder().id(categoryId).name("Tech").active(true).build();
        }

        @Test
        void shouldReturnCategoryWhenFound() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryMapper.toResponse(category)).thenReturn(response);

            CategoryResponse result = categoryService.getCategoryById(categoryId);

            assertNotNull(result);
            assertEquals(categoryId, result.getId());
        }

        @Test
        void shouldReturnNullWhenNotFound() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            CategoryResponse result = categoryService.getCategoryById(categoryId);

            assertNull(result);
        }
    }

    // ========== TEST 5 ==========
    @Nested
    class UpdateCategoryTest {
        private UpdateCategoryRequest updateRequest;
        private Category category;
        private CategoryResponse response;

        @BeforeEach
        void setUp() {
            categoryId = UUID.randomUUID();
            updateRequest = UpdateCategoryRequest.builder().name("Updated Name").build();
            category = Category.builder().name("Old Name").active(true).build();
            category.setId(categoryId);
            response = CategoryResponse.builder().id(categoryId).name("Updated Name").active(true).build();
        }

        @Test
        void shouldUpdateWhenCategoryFound() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            doNothing().when(categoryMapper).updateEntity(updateRequest, category);
            when(categoryMapper.toResponse(category)).thenReturn(response);

            CategoryResponse result = categoryService.updateCategory(categoryId, updateRequest);

            assertNotNull(result);
            verify(categoryRepository).save(category);
        }

        @Test
        void shouldReturnNullWhenCategoryNotFound() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            CategoryResponse result = categoryService.updateCategory(categoryId, updateRequest);

            assertNull(result);
        }
    }

    // ========== TEST 6 ==========
    @Nested
    class DeleteCategoryTest {
        @BeforeEach
        void setUp() {
            categoryId = UUID.randomUUID();
        }

        @Test
        void shouldDeleteCategoryById() {
            categoryService.deleteCategory(categoryId);
            verify(categoryRepository).deleteById(categoryId);
        }
    }

    // ========== TEST 7 ==========
    @Nested
    class ChangeCategoryActiveStatusTest {
        @ParameterizedTest
        @CsvSource({
                "true, false",
                "false, true"
        })
        void shouldChangeActiveStatus(boolean initial, boolean target) {
            categoryId = UUID.randomUUID();
            Category category = Category.builder().name("Tech").active(initial).build();
            category.setId(categoryId);

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

            categoryService.changeCategoryToActive(categoryId, target);

            assertEquals(target, category.isActive());
            verify(categoryRepository).save(category);
        }

        @Test
        void shouldDoNothingIfCategoryNotFound() {
            categoryId = UUID.randomUUID();

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            categoryService.changeCategoryToActive(categoryId, false);

            verify(categoryRepository, never()).save(any());
        }
    }
}
