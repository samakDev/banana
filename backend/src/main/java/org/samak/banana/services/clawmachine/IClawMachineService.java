package org.samak.banana.services.clawmachine;

import io.reactivex.Observable;
import org.samak.banana.dto.message.ClawMachineEvent;
import org.samak.banana.entity.ClawMachineEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClawMachineService {
    UUID create(String name, @Nullable Integer order);

    List<UUID> getAll();

    Optional<ClawMachineEntity> getClawMachine(UUID clawMachineId);

    Optional<ClawMachineEntity> updateClawMachine(UUID clawMachineId, @Nullable String name, @Nullable Integer order);

    Observable<ClawMachineEvent> getStream();

    void deleteClawMachine(UUID clawMachineId);
}
