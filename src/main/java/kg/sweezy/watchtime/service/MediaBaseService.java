package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.entity.CommentEntity;

public interface MediaBaseService {
    public void likeMediaById(Long id);
    public void dislikeMediaById(Long id);
    public void addToPlayList(Long id);
    public void removeFromPlayList(Long id);
    public void addMediaToHistory(Long id);
    public CommentEntity addCommentToMedia(Long id, CommentEntity commentEntity);
}
