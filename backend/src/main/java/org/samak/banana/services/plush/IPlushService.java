package org.samak.banana.services.plush;


import io.reactivex.Observable;
import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;

import java.util.List;
import java.util.Optional;

public interface IPlushService {

    boolean take(User user, String plushId);

    boolean release(User user, String plushId);

    Observable<PlushState> getStream();

    List<PlushState> getStates();

    Optional<PlushState> getState(String id);


}
