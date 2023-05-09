package org.samak.banana.services.plush;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface IFileStoreService {

    String store(String filename, InputStream fileContent) throws IOException;

    InputStream fetch(String imageAbsolutePath) throws FileNotFoundException;
}
