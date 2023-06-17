package org.samak.banana.repository;

import org.samak.banana.entity.PlushEntity;
import org.samak.banana.entity.PlushLockerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlushLockerRepository extends CrudRepository<PlushLockerEntity, UUID> {
    Optional<PlushLockerEntity> findAllByPlushAndUnlockDate(PlushEntity plushEntity, OffsetDateTime offsetDateTime);
}
