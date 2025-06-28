package kg.sweezy.watchtime.dto;

import lombok.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDtoPreview {
    private Long id;
    private String username;
    private Long subscribers;
    private LocalDate dateCreated;
    private ImageDto profilePicture;
}
