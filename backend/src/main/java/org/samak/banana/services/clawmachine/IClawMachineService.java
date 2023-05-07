package org.samak.banana.services.clawmachine;

import io.reactivex.Observable;
import org.samak.banana.dto.ClawMachineEvent;
import org.samak.banana.entity.ClawMachineEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClawMachineService {
    UUID create(String name, Integer order);

    List<UUID> getAll();

    Optional<ClawMachineEntity> getClawMachine(UUID clawMachineId);

    Optional<ClawMachineEntity> updateClawMachine(UUID clawMachineId, String name, Integer order);

    Observable<ClawMachineEvent> getStream();

    void deleteClawMachine(UUID clawMachineId);
}
