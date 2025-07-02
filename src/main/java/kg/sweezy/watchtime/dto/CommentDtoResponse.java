package kg.sweezy.watchtime.dto;

import lombok.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentDtoResponse {
    private Long id;
    private String content;
    private Long likes;
    private Long dislikes;
    private LocalDate dateCreated;
    private Long videoId;
    private UserDtoPreview userDtoPreview;
}
