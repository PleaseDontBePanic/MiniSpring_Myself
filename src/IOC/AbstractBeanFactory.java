package IOC;

import java.util.HashMap;

public abstract class AbstractBeanFactory implements BeanFactory {
    private HashMap<String,BeanDefinition> map = new HashMap<>();

    /**
     *
     * @param name
     * @return返回要查找的Bean对象，若没有，返回Exception
     * @throws Exception
     */
    @Override
    public Object GetBean(String name) throws Exception {
        BeanDefinition beanDefinition = map.get(name);
        Object bean = beanDefinition.GetBean();
        if( bean == null){
            throw new Exception("No Bean Named" + name + "Finded");
        }
        return bean;
    }

    /**
     * 写入BeanDifinition，首先先利用传入BeanDifinition中的BeanClass反射创建一个Bean对象
     * @param name
     * @param beanDefinition
     */
    @Override
    public void SetBeanDifinition(String name ,BeanDefinition beanDefinition) throws Exception {
        Object bean = DoCreatBean(beanDefinition);
        beanDefinition.setBean(bean);
        map.put(name,beanDefinition);
    }

    /**
     * 用BeanDifinition类的反射机制创建一个Bean对象
     * @param bean
     * @return
     */
    protected abstract Object DoCreatBean(BeanDefinition bean) throws Exception;
}
