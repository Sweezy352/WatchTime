package kg.sweezy.watchtime.repository;

import kg.sweezy.watchtime.entity.VideoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    List<VideoEntity> findByTitleStartingWith(String title);
    List<VideoEntity> findByIdGreaterThanOrderByIdAsc(Long afterId, Pageable pageable);
}
