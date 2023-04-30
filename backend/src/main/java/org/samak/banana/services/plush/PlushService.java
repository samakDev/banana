package org.samak.banana.services.plush;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.samak.banana.domain.plush.IPlushConfig;
import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;
import org.samak.banana.entity.PlushEntity;
import org.samak.banana.repository.PlushRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class PlushService implements IPlushService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlushService.class);

    private final Map<String, PlushState> plushStates = new HashMap<>();
    private final Subject<PlushState> stateSubject = PublishSubject.create();
    private final PlushRepository plushRepository;

    public PlushService(final IPlushConfig plushConfig, final PlushRepository plushRepository) {
        this.plushRepository = plushRepository;

        plushConfig.getPlushs()
                .forEach(p -> {
                    final PlushState state = new PlushState();
                    state.setPlush(p);

                    plushStates.put(p.getId(), state);
                });
    }

    @Override
    public UUID createPlush(final String plushName, final String filename, final InputStream imgInputString) {
        //Save File

        // Save entity
        final PlushEntity entity = new PlushEntity();
        entity.setName(plushName);
        entity.setImg(filename);

        return plushRepository.save(entity).getId();
    }

    @Override
    public boolean take(final User user, final String plushId) {
        final PlushState state = plushStates.get(plushId);

        if (state != null && state.getOwner() == null && user.getId() != null) {
            state.setOwner(user);
            stateSubject.onNext(state);
            return true;
        }

        return false;
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
