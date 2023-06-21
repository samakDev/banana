package org.samak.banana.broker;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.samak.banana.services.plush.IPlushService;
import org.samak.banana.utils.RxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class PlushBroker {

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
                .observeOn(Schedulers.io())
                .subscribe(plushEvent -> {
                    LOGGER.info("Send plushEvent : {}", plushEvent);
                    this.messagingTemplate.convertAndSend("/banana/plush", plushEvent.toByteArray());
                }, RxUtils.logError(LOGGER));

        listeningMap.compute(sessionId, (k, v) -> {
            if (WebSocketEventListener.DEFAULT_SESSION_ID.equals(k)) {
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
}
