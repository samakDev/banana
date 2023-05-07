package org.samak.banana.services.clawmachine;

import org.samak.banana.entity.ClawMachineEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClawMachineService {
    UUID create(String name, Integer order);

    List<UUID> getAll();

    Optional<ClawMachineEntity> getClawMachine(UUID clawMachineId);

    Optional<ClawMachineEntity> updateClawMachine(UUID clawMachineId, String name, Integer order);

    void deleteClawMachine(UUID clawMachineId);
}
