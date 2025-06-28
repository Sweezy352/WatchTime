package kg.sweezy.watchtime.dto;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDtoRequest {
    private String username;
    private String password;
    private String email;
}
