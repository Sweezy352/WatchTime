package kg.sweezy.watchtime.repository;

import kg.sweezy.watchtime.entity.ProfilePictureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePictureRepository extends JpaRepository<ProfilePictureEntity, Long> {
}
