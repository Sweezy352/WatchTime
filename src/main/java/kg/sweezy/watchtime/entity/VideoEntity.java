package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "videos")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class VideoEntity extends MediaBaseEntity {
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;
    @OneToOne(mappedBy = "video", fetch = FetchType.EAGER)
    private PreviewVideoEntity previewVideo;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_comments_videos",
            joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id", unique = true)
    )
    private List<CommentEntity> comments;
    @ManyToOne(fetch = FetchType.EAGER)
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
