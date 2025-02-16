package org.samak.banana.controllers;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.samak.banana.dto.model.Plush;
import org.samak.banana.dto.model.PlushIdentifier;
import org.samak.banana.dto.model.PlushImport;
import org.samak.banana.dto.model.PlushLocker;
import org.samak.banana.dto.model.PlushUnLocker;
import org.samak.banana.dto.model.PlushUpdater;
import org.samak.banana.dto.model.Plushes;
import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.entity.PlushEntity;
import org.samak.banana.services.clawmachine.IClawMachineService;
import org.samak.banana.services.plush.IPlushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/claw-machine/{claw-machine-id}/plushes")
public class PlushController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlushController.class);
    private static final List<String> IMAGE_CONTENT_TYPE_ALLOWED = Arrays.asList(MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);

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

        if (!IMAGE_CONTENT_TYPE_ALLOWED.contains(plushImg.getContentType())) {
            throw new IllegalArgumentException("image format not supported. Expected format : " + IMAGE_CONTENT_TYPE_ALLOWED);
        }

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

    @PatchMapping(value = "/{plush_id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Plush> updatePlush(@PathVariable("claw-machine-id") final UUID clawMachineId,
                                             @PathVariable("plush_id") final UUID plushId,
                                             @RequestParam(value = "metadata", required = false) final PlushUpdater plushUpdater,
                                             @RequestParam(value = "plushImg", required = false) final MultipartFile plushImg) throws IOException {
        LOGGER.info("PlushController.updatePlush {} and/or img {} for plush {} in clawMachine {}", plushUpdater, plushImg, plushId, clawMachineId);

        clawMachineService.getClawMachine(clawMachineId)
                .orElseThrow(() -> new IllegalArgumentException("no ClawMachine for " + clawMachineId));

        final PlushEntity originalPlush = plushService.getPlushMetadata(plushId)
                .orElseThrow(() -> new IllegalArgumentException("no plush for " + plushId));

        final String name = plushUpdater.hasName() ? plushUpdater.getName() : null;
        final Integer order = plushUpdater.hasOrder() ? plushUpdater.getOrder() : null;

        if (Objects.isNull(name) && Objects.isNull(order) && Objects.isNull(plushImg)) {
            throw new IllegalArgumentException("nothing to update");
        }

        final PlushEntity entity = plushService.updatePlush(originalPlush, name, order, plushImg);

        return ResponseEntity.ok(plushService.converter(entity));
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Plushes> getAll(@PathVariable("claw-machine-id") final UUID clawMachineId) {
        LOGGER.info("PlushController.getAll for clawMachine {}", clawMachineId);

        final Optional<ClawMachineEntity> clawMachineOpt = clawMachineService.getClawMachine(clawMachineId);

        if (clawMachineOpt.isEmpty()) {
            throw new IllegalArgumentException("no ClawMachine found for this id " + clawMachineId);
        }

        final List<Plush> plushList = plushService.getAll(clawMachineOpt.get())
                .stream()
                .map(plushService::converter)
                .filter(Objects::nonNull)
                .toList();

        final Plushes plushes = Plushes.newBuilder()
                .addAllPlush(plushList)
                .build();

        return ResponseEntity.ok(plushes);
    }

    @GetMapping(value = "{id}", produces = MediaType.MULTIPART_MIXED_VALUE)
    public ResponseEntity<MultiValueMap<String, Object>> getPlush(@PathVariable("claw-machine-id") final UUID clawMachineId,
                                                                  @PathVariable("id") final UUID plushId) {
        LOGGER.info("PlushController.get for clawMachine {} for plush {}", clawMachineId, plushId);

        final PlushEntity plushEntity = checkRequestValidity(clawMachineId, plushId);

        final InputStream plushImg;
        try {
            plushImg = plushService.getPlushImg(plushEntity);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("impossible to get the file error: ", e);
        }

        final MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("file", imageBody(plushEntity, plushImg));
        values.add("metadata", metadataBody(plushEntity));

        return ResponseEntity.ok(values);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> getPlushImg(@PathVariable("claw-machine-id") final UUID clawMachineId,
                                                           @PathVariable("id") final UUID plushId) {
        LOGGER.info("PlushController.get as octect stream for clawMachine {} for plush {}", clawMachineId, plushId);

        final PlushEntity plushEntity = checkRequestValidity(clawMachineId, plushId);

        final InputStream plushImg;
        try {
            plushImg = plushService.getPlushImg(plushEntity);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("impossible to get the file error: ", e);
        }

        final HttpEntity<InputStreamResource> httpEntity = imageBody(plushEntity, plushImg);

        return ResponseEntity.ok()
                .contentType(httpEntity.getHeaders().getContentType())
                .body(httpEntity.getBody());
    }

    private PlushEntity checkRequestValidity(final UUID clawMachineId, final UUID plushId) {
        final Optional<ClawMachineEntity> clawMachineOpt = clawMachineService.getClawMachine(clawMachineId);

        if (clawMachineOpt.isEmpty()) {
            throw new IllegalArgumentException("no ClawMachine found for this id " + clawMachineId);
        }

        final Optional<PlushEntity> plushEntityOpt = plushService.getPlushMetadata(plushId);

        if (plushEntityOpt.isEmpty()) {
            throw new IllegalArgumentException("no Plush found for this id " + plushId);
        }

        return plushEntityOpt.get();
    }

    private HttpEntity<InputStreamResource> imageBody(final PlushEntity plushEntity, final InputStream plushImg) {
        final String extension = FilenameUtils.getExtension(plushEntity.getImageAbsolutePath());

        final MediaType contentType = switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "png" -> MediaType.IMAGE_PNG;
            default -> throw new IllegalStateException("Unexpected value: " + extension.toLowerCase());
        };

        final HttpHeaders fileHeader = new HttpHeaders();
        fileHeader.setContentType(contentType);

        return new HttpEntity<>(new InputStreamResource(plushImg), fileHeader);
    }

    private HttpEntity<byte[]> metadataBody(final PlushEntity plushEntity) {
        final HttpHeaders metadataHeader = new HttpHeaders();
        metadataHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        final Plush plush = plushService.converter(plushEntity);

        return new HttpEntity<>(plush.toByteArray(), metadataHeader);
    }

    @PostMapping(value = "/{id}/lock")
    public ResponseEntity<Boolean> lock(
            @PathVariable("claw-machine-id") final UUID clawMachineId,
            @PathVariable("id") final UUID plushId,
            @RequestBody() final PlushLocker plushLocker) {
        LOGGER.info("PlushController.lock. PlushLocker {}, plushId {} for clawMachine {}", plushLocker, plushId, clawMachineId);

        if (Strings.isBlank(plushLocker.getName())) {
            throw new IllegalArgumentException("no name found");
        }

        final Optional<ClawMachineEntity> clawMachineOpt = clawMachineService.getClawMachine(clawMachineId);

        if (clawMachineOpt.isEmpty()) {
            throw new IllegalArgumentException("no ClawMachine found for this id " + clawMachineId);
        }

        final Optional<PlushEntity> plushOpt = plushService.getPlushMetadata(plushId);

        if (plushOpt.isEmpty()) {
            throw new IllegalArgumentException("no Plush found for this id " + plushId);
        }

        final OffsetDateTime lockDate = Optional.of(plushLocker.getSince())
                .filter(any -> plushLocker.hasSince())
                .map(timestamp -> Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()))
                .map(instant -> OffsetDateTime.ofInstant(instant, ZoneId.of("UTC")))
                .orElse(null);


        final boolean result = plushService.lock(plushId, plushOpt.get(), plushLocker.getName(), lockDate);

        if (result) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/{id}/unlock")
    public ResponseEntity<Boolean> unlock(
            @PathVariable("claw-machine-id") final UUID clawMachineId,
            @PathVariable("id") final UUID plushId,
            @RequestBody() final PlushUnLocker plushLocker) {
        LOGGER.info("PlushController.unlock. plushId {} for clawMachine {}", plushId, clawMachineId);

        clawMachineService.getClawMachine(clawMachineId)
                .orElseThrow(() -> new IllegalArgumentException("no ClawMachine found for this id " + clawMachineId));

        final Optional<PlushEntity> plushOpt = plushService.getPlushMetadata(plushId);

        if (plushOpt.isEmpty()) {
            throw new IllegalArgumentException("no Plush found for this id " + plushId);
        }

        final PlushEntity plushEntity = plushOpt.get();

        if (!plushService.hasRightToUnlock(plushEntity, plushLocker.getName())) {
            throw new IllegalArgumentException("You are not allowed");
        }

        plushService.unlock(plushEntity);

        return ResponseEntity.ok(true);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> deletePlush(@PathVariable("claw-machine-id") final UUID clawMachineId,
                                            @PathVariable("id") final UUID plushId) throws IOException {
        LOGGER.info("PlushController.delete {} for clawMachine {}", plushId, clawMachineId);

        final Optional<ClawMachineEntity> clawMachineOpt = clawMachineService.getClawMachine(clawMachineId);

        if (clawMachineOpt.isEmpty()) {
            throw new IllegalArgumentException("no ClawMachine found for this id " + clawMachineId);
        }

        plushService.delete(plushId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> importPlush(@PathVariable("claw-machine-id") final UUID clawMachineId,
                                               @RequestParam(value = "metadata", required = false) final PlushImport plushImport,
                                               @RequestParam("bananaConfigFile") final MultipartFile bananaConfigFile) throws IOException {
        LOGGER.info("PlushController.importPlush for clawMachine {} with plushImport {}", clawMachineId, plushImport);

        if (!"application/json".equals(bananaConfigFile.getContentType())) {
            throw new IllegalArgumentException("Only BananaConfig.json are allowed");
        }

        final Optional<ClawMachineEntity> clawMachineOpt = clawMachineService.getClawMachine(clawMachineId);

        if (clawMachineOpt.isEmpty()) {
            throw new IllegalArgumentException("no ClawMachine found for this id " + clawMachineId);
        }

        final String homeDirectory = Objects.nonNull(plushImport) && plushImport.hasHomeDirectory()
                ? plushImport.getHomeDirectory()
                : null;

        final boolean allSucceed = plushService.importBananaConfig(clawMachineOpt.get(), bananaConfigFile, homeDirectory);

        return ResponseEntity.ok(allSucceed);
    }

}
