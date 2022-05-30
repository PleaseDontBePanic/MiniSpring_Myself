package IOC.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemResource implements Resource {
    private String filepath;

    public FileSystemResource(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public InputStream GetInputStream() throws IOException {
        Path path = new File(this.filepath).toPath();
        return Files.newInputStream(path);

    }
}
