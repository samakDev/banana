package org.samak.banana.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.samak.banana.dto.model.Plush;
import org.samak.banana.dto.model.PlushLocker;
import org.samak.banana.dto.model.PlushState;
import org.samak.banana.entity.PlushEntity;
import org.samak.banana.entity.PlushLockerEntity;
import org.samak.banana.entity.PlushStateEnumEntity;

import java.util.Objects;
import java.util.Optional;

@Mapper
public interface PlushMapper {

    default Plush convertPlushEntityToDto(PlushEntity plushEntity, PlushLockerEntity lockerEntity) {
        final Optional<Plush.Builder> plushBuilderOpt = getPlushBuilder(plushEntity);

        return plushBuilderOpt
                .map(plushBuilder -> plushBuilder.setPlushLocker(convertPlushLockerEntityToDto(lockerEntity))
                        .build())
                .orElse(null);
    }

    default Plush convertPlushEntityToDto(PlushEntity plushEntity) {
        return getPlushBuilder(plushEntity)
                .map(Plush.Builder::build)
                .orElse(null);
    }

    private Optional<Plush.Builder> getPlushBuilder(final PlushEntity plushEntity) {
        if (Objects.isNull(plushEntity) || Objects.isNull(plushEntity.getId())) {
            return Optional.empty();
        }

        return Optional.of(Plush.newBuilder()
                .setId(plushEntity.getId().toString())
                .setClawMachineId(plushEntity.getClawMachine().getId().toString())
                .setName(plushEntity.getName())
                .setImageAbsolutePath(plushEntity.getImageAbsolutePath())
                .setOrder(plushEntity.getOrder())
                .setState(convertStateEntityToDto(plushEntity.getState())));
    }

    default PlushLocker convertPlushLockerEntityToDto(PlushLockerEntity lockerEntity) {
        return Optional.ofNullable(lockerEntity)
                .map(PlushLockerEntity::getLockDate)
                .map(dateTime -> Timestamp.newBuilder()
                        .setSeconds(dateTime.getSecond())
                        .setNanos(dateTime.getNano()))
                .map(timeStamp -> PlushLocker.newBuilder()
                        .setName(lockerEntity.getName())
                        .setSince(timeStamp)
                        .build())
                .orElse(null);
    }

    PlushState convertStateEntityToDto(PlushStateEnumEntity state);

}
