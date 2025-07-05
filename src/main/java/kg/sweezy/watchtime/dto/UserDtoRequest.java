package kg.sweezy.watchtime.dto;

import kg.sweezy.watchtime.utils.annotations.CheckEmptySpace;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDtoRequest {
    @CheckEmptySpace
    private String username;
    @CheckEmptySpace
    private String password;
    @CheckEmptySpace
    private String email;
}
