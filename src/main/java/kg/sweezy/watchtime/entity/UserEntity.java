package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UserEntity extends BaseEntity implements UserDetails {
    @Column(name = "username", nullable = false, unique = true)
    private String username;
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
    @ManyToMany(mappedBy = "users",fetch = FetchType.EAGER)
    private List<RoleEntity> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_subscription_users",
            joinColumns = @JoinColumn(name = "subscriber_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id", referencedColumnName = "id", unique = true)
    )
    private List<UserEntity> subscriptionList;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_subscribers_users",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id_subscriber", referencedColumnName = "id")
    )
    private List<UserEntity> subscribersList;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private ProfilePictureEntity profilePicture;

    @OneToMany(mappedBy = "channel",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<VideoEntity> videos;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_likes_videos",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id", unique = true)
    )
    private List<VideoEntity> videoLiked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_dislikes_videos",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id", unique = true)
    )
    private List<VideoEntity> videoDisliked;

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
    private List<CommentEntity> commentsLiked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "m2m_comments_dislikes",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id")
    )
    private List<CommentEntity> commentsDisliked;

    @PrePersist
    public void prePersist(){
        subscribers = 0l;
        isPremium = false;
        dateCreated = LocalDate.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
