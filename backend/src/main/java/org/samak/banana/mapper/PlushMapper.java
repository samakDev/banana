package org.samak.banana.mapper;

import org.mapstruct.Mapper;
import org.samak.banana.dto.model.Plush;
import org.samak.banana.dto.model.PlushState;
import org.samak.banana.entity.PlushEntity;
import org.samak.banana.entity.PlushStateEnumEntity;

import java.util.Objects;

@Mapper
public interface PlushMapper {

    default Plush convertPlushEntityToDto(PlushEntity plushEntity) {
        if (Objects.isNull(plushEntity) || Objects.isNull(plushEntity.getId())) {
            return null;
        }

        return Plush.newBuilder()
                .setId(plushEntity.getId().toString())
                .setClawMachineId(plushEntity.getClawMachine().getId().toString())
                .setName(plushEntity.getName())
                .setImageAbsolutePath(plushEntity.getImageAbsolutePath())
                .setOrder(plushEntity.getOrder())
                .setState(convertStateEntityToDto(plushEntity.getState()))
                .build();
    }

    PlushState convertStateEntityToDto(PlushStateEnumEntity state);
}
