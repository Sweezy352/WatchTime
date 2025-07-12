package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "comments")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CommentEntity extends MediaBaseEntity {
    @Column(name = "content", nullable = false)
    private String content;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_comments",
            joinColumns = @JoinColumn(name = "comment_id_original", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id_reply", referencedColumnName = "id")
    )
    private List<CommentEntity> comments;

    @PrePersist
    public void prePersist(){
        likes = 0l;
        dislikes = 0l;
        dateCreated = LocalDate.now();
        views = 0l;
        amountComments = 0l;
    }
}
