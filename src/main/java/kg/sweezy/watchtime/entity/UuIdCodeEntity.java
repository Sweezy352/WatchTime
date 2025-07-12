package kg.sweezy.watchtime.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("UuidCode")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UuIdCodeEntity implements Serializable {
    @Id
    private Long id;
    private String email;
    @Indexed
    private String code;
}
