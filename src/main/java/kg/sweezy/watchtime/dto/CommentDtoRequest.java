package kg.sweezy.watchtime.dto;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentDtoRequest {
    private String content;
    private Long videoId;
}
