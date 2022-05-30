package IOC.io;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface BeanDefinitionReader {
    void loadBeanDefinitions(String path) throws IOException, ParserConfigurationException, SAXException, Exception;
}
