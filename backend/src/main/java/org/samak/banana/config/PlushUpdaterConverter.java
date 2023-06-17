package org.samak.banana.config;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.samak.banana.dto.model.PlushUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlushUpdaterConverter implements Converter<String, PlushUpdater> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlushUpdaterConverter.class);

    /**
     * to Genereate source see JsonFormat.printer().print(message)
     */
    @Override
    public PlushUpdater convert(final String source) {
        try {
            final PlushUpdater.Builder builder = PlushUpdater.newBuilder();
            JsonFormat.parser().merge(source, builder);
            return builder.build();
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error("can't convert {} to PlushUpdater error: ", source, e);
            return null;
        }

    }

}
