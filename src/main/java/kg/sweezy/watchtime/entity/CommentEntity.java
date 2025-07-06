package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "comments")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CommentEntity extends MediaBaseEntity{
    @Column(name = "content", nullable = false)
    private String content;
    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "id")
    private VideoEntity video;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @PrePersist
    public void prePersist(){
        likes = 0l;
        dislikes = 0l;
        dateCreated = LocalDate.now();
    }
}
