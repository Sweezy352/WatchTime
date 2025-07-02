package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.exception.SubscribtionFailedException;
import kg.sweezy.watchtime.exception.UserNotFoundException;
import kg.sweezy.watchtime.repository.UserRepository;
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

    @Autowired
    public MediaServiceImpl(AuthService authService, UserRepository userRepository, ManageTranslation manageTranslation) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.manageTranslation = manageTranslation;
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
}
