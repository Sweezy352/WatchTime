package kg.sweezy.watchtime.dto;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class VideoDtoRequest {
    private String title;
    private String description;

}
