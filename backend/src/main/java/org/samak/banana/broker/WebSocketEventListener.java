package org.samak.banana.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Optional;
import java.util.UUID;

@Component
public class WebSocketEventListener {
    public static final UUID DEFAULT_SESSION_ID = UUID.fromString("00000000-0000-0000-0000-00000000000");

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final ClawMachineBroker clawMachineBroker;
    private final PlushBroker plushBroker;

    public WebSocketEventListener(final ClawMachineBroker clawMachineBroker, final PlushBroker plushBroker) {
        this.clawMachineBroker = clawMachineBroker;
        this.plushBroker = plushBroker;
    }

    @EventListener
    public void handleSessionConnectEvent(final SessionConnectEvent event) {
        LOGGER.info("receive connect event");
        enableConnection(event);
    }

    @EventListener
    public void handleSessionSubscribeEvent(final SessionSubscribeEvent event) {
        LOGGER.info("receive subscribe event");

        enableConnection(event);
    }

    private void enableConnection(final AbstractSubProtocolEvent event) {
        Optional.of(event.getMessage())
                .filter(GenericMessage.class::isInstance)
                .map(GenericMessage.class::cast)
                .map(message -> Optional.ofNullable(message.getHeaders().get("simpSessionId"))
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .map(UUID::fromString)
                        .orElseGet(() -> DEFAULT_SESSION_ID))
                .ifPresent(sessionId -> {
                    clawMachineBroker.listenClawService(sessionId);
                    plushBroker.listenPlushService(sessionId);
                });
    }

    @EventListener
    public void handleSessionDisconnectEvent(final SessionDisconnectEvent event) {
        LOGGER.info("receive disconnected event");

        final UUID sessionId = UUID.fromString(event.getSessionId());

        clawMachineBroker.stopListening(sessionId);
        plushBroker.stopListening(sessionId);
    }
}
