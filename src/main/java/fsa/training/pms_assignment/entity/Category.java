package fsa.training.pms_assignment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "category")
public class Category extends Auditable{

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
}
