package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "comments")
@RequiredArgsConstructor
@Getter
@Setter
public class CommentEntity extends BaseEntity{
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "likes")
    private Long likes;
    @Column(name = "dislikes")
    private Long dislikes;
    @Column(name = "date_created")
    private LocalDate dateCreated;
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
