package org.samak.banana.mapper;

import org.mapstruct.Mapper;
import org.samak.banana.dto.model.ClawMachine;
import org.samak.banana.entity.ClawMachineEntity;

import java.util.Objects;

@Mapper
public interface ClawMachineMapper {

    default ClawMachine convertClawMachineEntityToDto(ClawMachineEntity clawMachineEntity) {
        if (Objects.isNull(clawMachineEntity) || Objects.isNull(clawMachineEntity.getId())) {
            return null;
        }

        return ClawMachine.newBuilder()
                .setId(clawMachineEntity.getId().toString())
                .setName(clawMachineEntity.getName())
                .setOrder(clawMachineEntity.getOrder())
                .build();
    }
}
