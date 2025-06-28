package kg.sweezy.watchtime.dto;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ImageDto {
    private Long id;
    private String fileName;
}
