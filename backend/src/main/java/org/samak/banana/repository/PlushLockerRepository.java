package org.samak.banana.repository;

import org.samak.banana.entity.PlushLockerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlushLockerRepository extends CrudRepository<PlushLockerEntity, UUID> {
}
