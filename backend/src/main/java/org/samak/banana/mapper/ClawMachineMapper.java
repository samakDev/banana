package org.samak.banana.mapper;

import org.mapstruct.Mapper;
import org.samak.banana.dto.ClawMachine;
import org.samak.banana.entity.ClawMachineEntity;

@Mapper
public interface ClawMachineMapper {

    default ClawMachine convertClawMachineEntityToDto(ClawMachineEntity clawMachineEntity) {
        return ClawMachine.newBuilder()
                .setId(clawMachineEntity.getId().toString())
                .setName(clawMachineEntity.getName())
                .setOrder(clawMachineEntity.getOrder())
                .build();
    }
}
