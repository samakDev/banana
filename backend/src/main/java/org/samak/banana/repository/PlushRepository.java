package org.samak.banana.repository;

import org.samak.banana.entity.PlushEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlushRepository extends CrudRepository<PlushEntity, UUID> {
}
