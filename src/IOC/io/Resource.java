package IOC.io;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
    InputStream GetInputStream() throws IOException;
}
