package kg.sweezy.watchtime.repository;

import kg.sweezy.watchtime.entity.UuIdCodeEntity;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UuidCodeRepository extends KeyValueRepository<UuIdCodeEntity, Long> {
    Optional<UuIdCodeEntity> findByCode(String code);
}
