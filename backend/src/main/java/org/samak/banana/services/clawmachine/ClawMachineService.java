package org.samak.banana.services.clawmachine;

import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.repository.ClawMachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class ClawMachineService implements IClawMachineService {

    private final ClawMachineRepository clawMachineRepository;

    public ClawMachineService(final ClawMachineRepository clawMachineRepository) {
        this.clawMachineRepository = clawMachineRepository;
    }

    @Override
    public UUID create(final String name, final Integer order) {
        final ClawMachineEntity entity = new ClawMachineEntity();
        entity.setName(name);

        if (Objects.nonNull(order)) {
            entity.setOrder(order);
        }

        return clawMachineRepository.save(entity).getId();
    }

    @Override
    public List<UUID> getAll() {
        return StreamSupport.stream(clawMachineRepository.findAll().spliterator(), false)
                .map(ClawMachineEntity::getId)
                .toList();
    }
}
