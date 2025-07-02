package kg.sweezy.watchtime.mapper;

import kg.sweezy.watchtime.dto.CommentDtoRequest;
import kg.sweezy.watchtime.dto.CommentDtoResponse;
import kg.sweezy.watchtime.entity.CommentEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CommentsMapper {
    public static CommentEntity mapDtoRequestToEntity(CommentDtoRequest commentDtoRequest){
        CommentEntity commentEntity = CommentEntity.builder()
                .content(commentDtoRequest.getContent())
                .build();
        return commentEntity;
    }

    public static CommentDtoResponse mapEntityToDtoResponse(CommentEntity commentEntity){
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder()
                .id(commentEntity.getId())
                .content(commentEntity.getContent())
                .likes(commentEntity.getLikes())
                .dislikes(commentEntity.getDislikes())
                .dateCreated(commentEntity.getDateCreated())
                .userDtoPreview(UserMapper.mapEntityToDtoPreview(commentEntity.getUser()))
                .build();
        return commentDtoResponse;
    }

    public static List<CommentDtoResponse> mapEntityListToDtoResponseList(List<CommentEntity> commentEntityList){
        return commentEntityList.stream().map(CommentsMapper::mapEntityToDtoResponse).collect(Collectors.toList());
    }
}
