package org.samak.banana.config;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.samak.banana.dto.model.Plush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlushConverter implements Converter<String, Plush> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlushConverter.class);

    /**
     * to Genereate source see JsonFormat.printer().print(message)
     */
    @Override
    public Plush convert(final String source) {
        try {
            final Plush.Builder builder = Plush.newBuilder();
            JsonFormat.parser().merge(source, builder);
            return builder.build();
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error("can't convert {} to Plush error: ", source, e);
            return null;
        }

    }

}
