package fsa.training.pms_assignment.repository;

import fsa.training.pms_assignment.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByName(String name);
    List<Category> findByActive(boolean active);
}
