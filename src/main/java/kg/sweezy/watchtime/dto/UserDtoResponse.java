package kg.sweezy.watchtime.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    private List<UserDtoPreview> subscriptionList;
    private List<VideoDtoPreview> videoChannelList;
    private List<VideoDtoPreview> videoLikedList;
    private List<VideoDtoPreview> videoPlayList;
}
