package org.samak.banana.services.clawmachine;

import org.apache.logging.log4j.util.Strings;
import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.repository.ClawMachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

        if (Objects.isNull(order)) {
            entity.setOrder(Integer.MAX_VALUE);
        } else {
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

    @Override
    public Optional<ClawMachineEntity> getClawMachine(final UUID clawMachineId) {
        return clawMachineRepository.findById(clawMachineId);
    }

    @Override
    public Optional<ClawMachineEntity> updateClawMachine(final UUID clawMachineId, final String name, final Integer order) {
        final Optional<ClawMachineEntity> clawMachineOpt = getClawMachine(clawMachineId);

        if (clawMachineOpt.isEmpty()) {
            return Optional.empty();
        }

        return clawMachineOpt
                .map(clawMachineEntity -> {
                    Optional.ofNullable(name)
                            .map(String::trim)
                            .filter(tempName -> !Strings.isBlank(tempName))
                            .ifPresent(clawMachineEntity::setName);

                    if (Objects.nonNull(order) && order != clawMachineEntity.getOrder()) {
                        clawMachineEntity.setOrder(order);
                    }

                    return clawMachineEntity;
                })
                .map(clawMachineRepository::save);
    }
}
