package org.samak.banana.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class BananaConfigProvider {

    private static final String CONFIG_KEY = "BANANA_CONFIG_FILE";
    private static final String DEFAULT_CONFIG = "banana-config.json";

    @Bean
    public BananaConfig provideBananaConfig() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String configEnv = System.getenv(CONFIG_KEY);

        final BananaConfig config;
        if (configEnv != null) {
            final File input = new File(configEnv);
            config = mapper.readValue(input, BananaConfig.class);
        } else {
            final InputStream input = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG);
            config = mapper.readValue(input, BananaConfig.class);
        }

        return config;
    }

}
