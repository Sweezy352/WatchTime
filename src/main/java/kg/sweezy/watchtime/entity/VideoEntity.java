package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "videos")
@RequiredArgsConstructor
@Getter
@Setter
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private UserEntity user;

    @PrePersist
    public void prePersist(){
        views = 0l;
        likes = 0l;
        dislikes = 0l;
        amountComments = 0l;
        dateCreated = LocalDate.now();
    }
}
