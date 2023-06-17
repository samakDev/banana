package org.samak.banana.services.plush;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.samak.banana.domain.plush.IPlushConfig;
import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;
import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.entity.PlushEntity;
import org.samak.banana.entity.PlushLockerEntity;
import org.samak.banana.entity.PlushStateEnumEntity;
import org.samak.banana.repository.PlushLockerRepository;
import org.samak.banana.repository.PlushRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlushService implements IPlushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlushService.class);

    private final Map<String, PlushState> plushStates = new HashMap<>();
    private final Subject<PlushState> stateSubject = PublishSubject.create();
    private final IFileStoreService fileStoreService;
    private final PlushRepository plushRepository;
    private final PlushLockerRepository plushLockerRepository;

    public PlushService(final IPlushConfig plushConfig, final IFileStoreService fileStoreService, final PlushRepository plushRepository, final PlushLockerRepository plushLockerRepository) {
        this.fileStoreService = fileStoreService;
        this.plushRepository = plushRepository;
        this.plushLockerRepository = plushLockerRepository;
        LOGGER.info("Plush Service Constructor : {}", plushConfig);

        plushConfig.getPlushs()
                .forEach(p -> {
                    final PlushState state = new PlushState();
                    state.setPlush(p);

                    plushStates.put(p.getId(), state);
                });
    }

    @Override
    public UUID create(final ClawMachineEntity clawMachineEntity, final String name, @Nullable final Integer order, final MultipartFile plushImg) {
        final String fileAbsolutePath = saveImg(plushImg);

        final PlushEntity entity = new PlushEntity();
        entity.setClawMachine(clawMachineEntity);
        entity.setName(name);
        entity.setImageAbsolutePath(fileAbsolutePath);
        if (Objects.nonNull(order)) {
            entity.setOrder(order);
        }
        entity.setState(PlushStateEnumEntity.FREE);

        final PlushEntity saved = plushRepository.save(entity);

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

        if (Objects.nonNull(originalPlush)) {
            this.fileStoreService.delete(originalImagePath);
        }

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
    public boolean take(final UUID plushId, final PlushEntity plushEntity, final String lockerName, @Nullable final OffsetDateTime lockDate) {
        final PlushLockerEntity entity = new PlushLockerEntity();
        entity.setName(lockerName);
        entity.setPlush(plushEntity);
        entity.setLockDate(Optional.ofNullable(lockDate)
                .orElseGet(OffsetDateTime::now));

        plushLockerRepository.save(entity);

        plushEntity.setState(PlushStateEnumEntity.TAKEN);
        plushRepository.save(plushEntity);

        return true;
    }

    @Override
    public void delete(final UUID plushId) throws IOException {
        final Optional<PlushEntity> plush = getPlushMetadata(plushId);
        if (plush.isPresent()) {
            fileStoreService.delete(plush.get().getImageAbsolutePath());
        }

        plushRepository.deleteById(plushId);
    }

    @Override
    public boolean release(final User user, final String plushId) {
        final PlushState state = plushStates.get(plushId);

        if (state != null && (user.equals(state.getOwner()) || user.getId().equals("admin"))) {
            state.setOwner(null);
            stateSubject.onNext(state);
            return true;
        }
        return false;
    }

    @Override
    public Observable<PlushState> getStream() {
        return stateSubject.startWith(plushStates.values());
    }

    @Override
    public List<PlushState> getStates() {
        return new ArrayList<>(plushStates.values());

    }

    @Override
    public Optional<PlushState> getState(String id) {
        return Optional.ofNullable(plushStates.get(id));
    }

}
