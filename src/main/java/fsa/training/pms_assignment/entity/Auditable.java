package fsa.training.pms_assignment.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @LastModifiedBy
    private String lastModifiedBy;

    @Column
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Column
    @CreatedBy
    private String createdBy;

    @Column
    @CreatedDate
    private LocalDateTime createdDate;

    @Column
    @NonNull
    private boolean isDeleted = false;
}
