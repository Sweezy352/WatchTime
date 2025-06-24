package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@RequiredArgsConstructor
@Getter
@Setter
public class UserEntity extends BaseEntity{
    @Column(name = "username", nullable = false, unique = true)
    private String userName;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "uuid")
    private String uuId;
    @Column(name = "subscribers")
    private Long subscribers;
    @Column(name = "is_premium")
    private Boolean isPremium;
    @Column(name = "date_created")
    private LocalDate dateCreated;
    @ManyToMany
    @JoinTable(name = "m2m_subscribers_users",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id_subscriber", referencedColumnName = "id", unique = true)
    )
    private List<UserEntity> subscribtionList;
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private ProfilePictureEntity profilePicture;
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<VideoEntity> videos;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_likes_videos",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id", unique = true)
    )
    private List<VideoEntity> videoLiked;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_play_list_videos",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id", unique = true)
    )
    private List<VideoEntity> videoPlayList;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_history_videos",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns =@JoinColumn(name = "video_id", referencedColumnName = "id")
    )
    private List<VideoEntity> videoHistory;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_likes_comments",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id")
    )
    private List<CommentEntity> comments;

    @PrePersist
    public void prePersist(){
        subscribers = 0l;
        isPremium = false;
        dateCreated = LocalDate.now();
    }
}
