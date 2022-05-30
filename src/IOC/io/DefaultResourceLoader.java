package IOC.io;

public class DefaultResourceLoader implements ResourceLoader {
    @Override
    public Resource GetResource(String path) {
        return new FileSystemResource(path);
    }
}
