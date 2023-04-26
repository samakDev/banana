package org.samak.banana.broker;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.services.plush.IPlushService;
import org.samak.banana.utils.RxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class PlushBroker {
    public static final UUID DEFAULT_SESSION_ID = UUID.fromString("00000000-0000-0000-0000-00000000000");

    private static final Logger LOGGER = LoggerFactory.getLogger(PlushBroker.class);

    private final IPlushService plushService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final Map<UUID, Disposable> listeningMap;

    public PlushBroker(final IPlushService plushService, final SimpMessageSendingOperations messagingTemplate) {
        this.plushService = plushService;
        this.messagingTemplate = messagingTemplate;
        listeningMap = new ConcurrentHashMap<>();
    }

    public void listenPlushService(final UUID sessionId) {
        final Disposable disposable = this.plushService.getStream()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .subscribe(state -> {
                    LOGGER.info("Send state : {}", state);
                    this.messagingTemplate.convertAndSend("/plush/states", state);
                }, RxUtils.logError(LOGGER));

        listeningMap.compute(sessionId, (k, v) -> {
            if (DEFAULT_SESSION_ID.equals(k)) {
                listeningMap.remove(k).dispose();
            }

            return disposable;
        });
    }

    public void stopListening(final UUID sessionId) {
        final Disposable disposable = listeningMap.remove(sessionId);

        if (Objects.nonNull(disposable)) {
            disposable.dispose();
        }
    }

    @SubscribeMapping("/plush/states")
    public List<PlushState> getStates() {
        LOGGER.info("getStates");
        return plushService.getStates();
    }

    @MessageMapping("/plush/take")
    public void take(final PlushState state) {
        LOGGER.info("take {}", state);
        plushService.take(state.getOwner(), state.getPlush().getId());
    }

    @MessageMapping("/plush/release")
    public void release(final PlushState state) {
        LOGGER.info("release {}", state);
        plushService.release(state.getOwner(), state.getPlush().getId());
    }
}
