package kg.sweezy.watchtime.dto;

import lombok.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class VideoDtoPreview {
    private Long id;
    private String fileName;
    private String title;
    private Long views;
    private ImageDto videoPreview;
    private LocalDate dateCreated;
    private UserDtoPreview channel;
}
