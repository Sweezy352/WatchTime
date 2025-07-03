package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.entity.VideoEntity;

public interface MediaService {
    public String subscribeByChannelId(Long channelId);
    public void viewVideoById(Long videoId);
    public void likeVideoById(Long videoId);
    public void dislikeVideoById(Long videoId);
}
