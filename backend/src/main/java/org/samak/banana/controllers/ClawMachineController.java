package org.samak.banana.controllers;

import org.apache.logging.log4j.util.Strings;
import org.samak.banana.dto.ClawMachine;
import org.samak.banana.dto.ClawMachineIdentifier;
import org.samak.banana.dto.ClawMachineIdentifiers;
import org.samak.banana.services.clawmachine.ClawMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/claw-machine")
public class ClawMachineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClawMachineController.class);
    private final ClawMachineService clawMachineService;

    public ClawMachineController(final ClawMachineService clawMachineService) {
        this.clawMachineService = clawMachineService;
    }

    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClawMachineIdentifier> create(
            @RequestBody() final ClawMachine clawMachine) {
        LOGGER.info("ClawMachineController.create {}", clawMachine);

        if (Strings.isBlank(clawMachine.getName())) {
            throw new IllegalArgumentException("Name required");
        }

        final UUID clawId = clawMachineService.create(clawMachine.getName(), clawMachine.getOrder());

        final ClawMachineIdentifier identifier = ClawMachineIdentifier.newBuilder()
                .setId(clawId.toString())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(identifier);
    }

    @GetMapping(value = "/clawMachines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClawMachineIdentifiers> getAll() {
        LOGGER.info("ClawMachineController.getAll");

        final List<String> clawIds = clawMachineService.getAll()
                .stream()
                .map(UUID::toString)
                .toList();

        final ClawMachineIdentifiers identifiers = ClawMachineIdentifiers.newBuilder()
                .addAllIds(clawIds)
                .build();

        return ResponseEntity.ok(identifiers);
    }
}
