package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "videos")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class VideoEntity extends BaseEntity {
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "views")
    private Long views;
    @Column(name = "likes")
    private Long likes;
    @Column(name = "dislikes")
    private Long dislikes;
    @Column(name = "amount_comments")
    private Long amountComments;
    @Column(name = "date_created")
    private LocalDate dateCreated;
    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;
    @OneToOne(mappedBy = "video", fetch = FetchType.EAGER)
    private PreviewVideoEntity previewVideo;
    @OneToMany(mappedBy = "video",fetch = FetchType.EAGER)
    private List<CommentEntity> comments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private UserEntity channel;

    @PrePersist
    public void prePersist(){
        views = 0l;
        likes = 0l;
        dislikes = 0l;
        amountComments = 0l;
        dateCreated = LocalDate.now();
    }
}
