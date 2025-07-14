package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "preview_video")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class PreviewVideoEntity extends BaseEntity {
    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;
    @OneToOne
    @JoinColumn(name = "video_id", referencedColumnName = "id")
    private VideoEntity video;
}
