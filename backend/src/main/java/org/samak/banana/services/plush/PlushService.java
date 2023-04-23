package org.samak.banana.services.plush;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.samak.banana.domain.plush.IPlushConfig;
import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class PlushService implements IPlushService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlushService.class);

    private final Map<String, PlushState> plushStates = new HashMap<>();
    private final Subject<PlushState> stateSubject = PublishSubject.create();

    @Autowired
    private IPlushConfig plushConfig;

    @PostConstruct
    private void init() {
        LOGGER.info("init Plush : {}", plushConfig);

        plushConfig.getPlushs()
                .forEach(p -> {
                    final PlushState state = new PlushState();
                    state.setPlush(p);

                    plushStates.put(p.getId(), state);
                });
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
