package Tests;

import AOP.JdkDynamicAopProxy;
import IOC.*;
import IOC.annotation.Component;
import IOC.io.DefaultResourceLoader;
import IOC.io.FileSystemResource;
import IOC.io.XmlBeanDefinitionReader;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;

public class Testss {
    @Test
    public void TestBeanFactory() throws Exception {
        AutowireCapableBeanFactory factory = new AutowireCapableBeanFactory();
        BeanDefinition definition = new BeanDefinition(HelloService.class);
        factory.SetBeanDifinition("Test",definition);
        HelloService test = (HelloService) factory.GetBean("Test");
        test.Sayhello();
    }
    @Test
    public void TestField() throws IllegalAccessException, NoSuchFieldException {
        HelloService helloService = new HelloService();
        Field name = helloService.getClass().getDeclaredField("name");
        name.setAccessible(true);
        name.set(helloService,"hjw");
        System.out.println(helloService.getName());
    }

    @Test
    public void TestAutoWierdFactory() throws Exception {
        PropertyValue namevalue = new PropertyValue("name", "hjw");
        PropertyValues values = new PropertyValues();
        values.AddPropertyValue(namevalue);
        BeanDefinition HelloBean = new BeanDefinition(HelloService.class, values);
        AutowireCapableBeanFactory factory = new AutowireCapableBeanFactory();
        factory.SetBeanDifinition("HelloBean",HelloBean);
        HelloService helloBean = (HelloService) factory.GetBean("HelloBean");
        System.out.println(helloBean.getName());
        helloBean.Sayhello();
    }

    @Test
    public void TestinstanceOf(){
        HelloService service = new HelloService();
        ToService toService = new ToService();
        Object o1 = toService;
        Object o = service;
        System.out.println(o instanceof HelloService);
        System.out.println(o1 instanceof HelloService);
    }

    @Test
    public void TestReference() throws Exception {
//        新建工厂
        AutowireCapableBeanFactory factory = new AutowireCapableBeanFactory();
//        准备HelloService对象的属性容器，并赋值
        PropertyValues hellovalues = new PropertyValues();
        hellovalues.AddPropertyValue(new PropertyValue("name","hjw"));
        BeanDefinition hellobean = new BeanDefinition(HelloService.class, hellovalues);
//        准备ToService对象的属性容器并赋值，仅有一个指向Factory中HelloService对象的引用
        PropertyValues Tovalues = new PropertyValues();
        Tovalues.AddPropertyValue(new PropertyValue("helloService",new BeanReference("hellobean")));
        BeanDefinition Tobean = new BeanDefinition(ToService.class, Tovalues);

        factory.SetBeanDifinition("hellobean",hellobean);
        factory.SetBeanDifinition("Tobean",Tobean);
        ToService tobean = (ToService)factory.GetBean("Tobean");
        HelloService tobeanHelloService = tobean.getHelloService();
        System.out.println(tobeanHelloService.getName());
    }

    @Test
    public void TestGetResource() throws IOException {
        URL url = this.getClass().getClassLoader().getResource("hjw.txt");
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();

    }

    @Test
    public void TestFileSystemResource() throws IOException {
        FileSystemResource resource = new FileSystemResource("hjw.txt");
        InputStream stream = resource.GetInputStream();
        byte[] b = new byte[stream.available()];
        stream.read(b);
        System.out.println(new String(b));
    }
    @Test
    public void TestXml() throws Exception {
        AutowireCapableBeanFactory factory = new AutowireCapableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new DefaultResourceLoader(), factory);
        xmlBeanDefinitionReader.loadBeanDefinitions("FirstSet.xml");
        ToService toService = (ToService)factory.GetBean("Tobean");
        HelloService helloService = toService.getHelloService();
        helloService.Sayhello();
        System.out.println(helloService.getName() + ",age:" + helloService.getYear());
    }

    @Test
    public void TestPorxy(){
        UserDaoImpl userDao = new UserDaoImpl();
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(userDao, "add");
        UserDao dao = (UserDao)proxy.getProxy();
        System.out.println(dao.add(1,2));
    }

    @Test
    public void TestComponent(){
        UseComponent useComponent = new UseComponent();
        Class<? extends UseComponent> componentClass = useComponent.getClass();
        Component annotation = componentClass.getAnnotation(Component.class);
        System.out.println(annotation.name());
    }

    @Test
    public void Update(){
        System.out.println("update");
    }
}
