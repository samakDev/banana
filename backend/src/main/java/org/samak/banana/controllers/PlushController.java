package org.samak.banana.controllers;

import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;
import org.samak.banana.services.plush.IPlushService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plush/{id}")
public class PlushController {

    private final IPlushService service;

    public PlushController(final IPlushService service) {
        this.service = service;
    }

    @PostMapping(value = "/take/{key}")
    public ResponseEntity<Boolean> take(@PathVariable("id") final String id, @PathVariable("key") final String key) {
        final User user = new User();
        user.setId(key);
        user.setName(key);

        final boolean result = service.take(user, id);

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

        final boolean result = service.release(user, id);

        if (result) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping(value = "/status")
    public ResponseEntity<PlushState> status(@PathVariable("id") final String id) {
        return service.getState(id)
                .map(plush -> new ResponseEntity<>(plush, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));

    }

}
