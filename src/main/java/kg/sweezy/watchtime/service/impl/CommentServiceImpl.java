package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.CommentEntity;
import kg.sweezy.watchtime.entity.MediaBaseEntity;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.exception.CommentBodyEmptyException;
import kg.sweezy.watchtime.exception.CommentNotFound;
import kg.sweezy.watchtime.repository.CommentRepository;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.service.AuthService;
import kg.sweezy.watchtime.service.CommentService;
import kg.sweezy.watchtime.utils.ManageTranslation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CommentServiceImpl extends MediaBaseServiceImpl<CommentEntity> implements CommentService {
    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final ManageTranslation manageTranslation;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, AuthService authService, ManageTranslation manageTranslation, UserRepository userRepository) {
        super(authService, manageTranslation);
        this.commentRepository = commentRepository;
        this.authService = authService;
        this.manageTranslation = manageTranslation;
        this.userRepository = userRepository;
    }

    @Override
    protected CommentEntity findMediaById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFound("error.commentNotFound"));
    }

    @Override
    protected List<CommentEntity> getLiked(UserEntity userEntity) {
        return userEntity.getCommentsLiked();
    }

    @Override
    protected List<CommentEntity> getDisliked(UserEntity userEntity) {
        return userEntity.getCommentsDisliked();
    }

    @Override
    protected List<CommentEntity> getPlayList(UserEntity userEntity) {
        return List.of();
    }

    @Override
    protected List<CommentEntity> getHistory(UserEntity userEntity) {
        return List.of();
    }

    @Override
    protected List<CommentEntity> getCommentsMedia(CommentEntity media) {
        return media.getComments();
    }

    @Override
    protected CommentEntity saveComment(CommentEntity media, CommentEntity comment) {
        CommentEntity commentEntity = commentRepository.save(comment);
        commentRepository.saveAndFlush(media);
        return commentEntity;
    }

    @Override
    protected void saveUser(UserEntity user) {
        userRepository.saveAndFlush(user);
    }

    @Override
    protected void saveMedia(CommentEntity media) {
        commentRepository.saveAndFlush(media);
    }

    @Override
    public CommentEntity updateComment(Long commentId,CommentEntity comment) {
        UserEntity userEntity = authService.getCurrentUser();
        CommentEntity commentFound = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFound("error.commentNotFound"));
        if(userEntity == null) throw new AuthenticationException("error.authentication");
        if(!commentFound.getUser().equals(userEntity)) throw new AuthenticationException("error.authentication");
        if(comment.getContent().replace(" ", "").isEmpty()) throw new CommentBodyEmptyException("error.commentEmptyBody");

        commentFound.setContent(comment.getContent());
        commentFound.setDateCreated(LocalDate.now());
        return commentRepository.saveAndFlush(commentFound);
    }

    @Override
    public String deleteCommentById(Long id) {
        UserEntity userEntity = authService.getCurrentUser();
        CommentEntity commentFound = commentRepository.findById(id).orElseThrow(() -> new CommentNotFound("error.commentNotFound"));
        if(userEntity == null) throw new AuthenticationException("error.authentication");
        if(!commentFound.getUser().equals(userEntity)) throw new AuthenticationException("error.authentication");
        commentRepository.delete(commentFound);

        return "success.deleteComment";
    }
}
