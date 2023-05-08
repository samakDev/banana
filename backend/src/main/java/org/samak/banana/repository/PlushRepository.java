package org.samak.banana.repository;

import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.entity.PlushEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PlushRepository extends CrudRepository<PlushEntity, UUID> {

    List<PlushEntity> findAllByClawMachine(ClawMachineEntity clawMachine);
}
