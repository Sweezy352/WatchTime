package kg.sweezy.watchtime.dto;

import lombok.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class VideoDtoResponse {
    private Long id;
    private String title;
    private String description;
    private Long views;
    private Long likes;
    private Long dislikes;
    private Long amountComments;
    private LocalDate dateCreated;
    private String fileName;
    private ImageDto previewImage;
    private UserDtoPreview userPreview;
}
