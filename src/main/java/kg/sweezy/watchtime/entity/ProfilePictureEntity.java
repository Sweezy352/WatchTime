package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_picture")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProfilePictureEntity extends BaseEntity{
    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}
