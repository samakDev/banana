package org.samak.banana.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.samak.banana.domain.plush.IPlushConfig;
import org.samak.banana.domain.plush.Plush;

import java.util.List;

public class BananaConfig implements IPlushConfig {

    private final List<Plush> plushs;

    @JsonCreator
    public BananaConfig(@JsonProperty("plushs") List<Plush> plushs) {
        super();
        this.plushs = plushs;
    }

    @Override
    public List<Plush> getPlushs() {
        return plushs;
    }

}