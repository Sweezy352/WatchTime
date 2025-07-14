package kg.sweezy.watchtime.repository;

import kg.sweezy.watchtime.entity.PreviewVideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreviewVideoRepository extends JpaRepository<PreviewVideoEntity, Long> {
}
