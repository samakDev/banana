package org.samak.banana.services.plush;


import io.reactivex.Observable;
import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;
import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.entity.PlushEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPlushService {

    boolean take(User user, String plushId);

    boolean release(User user, String plushId);

    Observable<PlushState> getStream();

    List<PlushState> getStates();

    Optional<PlushState> getState(String id);


    UUID create(ClawMachineEntity clawMachineEntity, String name, @Nullable Integer order, MultipartFile plushImg);

    List<PlushEntity> getAll(ClawMachineEntity clawMachineEntity);
}
