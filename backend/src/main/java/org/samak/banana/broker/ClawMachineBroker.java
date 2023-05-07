package org.samak.banana.broker;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.samak.banana.services.clawmachine.IClawMachineService;
import org.samak.banana.utils.RxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClawMachineBroker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClawMachineBroker.class);

    private final SimpMessageSendingOperations messagingTemplate;
    private final IClawMachineService clawMachineService;
    private final Map<UUID, Disposable> listeningMap;

    public ClawMachineBroker(final SimpMessageSendingOperations messagingTemplate, final IClawMachineService clawMachineService) {
        this.messagingTemplate = messagingTemplate;
        this.clawMachineService = clawMachineService;
        listeningMap = new ConcurrentHashMap<>();
    }

    public void listenClawService(final UUID sessionId) {
        final Disposable disposable = this.clawMachineService.getStream()
                .observeOn(Schedulers.io())
                .subscribe(clawMachineEvent -> {
                    LOGGER.info("Send clawMachineEvent : {}", clawMachineEvent);
                    messagingTemplate.convertAndSend("/banana/claw-machine", clawMachineEvent.toByteArray());
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
