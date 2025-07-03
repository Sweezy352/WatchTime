package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.entity.VideoEntity;
import kg.sweezy.watchtime.exception.SubscribtionFailedException;
import kg.sweezy.watchtime.exception.UserNotFoundException;
import kg.sweezy.watchtime.exception.VideoNotFoundException;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.repository.VideoRepository;
import kg.sweezy.watchtime.service.AuthService;
import kg.sweezy.watchtime.service.MediaService;
import kg.sweezy.watchtime.service.UserService;
import kg.sweezy.watchtime.utils.ManageTranslation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediaServiceImpl implements MediaService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final ManageTranslation manageTranslation;
    private final VideoRepository videoRepository;

    @Autowired
    public MediaServiceImpl(AuthService authService, UserRepository userRepository, ManageTranslation manageTranslation, VideoRepository videoRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.manageTranslation = manageTranslation;
        this.videoRepository = videoRepository;
    }

    @Override
    public String subscribeByChannelId(Long channelId) {
        UserEntity subscriber = authService.getCurrentUser();
        UserEntity channel = userRepository.findById(channelId).orElseThrow(() -> new UserNotFoundException("error.userNotFound"));
        if(subscriber != null && channel != null){
            if(!subscriber.getSubscriptionList().contains(channel)) {
                subscriber.getSubscriptionList().add(channel);
                channel.getSubscribersList().add(subscriber);
                channel.setSubscribers(channel.getSubscribers() + 1);
                userRepository.saveAndFlush(channel);
                userRepository.saveAndFlush(subscriber);
                return manageTranslation.getTranslation("success.subscription");
            }else{
                subscriber.getSubscriptionList().remove(channel);
                channel.getSubscribersList().remove(subscriber);
                userRepository.saveAndFlush(channel);
                userRepository.saveAndFlush(subscriber);
                return manageTranslation.getTranslation("success.unSubscribe");
            }
        }
        return "";
    }

    @Override
    public void viewVideoById(Long videoId) {
        VideoEntity videoEntity = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
        videoEntity.setViews(videoEntity.getViews() + 1);
        videoRepository.saveAndFlush(videoEntity);
    }

    @Override
    public void likeVideoById(Long videoId) {
        VideoEntity videoEntity = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
        UserEntity userLiked = authService.getCurrentUser();

        if(userLiked == null) throw new UserNotFoundException("error.userNotFound");

        if(!userLiked.getVideoLiked().contains(videoEntity) && userLiked.getVideoDisliked().contains(videoEntity)) {
            userLiked.getVideoDisliked().remove(videoEntity);
            userLiked.getVideoLiked().add(videoEntity);
            videoEntity.setLikes(videoEntity.getLikes() + 1);
        }
        else if(!userLiked.getVideoLiked().contains(videoEntity) && !userLiked.getVideoDisliked().contains(videoEntity)) {
            userLiked.getVideoLiked().add(videoEntity);
            videoEntity.setLikes(videoEntity.getLikes() + 1);
        }
        else{
            userLiked.getVideoLiked().remove(videoEntity);
            videoEntity.setLikes(videoEntity.getLikes() - 1);
        }
        userRepository.saveAndFlush(userLiked);
        videoRepository.saveAndFlush(videoEntity);
    }

    @Override
    public void dislikeVideoById(Long videoId) {
        VideoEntity videoEntity = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
        UserEntity userDisliked = authService.getCurrentUser();
        if(userDisliked == null) throw new UserNotFoundException("error.userNotFound");

        if(!userDisliked.getVideoDisliked().contains(videoEntity) && userDisliked.getVideoLiked().contains(videoEntity)) {
            userDisliked.getVideoLiked().remove(videoEntity);
            userDisliked.getVideoDisliked().add(videoEntity);
            videoEntity.setDislikes(videoEntity.getDislikes() + 1);
        }
        else if(!userDisliked.getVideoDisliked().contains(videoEntity) && !userDisliked.getVideoLiked().contains(videoEntity)) {
            userDisliked.getVideoDisliked().add(videoEntity);
            videoEntity.setDislikes(videoEntity.getDislikes() - 1);
        }
        else{
            userDisliked.getVideoDisliked().remove(videoEntity);
            videoEntity.setDislikes(videoEntity.getDislikes() - 1);
        }
        userRepository.saveAndFlush(userDisliked);
        videoRepository.saveAndFlush(videoEntity);
    }
}
