package org.samak.banana.controllers;

import org.apache.logging.log4j.util.Strings;
import org.mapstruct.factory.Mappers;
import org.samak.banana.dto.ClawMachine;
import org.samak.banana.dto.ClawMachineIdentifier;
import org.samak.banana.dto.ClawMachineIdentifiers;
import org.samak.banana.dto.ClawMachineUpdater;
import org.samak.banana.mapper.ClawMachineMapper;
import org.samak.banana.services.clawmachine.ClawMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private static final ClawMachineMapper CLAW_MACHINE_MAPPER = Mappers.getMapper(ClawMachineMapper.class);

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

        final Integer order = clawMachine.hasOrder() ? clawMachine.getOrder() : null;
        final UUID clawId = clawMachineService.create(clawMachine.getName(), order);

        final ClawMachineIdentifier identifier = ClawMachineIdentifier.newBuilder()
                .setId(clawId.toString())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(identifier);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClawMachine> getClawMachine(@PathVariable("id") final UUID clawMachineId) {
        LOGGER.info("ClawMachineController.get ClawMachine with id: {}", clawMachineId);

        final ClawMachine clawMachine = clawMachineService.getClawMachine(clawMachineId)
                .map(CLAW_MACHINE_MAPPER::convertClawMachineEntityToDto)
                .orElseThrow(() -> new IllegalArgumentException("not ClawMachine found for this id"));

        return ResponseEntity.ok(clawMachine);
    }

    @PatchMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClawMachine> updateClawMachine(@PathVariable("id") final UUID clawMachineId,
                                                         @RequestBody() final ClawMachineUpdater clawMachineUpdater) {
        LOGGER.info("ClawMachineController.update ClawMachine with id {}, and new value {}", clawMachineId, clawMachineUpdater);

        final String name = clawMachineUpdater.hasName() ? clawMachineUpdater.getName() : null;
        final int order = clawMachineUpdater.hasOrder() ? clawMachineUpdater.getOrder() : null;

        final ClawMachine clawMachine = clawMachineService.updateClawMachine(clawMachineId, name, order)
                .map(CLAW_MACHINE_MAPPER::convertClawMachineEntityToDto)
                .orElseThrow(() -> new IllegalArgumentException("not ClawMachine found for this id"));

        return ResponseEntity.ok(clawMachine);
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteClawMachine(@PathVariable("id") final UUID clawMachineId) {
        LOGGER.info("ClawMachineController.delete ClawMachine with id {}", clawMachineId);

        clawMachineService.deleteClawMachine(clawMachineId);

        return ResponseEntity.noContent().build();
    }
}
