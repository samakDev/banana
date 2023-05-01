package org.samak.banana.services.clawmachine;

import java.util.List;
import java.util.UUID;

public interface IClawMachineService {
    UUID create(String name, Integer order);

    List<UUID> getAll();
}
