package org.samak.banana.controllers;

import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;
import org.samak.banana.services.plush.IPlushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plush")
public class PlushController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlushController.class);
    private static final List<String> CONTENT_TYPE_AUTORIZE = Arrays.asList("image/png", "image/jpeg");

    private final IPlushService plushService;

    public PlushController(final IPlushService plushService) {
        this.plushService = plushService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> createPlush(@RequestParam("plushName") final String plushName, @RequestParam("imagePath") final MultipartFile multipartImagePath) {
        LOGGER.info("PlushController.createPlush");

        LOGGER.info("plushName : {}", plushName);
        LOGGER.info("imagePath : {}", multipartImagePath);

        final String contentType = multipartImagePath.getContentType();
        LOGGER.info("contentType : {}", contentType);

        if (!CONTENT_TYPE_AUTORIZE.contains(contentType)) {
            return ResponseEntity.badRequest()
                    .body(String.format("The ContentType found is %s, no in %s", contentType, CONTENT_TYPE_AUTORIZE));
        }

        final String name = multipartImagePath.getName();
        LOGGER.info("name : {}", name);
        final String originalFilename = multipartImagePath.getOriginalFilename();
        LOGGER.info("originalFilename : {}", originalFilename);

        try (final InputStream imgInputString = multipartImagePath.getInputStream()) {

            final UUID plushId = this.plushService.createPlush(plushName, originalFilename, imgInputString);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(plushId);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Can't read file");
        }
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
