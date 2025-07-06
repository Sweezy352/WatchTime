package kg.sweezy.watchtime.service;

public interface MediaBaseService {
    public void likeMediaById(Long id);
    public void dislikeMediaById(Long id);
    public void addToPlayList(Long id);
    public void removeFromPlayList(Long id);
    public void addMediaToHistory(Long id);
}
