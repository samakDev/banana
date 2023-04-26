package org.samak.banana.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Optional;
import java.util.UUID;

import static org.samak.banana.config.WebSocketBrokerConfiguration.TOPIC_PREFIX;

@Component
public class WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final PlushBroker plushBroker;

    public WebSocketEventListener(final PlushBroker plushBroker) {
        this.plushBroker = plushBroker;
    }

    @EventListener
    public void handleSessionSubscribeEvent(final SessionSubscribeEvent event) {
        LOGGER.info("receive subscribe event");

        Optional.of(event.getMessage())
                .filter(GenericMessage.class::isInstance)
                .map(GenericMessage.class::cast)
                .map(message -> {
                    final UUID simpSessionId = Optional.ofNullable(message.getHeaders().get("simpSessionId"))
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .map(UUID::fromString)
                            .orElseGet(() -> PlushBroker.DEFAULT_SESSION_ID);


                    final String simpDestination = Optional.ofNullable(message.getHeaders().get("simpDestination"))
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .orElse("");

                    return new EventBrokerRecord(simpSessionId, simpDestination);
                })
                .filter(eventBroker -> eventBroker.destination().startsWith(TOPIC_PREFIX))
                .ifPresent(eventBrokerRecord -> plushBroker.listenPlushService(eventBrokerRecord.sessionId()));
    }

    @EventListener
    public void handleSessionDisconnectEvent(final SessionDisconnectEvent event) {
        LOGGER.info("receive disconnected event");

        plushBroker.stopListening(UUID.fromString(event.getSessionId()));
    }
}
