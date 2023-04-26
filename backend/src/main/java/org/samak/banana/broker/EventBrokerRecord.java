package org.samak.banana.broker;

import java.util.UUID;

public record EventBrokerRecord(UUID sessionId, String destination) {
}
