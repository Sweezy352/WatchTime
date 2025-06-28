package kg.sweezy.watchtime.dto;

import lombok.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDtoResponse {
    private Long id;
    private String username;
    private String email;
    private Long subscribers;
    private Boolean isPremium;
    private LocalDate dateCreated;
    private ImageDto profilePicture;
}
