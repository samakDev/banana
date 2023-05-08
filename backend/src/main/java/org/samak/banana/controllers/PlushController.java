package org.samak.banana.controllers;

import org.apache.logging.log4j.util.Strings;
import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;
import org.samak.banana.dto.model.Plush;
import org.samak.banana.dto.model.PlushIdentifier;
import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.services.clawmachine.IClawMachineService;
import org.samak.banana.services.plush.IPlushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/claw-machine/{claw-machine-id}/plush")
public class PlushController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlushController.class);

    private final IClawMachineService clawMachineService;
    private final IPlushService plushService;

    public PlushController(final IClawMachineService clawMachineService, final IPlushService plushService) {
        this.clawMachineService = clawMachineService;
        this.plushService = plushService;
    }

    @PostMapping(value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlushIdentifier> create(@PathVariable("claw-machine-id") final UUID clawMachineId,
                                                  @RequestParam("metadata") final Plush plush,
                                                  @RequestParam("plushImg") final MultipartFile plushImg) {
        LOGGER.info("PlushController.create {} for clawMachine {}", plush, clawMachineId);

        if (Strings.isBlank(plush.getName())) {
            throw new IllegalArgumentException("Name and Image are required");
        }

        final Optional<ClawMachineEntity> clawMachineOpt = clawMachineService.getClawMachine(clawMachineId);

        if (clawMachineOpt.isEmpty()) {
            throw new IllegalArgumentException("no ClawMachine found for this id " + clawMachineId);
        }

        final Integer order = plush.hasOrder() ? plush.getOrder() : null;

        final UUID plushId = plushService.create(clawMachineOpt.get(), plush.getName(), order, plushImg);

        final PlushIdentifier identifier = PlushIdentifier.newBuilder()
                .setId(plushId.toString())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(identifier);
    }

    @PostMapping(value = "/take/{key}")
    public ResponseEntity<Boolean> take(@PathVariable("id") final String id, @PathVariable("key") final String key) {
        final User user = new User();
        user.setId(key);
        user.setName(key);

        final boolean result = plushService.take(user, id);

        if (result) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(value = "/release/{key}")
    public ResponseEntity<Boolean> release(@PathVariable("id") final String id, @PathVariable("key") final String key) {
        final User user = new User();
        user.setId(key);
        user.setName(key);

        final boolean result = plushService.release(user, id);

        if (result) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping(value = "/status")
    public ResponseEntity<PlushState> status(@PathVariable("id") final String id) {
        return plushService.getState(id)
                .map(plush -> new ResponseEntity<>(plush, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));

    }

}
