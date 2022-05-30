package IOC.io;

import IOC.BeanDefinition;

import java.util.HashMap;

public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    private HashMap<String, BeanDefinition> map = new HashMap<>();
    private ResourceLoader loader;

    public AbstractBeanDefinitionReader(ResourceLoader loader) {
        this.loader = loader;
    }

    public HashMap<String, BeanDefinition> getMap() {
        return map;
    }

    public ResourceLoader getLoader() {
        return loader;
    }
}
