package org.samak.banana.services.clawmachine;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.apache.logging.log4j.util.Strings;
import org.mapstruct.factory.Mappers;
import org.samak.banana.dto.ClawMachineEvent;
import org.samak.banana.dto.CreatedClawMachineEvent;
import org.samak.banana.dto.CurrentStateClawMachineEvent;
import org.samak.banana.dto.DeletedClawMachineEvent;
import org.samak.banana.dto.UpdatedClawMachineEvent;
import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.mapper.ClawMachineMapper;
import org.samak.banana.repository.ClawMachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class ClawMachineService implements IClawMachineService {
    private static final ClawMachineMapper CLAW_MACHINE_MAPPER = Mappers.getMapper(ClawMachineMapper.class);

    private final ClawMachineRepository clawMachineRepository;
    private final Subject<ClawMachineEvent> clawMachineEventSubject = PublishSubject.create();

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

        final ClawMachineEntity clawMachineEntity = clawMachineRepository.save(entity);

        final ClawMachineEvent createEvent = ClawMachineEvent.newBuilder()
                .setCreateClawMachineEvent(CreatedClawMachineEvent.newBuilder()
                        .setClawMachine(CLAW_MACHINE_MAPPER.convertClawMachineEntityToDto(clawMachineEntity)))
                .build();

        clawMachineEventSubject.onNext(createEvent);

        return clawMachineEntity.getId();
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
                .map(clawMachineRepository::save)
                .map(entity -> {
                    final ClawMachineEvent updateEvent = ClawMachineEvent.newBuilder()
                            .setUpdatedClawMachineEvent(UpdatedClawMachineEvent.newBuilder()
                                    .setClawMachine(CLAW_MACHINE_MAPPER.convertClawMachineEntityToDto(entity)))
                            .build();

                    clawMachineEventSubject.onNext(updateEvent);

                    return entity;
                });
    }

    @Override
    public void deleteClawMachine(final UUID clawMachineId) {
        clawMachineRepository.deleteById(clawMachineId);

        clawMachineEventSubject.onNext(ClawMachineEvent.newBuilder()
                .setDeletedClawMachineEvent(DeletedClawMachineEvent.newBuilder()
                        .setClawMachineId(clawMachineId.toString()))
                .build());
    }

    @Override
    public Observable<ClawMachineEvent> getStream() {
        return Observable.concat(initState(), clawMachineEventSubject)
                .observeOn(Schedulers.computation());
    }

    private Observable<ClawMachineEvent> initState() {
        return Observable.fromCallable(clawMachineRepository::findAll)
                .map(clawMachineEntities -> StreamSupport.stream(clawMachineEntities.spliterator(), false)
                        .map(CLAW_MACHINE_MAPPER::convertClawMachineEntityToDto)
                        .toList())
                .map(clawMachines -> ClawMachineEvent.newBuilder()
                        .setCurrentStateClawMachineEvent(CurrentStateClawMachineEvent.newBuilder()
                                .addAllClawMachines(clawMachines))
                        .build())
                .subscribeOn(Schedulers.io());
    }
}
