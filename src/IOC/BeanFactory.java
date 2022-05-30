package IOC;

public interface BeanFactory {
    Object GetBean(String name) throws Exception;
    void SetBeanDifinition(String name ,BeanDefinition beanDefinition) throws Exception;
}
