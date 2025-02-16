package org.samak.banana.repository;

import org.samak.banana.entity.ClawMachineEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClawMachineRepository extends CrudRepository<ClawMachineEntity, UUID> {
}
