package IOC;

public class BeanDefinition {

    private Object Bean;
    private Class BeanClass;
    private PropertyValues values;
    private String className;

    public BeanDefinition(PropertyValues values) {
        this.values = values;
    }

    public BeanDefinition(Class beanClass) {
        BeanClass = beanClass;
    }
    public BeanDefinition(){}

    public BeanDefinition(Object Bean){
        this.Bean = Bean;
    }

    public void setClassName(String className) throws ClassNotFoundException {
        this.className = className;
        this.BeanClass = Class.forName(className);
    }

    /**
     * class\PropertyValues构造器，若values为null则生成一个new PropertyValues()
     * @param beanClass
     * @param values
     */
    public BeanDefinition(Class beanClass, PropertyValues values) {
        BeanClass = beanClass;
        this.values = values != null ? values : new PropertyValues();
    }

    public Class getBeanClass(){
        return BeanClass;
    }

    public void setBean(Object bean) {
        Bean = bean;
    }

    public Object GetBean(){
        return Bean;
    }

    public PropertyValues getValues() {
        return values;
    }

    public void setValues(PropertyValues values) {
        this.values = values;
    }
}
