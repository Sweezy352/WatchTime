package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.entity.VideoEntity;

public interface MediaService {
    public String subscribeByChannelId(Long channelId);
    public void viewVideoByEntity(VideoEntity videoEntity);
    public void likeVideoById(Long videoId);
    public void dislikeVideoById(Long videoId);
    public void addToPlayList(Long videoId);
    public void removeFromPlayList(Long videoId);
    public void addVideoToHistory(VideoEntity videoEntity);
}
