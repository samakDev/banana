package org.samak.banana.controllers;

import org.samak.banana.domain.plush.PlushState;
import org.samak.banana.domain.plush.User;
import org.samak.banana.services.plush.IPlushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plush/{id}")
public class PlushController {

    private final IPlushService service;

    @Autowired
    public PlushController(final IPlushService service) {
        this.service = service;
    }

    @RequestMapping(value = "/take/{key}", method = RequestMethod.POST)
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

    @RequestMapping(value = "/release/{key}", method = RequestMethod.POST)
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

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<PlushState> status(@PathVariable("id") final String id) {
        return service.getState(id)
                .map(plush -> new ResponseEntity<>(plush, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));

    }

}
