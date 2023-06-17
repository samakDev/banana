package org.samak.banana.controllers;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.mapstruct.factory.Mappers;
import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;
import org.samak.banana.dto.model.Plush;
import org.samak.banana.dto.model.PlushIdentifier;
import org.samak.banana.dto.model.PlushLocker;
import org.samak.banana.dto.model.Plushes;
import org.samak.banana.entity.ClawMachineEntity;
import org.samak.banana.entity.PlushEntity;
import org.samak.banana.mapper.PlushMapper;
import org.samak.banana.services.clawmachine.IClawMachineService;
import org.samak.banana.services.plush.IPlushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    private static final PlushMapper PLUSH_MAPPER = Mappers.getMapper(PlushMapper.class);
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

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Plushes> getAll(@PathVariable("claw-machine-id") final UUID clawMachineId) {
        LOGGER.info("PlushController.getAll for clawMachine {}", clawMachineId);

        final Optional<ClawMachineEntity> clawMachineOpt = clawMachineService.getClawMachine(clawMachineId);

        if (clawMachineOpt.isEmpty()) {
            throw new IllegalArgumentException("no ClawMachine found for this id " + clawMachineId);
        }

        final List<Plush> plushList = plushService.getAll(clawMachineOpt.get())
                .stream()
                .map(PLUSH_MAPPER::convertPlushEntityToDto)
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
        LOGGER.info("PlushController.getAll for clawMachine {}", clawMachineId);

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

    private HttpEntity<Resource> imageBody(final PlushEntity plushEntity, final InputStream plushImg) {
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

        final Plush plush = PLUSH_MAPPER.convertPlushEntityToDto(plushEntity);

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


        final boolean result = plushService.take(plushId, plushOpt.get(), plushLocker.getName(), lockDate);

        if (result) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.internalServerError().build();
        }
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
