package org.samak.banana.broker;

import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.services.plush.IPlushService;
import org.samak.banana.utils.RxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;


@Controller
public class PlushBroker {

    private final Logger LOGGER = LoggerFactory.getLogger(PlushBroker.class);

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private IPlushService plushService;

    public PlushBroker() {
    }

    @PostConstruct
    private void init() {
        plushService.getStream().subscribe(state -> {
            LOGGER.info("Send state : {}", state);
            template.convertAndSend("/plush/states", Arrays.asList(state));
        }, RxUtils.logError(LOGGER));
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
