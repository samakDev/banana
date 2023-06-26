package org.samak.banana.services.plush;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.apache.commons.io.FilenameUtils;
import org.mapstruct.factory.Mappers;
import org.samak.banana.dto.message.CreatedPlushEvent;
import org.samak.banana.dto.message.CurrentStatePlushEvent;
import org.samak.banana.dto.message.DeletedPlushEvent;
import org.samak.banana.dto.message.LockPlushEvent;
import org.samak.banana.dto.message.PlushEvent;
import org.samak.banana.dto.message.UnLockPlushEvent;
import org.samak.banana.dto.message.UpdatedPlushEvent;
import org.samak.banana.dto.model.Plush;
import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.entity.PlushEntity;
import org.samak.banana.entity.PlushLockerEntity;
import org.samak.banana.entity.PlushStateEnumEntity;
import org.samak.banana.importplush.Plushes;
import org.samak.banana.mapper.PlushMapper;
import org.samak.banana.repository.PlushLockerRepository;
import org.samak.banana.repository.PlushRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

@Service
public class PlushService implements IPlushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlushService.class);
    private static final PlushMapper PLUSH_MAPPER = Mappers.getMapper(PlushMapper.class);

    private final IFileStoreService fileStoreService;
    private final PlushRepository plushRepository;
    private final PlushLockerRepository plushLockerRepository;
    private final Subject<PlushEvent> plushEventSubject = PublishSubject.create();

    public PlushService(final IFileStoreService fileStoreService, final PlushRepository plushRepository, final PlushLockerRepository plushLockerRepository) {
        this.fileStoreService = fileStoreService;
        this.plushRepository = plushRepository;
        this.plushLockerRepository = plushLockerRepository;
    }

    @Override
    public UUID create(final ClawMachineEntity clawMachineEntity, final String name, @Nullable final Integer order, final MultipartFile plushImg) {
        final String fileAbsolutePath = saveImg(plushImg);

        return create(clawMachineEntity, name, order, fileAbsolutePath);
    }

    private UUID create(final ClawMachineEntity clawMachineEntity, final String name, @Nullable final Integer order, final String fileAbsolutePath) {
        final PlushEntity entity = new PlushEntity();
        entity.setClawMachine(clawMachineEntity);
        entity.setName(name);
        entity.setImageAbsolutePath(fileAbsolutePath);
        if (Objects.nonNull(order)) {
            entity.setOrder(order);
        }
        entity.setState(PlushStateEnumEntity.FREE);

        final PlushEntity saved = plushRepository.save(entity);

        final PlushEvent createdEvent = PlushEvent.newBuilder()
                .setCreatePlushEvent(CreatedPlushEvent.newBuilder()
                        .setPlush(PLUSH_MAPPER.convertPlushEntityToDto(entity)))
                .build();

        plushEventSubject.onNext(createdEvent);

        return saved.getId();
    }

    @Override
    public PlushEntity updatePlush(final PlushEntity originalPlush, @Nullable final String name, @Nullable final Integer order, @Nullable final MultipartFile plushImg) throws IOException {
        final String newImgPath = Objects.nonNull(plushImg)
                ? this.saveImg(plushImg)
                : null;

        final String originalImagePath;

        if (Objects.nonNull(newImgPath)) {
            originalImagePath = originalPlush.getImageAbsolutePath();
            originalPlush.setImageAbsolutePath(newImgPath);
        } else {
            originalImagePath = null;
        }

        if (Objects.nonNull(name)) {
            originalPlush.setName(name);
        }

        if (Objects.nonNull(order)) {
            originalPlush.setOrder(order);
        }

        final PlushEntity updated = plushRepository.save(originalPlush);

        if (Objects.nonNull(originalImagePath)) {
            this.fileStoreService.delete(originalImagePath);
        }

        final PlushEvent updatedEvent = PlushEvent.newBuilder()
                .setUpdatedPlushEvent(UpdatedPlushEvent.newBuilder()
                        .setPlush(PLUSH_MAPPER.convertPlushEntityToDto(updated)))
                .build();

        plushEventSubject.onNext(updatedEvent);

        return updated;
    }

    private String saveImg(final MultipartFile plushImg) {
        try {
            final String imageStoreAbsolutePath = fileStoreService.store(plushImg.getOriginalFilename(), plushImg.getInputStream());

            LOGGER.info("image saved {}", imageStoreAbsolutePath);

            return imageStoreAbsolutePath;
        } catch (IOException e) {
            throw new RuntimeException("impossible to read file", e);
        }
    }

    @Override
    public List<PlushEntity> getAll(final ClawMachineEntity clawMachineEntity) {
        return plushRepository.findAllByClawMachine(clawMachineEntity);
    }

    @Override
    public Optional<PlushEntity> getPlushMetadata(final UUID plushId) {
        return plushRepository.findById(plushId);
    }

    @Override
    public InputStream getPlushImg(final PlushEntity plushEntity) throws FileNotFoundException {
        return fileStoreService.fetch(plushEntity.getImageAbsolutePath());
    }

    @Override
    public boolean lock(final UUID plushId, final PlushEntity plushEntity, final String lockerName, @Nullable final OffsetDateTime lockDate) {
        final PlushLockerEntity entity = new PlushLockerEntity();
        entity.setName(lockerName);
        entity.setPlush(plushEntity);
        entity.setLockDate(Optional.ofNullable(lockDate)
                .orElseGet(OffsetDateTime::now));

        plushLockerRepository.save(entity);

        plushEntity.setState(PlushStateEnumEntity.TAKEN);
        plushRepository.save(plushEntity);

        final PlushEvent event = PlushEvent.newBuilder()
                .setLockPlushEvent(LockPlushEvent.newBuilder()
                        .setPlushId(plushEntity.getId().toString())
                        .setPlushLocker(PLUSH_MAPPER.convertPlushLockerEntityToDto(entity)))
                .build();

        plushEventSubject.onNext(event);

        return true;
    }

    @Override
    public boolean hasRightToUnlock(final PlushEntity plushEntity, final String name) {
        if (PlushStateEnumEntity.TAKEN != plushEntity.getState()) {
            LOGGER.error("the plush {} is not taken", plushEntity);

            return false;
        }

        final Optional<PlushLockerEntity> lockerOpt = plushLockerRepository.findAllByPlushAndUnlockDate(plushEntity, null);

        if (lockerOpt.isEmpty()) {
            LOGGER.error("State error no locker found for {} but state is TAKEN, should not append", plushEntity);
            plushEntity.setState(PlushStateEnumEntity.FREE);
            plushRepository.save(plushEntity);
            return false;
        }

        if (name.isEmpty()) {
            return false;
        }

        return "admin".equalsIgnoreCase(name) || lockerOpt.get().getName().equalsIgnoreCase(name);
    }

    @Override
    public void unlock(final PlushEntity plushEntity) {
        final PlushLockerEntity locker = plushLockerRepository.findAllByPlushAndUnlockDate(plushEntity, null)
                .orElseThrow(() -> new RuntimeException("dev should use IPlushService#hasRightToUnlock before calling this"));

        locker.setUnlockDate(OffsetDateTime.now());

        plushLockerRepository.save(locker);

        plushEntity.setState(PlushStateEnumEntity.FREE);
        plushRepository.save(plushEntity);

        final PlushEvent event = PlushEvent.newBuilder()
                .setUnLockPlushEvent(UnLockPlushEvent.newBuilder()
                        .setPlushId(plushEntity.getId().toString()))
                .build();

        plushEventSubject.onNext(event);
    }

    @Override
    public void delete(final UUID plushId) throws IOException {
        final Optional<PlushEntity> plush = getPlushMetadata(plushId);
        if (plush.isPresent()) {
            final PlushEntity plushEntity = plush.get();

            final File file = new File(plushEntity.getImageAbsolutePath());
            if (file.exists()) {
                fileStoreService.delete(plushEntity.getImageAbsolutePath());
            }
        }

        plushRepository.deleteById(plushId);

        final PlushEvent deletedEvent = PlushEvent.newBuilder()
                .setDeletedPlushEvent(DeletedPlushEvent.newBuilder()
                        .setPlushId(plushId.toString()))
                .build();

        plushEventSubject.onNext(deletedEvent);
    }

    @Override
    public boolean importBananaConfig(final ClawMachineEntity clawMachineEntity, final MultipartFile jsonFile, @Nullable final String homeDirectory) throws IOException {
        final Plushes plushes = getIPlushConfig(jsonFile);

        final AtomicBoolean allSucceed = new AtomicBoolean(true);

        plushes.getPlushs()
                .stream()
                .map(plush -> {
                    final String original = plush.getImg();

                    final File imgFile = new File(homeDirectory, original);

                    if (imgFile.exists() && imgFile.isFile()) {
                        try {
                            final String filePath = fileStoreService.store(FilenameUtils.getName(original), new FileInputStream(imgFile));

                            return Optional.of(Map.entry(plush.getName(), filePath));
                        } catch (IOException e) {
                            LOGGER.error("Une erreur est surevenue lors de l'import de filePath ", e);
                            allSucceed.set(false);
                            return Optional.<Map.Entry<String, String>>empty();
                        }
                    } else {
                        allSucceed.set(false);
                        return Optional.of(Map.entry(plush.getName(), ""));
                    }

                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(plush -> create(clawMachineEntity, plush.getKey(), 0, plush.getValue()));

        return allSucceed.get();
    }

    private Plushes getIPlushConfig(final MultipartFile jsonFile) throws IOException {
        try (InputStream fileContentStream = jsonFile.getInputStream()) {
            return new ObjectMapper().readValue(fileContentStream, Plushes.class);
        }
    }

    @Override
    public Observable<PlushEvent> getStream() {
        return Observable.concat(initState(), plushEventSubject);
    }

    private Observable<PlushEvent> initState() {
        return Observable.fromCallable(plushRepository::findAll)
                .map(plushEntities -> {
                    return StreamSupport.stream(plushEntities.spliterator(), false)
                            .map(this::converter)
                            .toList();
                })
                .map(plushes -> PlushEvent.newBuilder()
                        .setCurrentStatePlushEvent(CurrentStatePlushEvent.newBuilder()
                                .addAllPlushes(plushes))
                        .build())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Plush converter(final PlushEntity plush) {
        if (PlushStateEnumEntity.FREE == plush.getState()) {
            return PLUSH_MAPPER.convertPlushEntityToDto(plush);
        } else {
            final Optional<PlushLockerEntity> plushLockerEntityOpt = plushLockerRepository.findAllByPlushAndUnlockDate(plush, null);

            return plushLockerEntityOpt
                    .map(lockerEntity -> PLUSH_MAPPER.convertPlushEntityToDto(plush, lockerEntity))
                    .orElseGet(() -> PLUSH_MAPPER.convertPlushEntityToDto(plush));
        }
    }
}
