package fsa.training.pms_assignment.repository;

import fsa.training.pms_assignment.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Page<Post> findAll(Pageable pageable);


    // Phân trang theo tiêu đề chứa từ khóa (không phân biệt hoa thường)
    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Post> findByAuthor(String author, Pageable pageable);

    List<Post> findByCategoryIdAndPublishedTrue(UUID categoryId);

    List<Post> findByCategoryId(UUID categoryId);

    List<Post> findByPublishedTrue();

    List<Post> findByTitleContainingIgnoreCase(String keyword);

    List<Post> findByAuthor(String author);
}
