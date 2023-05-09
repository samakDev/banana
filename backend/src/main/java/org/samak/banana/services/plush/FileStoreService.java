package org.samak.banana.services.plush;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStoreService implements IFileStoreService {

    @Value("${plush.image.folder:}")
    private String imageFolderStr;

    private final Path imageFolderPath;


    public FileStoreService() throws IOException {
        if (Strings.isBlank(imageFolderStr)) {
            imageFolderPath = Files.createTempDirectory("banana-img-container");
        } else {
            imageFolderPath = Paths.get(imageFolderStr.trim());
        }
    }

    @Override
    public String store(final String filename, final InputStream fileContent) throws IOException {
        final Path fileAbsolutePath = imageFolderPath.resolve(filename);

        Files.write(fileAbsolutePath, fileContent.readAllBytes());

        return fileAbsolutePath.toAbsolutePath().toString();
    }

    @Override
    public InputStream fetch(final String imageAbsolutePath) throws FileNotFoundException {
        return new FileInputStream(imageAbsolutePath);
    }
}
