package org.samak.banana.repository;

import org.samak.banana.entity.PlushEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlushRepository extends CrudRepository<PlushEntity, UUID> {
}
