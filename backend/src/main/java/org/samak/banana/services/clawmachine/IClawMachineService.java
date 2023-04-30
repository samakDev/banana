package org.samak.banana.services.clawmachine;

import java.util.UUID;

public interface IClawMachineService {
    UUID create(String name, Integer order);
}
