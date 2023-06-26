package org.samak.banana.services.plush;


import io.reactivex.Observable;
import org.samak.banana.dto.message.PlushEvent;
import org.samak.banana.dto.model.Plush;
import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.entity.PlushEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPlushService {

    Observable<PlushEvent> getStream();

    UUID create(ClawMachineEntity clawMachineEntity, String name, @Nullable Integer order, MultipartFile plushImg);

    PlushEntity updatePlush(PlushEntity originalPlush, @Nullable String name, @Nullable Integer order, @Nullable MultipartFile plushImg) throws IOException;

    List<PlushEntity> getAll(ClawMachineEntity clawMachineEntity);

    Optional<PlushEntity> getPlushMetadata(UUID plushId);

    InputStream getPlushImg(PlushEntity plushEntity) throws FileNotFoundException;

    boolean lock(UUID plushId, final PlushEntity plushEntity, String lockerName, final OffsetDateTime lockDate);

    boolean hasRightToUnlock(final PlushEntity plushEntity, final String name);

    void unlock(PlushEntity plushEntity);

    void delete(UUID plushId) throws IOException;

    boolean importBananaConfig(final ClawMachineEntity clawMachineEntity, MultipartFile jsonFile, @Nullable final String homeDirectory) throws IOException;

    Plush converter(PlushEntity plush);
}
