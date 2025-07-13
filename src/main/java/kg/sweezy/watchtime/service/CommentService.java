package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.entity.CommentEntity;

public interface CommentService extends MediaBaseService{
    public CommentEntity updateComment(Long commentId,CommentEntity comment);
}
