package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.MediaBaseEntity;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.entity.VideoEntity;
import kg.sweezy.watchtime.exception.*;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.service.AuthService;
import kg.sweezy.watchtime.utils.ManageTranslation;

import java.util.List;

public abstract class MediaBaseServiceImpl<T extends MediaBaseEntity>{
    protected final AuthService authService;
    protected final ManageTranslation manageTranslation;

    public MediaBaseServiceImpl(AuthService authService, ManageTranslation manageTranslation) {
        this.authService = authService;
        this.manageTranslation = manageTranslation;
    }

    protected abstract T findMediaById(Long id);
    protected abstract List<T> getLiked(UserEntity userEntity);
    protected abstract List<T> getDisliked(UserEntity userEntity);
    protected abstract List<T> getPlayList(UserEntity userEntity);
    protected abstract List<T> getHistory(UserEntity userEntity);
    protected abstract void saveUser(UserEntity user);
    protected abstract void saveMedia(T media);

    public void viewByMediaEntity(Long id) {
        T media = findMediaById(id);
        media.setViews(media.getViews() + 1);
        saveMedia(media);
    }

    public void likeMediaById(Long id) {
        T media = findMediaById(id);
        UserEntity userLiked = authService.getCurrentUser();
        List<T> likedVideos = getLiked(userLiked);
        List<T> dislikedVideos = getDisliked(userLiked);

        if(userLiked == null) throw new UserNotFoundException("error.userNotFound");

        if(!likedVideos.contains(media) && dislikedVideos.contains(media)) {
            dislikedVideos.remove(media);
            likedVideos.add(media);
            media.setDislikes(media.getDislikes() - 1);
            media.setLikes(media.getLikes() + 1);
        }
        else if(likedVideos.contains(media) && dislikedVideos.contains(media)) {
            likedVideos.add(media);
            media.setLikes(media.getLikes() + 1);
        }
        else{
            likedVideos.remove(media);
            media.setLikes(media.getLikes() - 1);
        }
        saveUser(userLiked);
        saveMedia(media);
    }

    public void dislikeMediaById(Long id) {
        T media = findMediaById(id);
        UserEntity userDisliked = authService.getCurrentUser();
        if(userDisliked == null) throw new UserNotFoundException("error.userNotFound");

        List<T> likedVideos = getLiked(userDisliked);
        List<T> dislikedVideos = getDisliked(userDisliked);

        if(!dislikedVideos.contains(media) && likedVideos.contains(media)) {
            likedVideos.remove(media);
            dislikedVideos.add(media);
            media.setLikes(media.getLikes() - 1);
            media.setDislikes(media.getDislikes() + 1);
        }
        else if(!dislikedVideos.contains(media) && !likedVideos.contains(media)) {
            dislikedVideos.add(media);
            media.setDislikes(media.getDislikes() + 1);
        }
        else{
            dislikedVideos.remove(media);
            media.setDislikes(media.getDislikes() - 1);
        }
        saveUser(userDisliked);
        saveMedia(media);
    }

    public void addToPlayList(Long videoId) {
        UserEntity userEntity = authService.getCurrentUser();
        T media = findMediaById(videoId);
        if(userEntity == null) throw new UserNotFoundException("error.authentication");

        List<T> playList = getPlayList(userEntity);

        if(!playList.contains(media)) {
            playList.add(media);
        }else{
            playList.remove(media);
        }
        saveUser(userEntity);
    }

    public void removeFromPlayList(Long videoId) {
        UserEntity userEntity = authService.getCurrentUser();
        T media = findMediaById(videoId);
        if(userEntity == null) throw new UserNotFoundException("error.authentication");

        List<T> playList = getPlayList(userEntity);

        if(!playList.contains(media)) throw new DoesNotExistInPlayListException("error.videoDoesNotExistInPlayList");
        playList.remove(media);
        saveUser(userEntity);
    }

    public void addMediaToHistory(Long id) {
        UserEntity userEntity = authService.getCurrentUser();
        T media = findMediaById(id);
        if(userEntity == null) return;

        List<T> history = getHistory(userEntity);

        if(!history.contains(media)) {
            history.add(media);
        } else if (history.contains(media)) {
            history.remove(media);
            history.add(media);
        }
        saveUser(userEntity);
    }
}
