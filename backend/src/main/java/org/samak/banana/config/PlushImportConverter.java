package org.samak.banana.config;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.samak.banana.dto.model.PlushImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlushImportConverter implements Converter<String, PlushImport> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlushImportConverter.class);

    /**
     * to Genereate source see JsonFormat.printer().print(message)
     */
    @Override
    public PlushImport convert(final String source) {
        try {
            final PlushImport.Builder builder = PlushImport.newBuilder();
            JsonFormat.parser().merge(source, builder);
            return builder.build();
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error("can't convert {} to PlushImport error: ", source, e);
            return null;
        }

    }

}
