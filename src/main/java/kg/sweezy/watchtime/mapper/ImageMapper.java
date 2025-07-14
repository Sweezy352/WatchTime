package kg.sweezy.watchtime.mapper;

import kg.sweezy.watchtime.dto.ImageDto;


public class ImageMapper {
    public static ImageDto mapToImageDto(Long id, String fileName) {
        return new ImageDto().builder()
                .id(id)
                .fileName(fileName)
                .build();
    }
}
