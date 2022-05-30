package IOC;

import java.lang.reflect.Field;

public class AutowireCapableBeanFactory extends AbstractBeanFactory {
    /**
     * 利用BeanDifinition的反射机制创建一个Bean对象，故传入的BeanDifinition应为只有对应Bean的class
     * @param bean
     * @return
     */
    @Override
    protected Object DoCreatBean(BeanDefinition bean) throws Exception {
        try {
            Object instance = bean.getBeanClass().newInstance();
//            为此对象的属性赋值
            applyPropertyValues(instance,bean.getValues());
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 利用反射性质为bean对象中的属性赋值
     * @param bean
     * @param props
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    protected void applyPropertyValues(Object bean , PropertyValues props ) throws Exception {
        //首先获得BeanFactory中已经写入的Properitys集合
        PropertyValue[] values = props.getPropertyValues();
        for( PropertyValue pr : values){
//            针对每个属性的名称，利用class的getDeclaredField方法获取其Field类准备对其赋值
            Field field = bean.getClass().getDeclaredField(pr.getName());
//            将其可写性设置为true
            field.setAccessible(true);
            Object value = pr.getValue();
//            若value是BeanReference的实现对象，则表示其为Bean的引用对象，指向Factory中的其他Bean的Object类
//            因此先获得此Factory中的被引用Bean的Object对象
            if( value instanceof BeanReference){
                BeanReference beanReference = (BeanReference) value;
                value = GetBean(beanReference.getName());
            }
            field.set(bean,value);
        }
    }
}
