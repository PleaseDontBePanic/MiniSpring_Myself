# **Mini Spring 整体项目的设计流程及知识点**

[TOC]



## Spring

Spring是轻量级开源的JavaEE框架

解决企业应用开发的复杂性

有两个核心部分：IOC、AOP

IOC：控制反转，不再自己创建对象，而由Spring进行管理

AOP：不修改源代码的情况下进行功能增强

特点：

方便解耦，简化开发

AOP的支持（这一特性的优势）

方便程序测试

## IOC（Inversion Of Control）

控制反转，把对象的创建与调用过程交给Spring管理

底层原理：XML解析、工厂模式、反射

工厂模式：

![](https://s1.ax1x.com/2022/05/30/X174PO.png)

IOC的做法：

![](https://s1.ax1x.com/2022/05/30/X17zRg.png)

IOC的思想基于IOC容器完成，IOC容器底层就是对象工厂

### Spring提供IOC容器实现的两种方式（两个接口）：

（1）、BeanFactory：IOC最基本的实现，Spring内部的使用接口

*加载配置文件的时候不会创建对象，在获取/使用对象的时候才会创建对象

（2）、ApplicationContext：BeanFactory接口的子接口，开发使用

*加载配置文件的时候就会创建

**Spring容器使用依赖注入（DI）来管理组成一个应用程序的组件。这些对象被称为Spring Beans （一个对象就是一个Bean）。**

### **定义Bean**

**Bean****在理解上可以为一个Object对象的容器，在包装Object对象的同时也会定义一些此对象的相关属性（后续添加）**

```java
public class BeanDefinition {

    private Object Bean;
    public BeanDefinition(Object Bean){
        this.Bean = Bean;
    }
    public Object GetBean(){
        return Bean;
    }
}
```

**目前的 Bean 定义中，只有一个 Object 用于存放 Bean 对象。在后面陆续的实现中会逐步完善 BeanDefinition 相关属性的填充，例如：SCOPE_SINGLETON、SCOPE_PROTOTYPE、ROLE_APPLICATION、ROLE_SUPPORT、ROLE_INFRASTRUCTURE 以及 Bean Class 信息。**

### **定义一个最简单的Bean容器接口**

```java
public interface BeanFactory {
    Object GetBean(String name) throws Exception;
    void SetBeanDifinition(String name ,BeanDefinition beanDefinition) throws Exception;
}
```

### **对Bean容器的进一步设计**

**首先非常重要的是在Bean注册进BeanDifinition时不采取之前的将实例化信息（直接new出Object对象）注册入容器的方法，取而代之的是在Bean注册的时候只注册一个类信息（class）**

public class BeanDefinition {

```java
public class BeanDefinition {
	
    private Class BeanClass;
    private Object Bean;
    
    public BeanDefinition(Class beanClass) {
        BeanClass = beanClass;
    }
    public BeanDefinition(Object Bean){
        this.Bean = Bean;
    }
    
    public Class getBeanClass(){
        return BeanClass;
    }
    
    public Object GetBean(){
        return Bean;
    }
}
```

**用AbstractBeanFactory实现BeanFactory接口，维护一个map存储其中的BeanDifinition，并提供DoCreatBean方法（Protected）**

```java
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
```

定义AutowireCapableBeanFactory实现抽象类AbstractBeanFactory，实现DoCreatBean方法：

```java
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
    }
```

测试：

```java
@Test
public void TestBeanFactory() throws Exception {
    AutowireCapableBeanFactory factory = new AutowireCapableBeanFactory();
    BeanDefinition definition = new BeanDefinition(HelloService.class);
    factory.SetBeanDifinition("Test",definition);
    HelloService test = (HelloService) factory.GetBean("Test");
    test.Sayhello();
}
```

![](https://s1.ax1x.com/2022/05/30/X1zytA.png)

### **为什么要用反射的方式创建、存储Bean对象？**

**应从解耦的角度回答**

### **在创建对象实例化中我们还缺少什么？**

**其实还缺少一个关于类中是否有属性的问题，如果有类中包含属性那么在实例化的时候就需要把属性信息填充上，这样才是一个完整的对象创建。**

**属性填充要在类实例化创建之后，也就是需要在 AutowireCapableBeanFactory中添加 applyPropertyValues 操作。**

**由于我们需要在创建Bean时候填充属性操作，那么就需要在 bean 定义 BeanDefinition 类中，添加 PropertyValues 信息。**

**注：BeanDifinition中的属性为欲创造的对象类中本身就有的属性，并非是根据自身意图随意添加的**

**定义属性：**

```java
public class PropertyValue {
    private final String name;
    private final Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
```

**定义类属性的集合容器：**

```java
public class PropertyValues {
    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    /**
     * 在集合中添加属性类
     * @param value
     */
    public void AddPropertyValue(PropertyValue value){
        this.propertyValueList.add(value);
    }

    /**
     * 通过属性的name返回要找的属性类
     * @param name
     * @return
     */
    public PropertyValue GetPropertyValue(String name){
        for( PropertyValue p : this.propertyValueList){
            if(p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }

    /**
     * 以属性类数组的形式返回此对象的所有属性
     * @return
     */
    public PropertyValue[] getPropertyValues(){
        PropertyValue[] propertyValues = this.propertyValueList.toArray(new PropertyValue[0]);
        return propertyValues;
    }
}
```

**定义后的类补全：**

```java
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
```

**接下来就要利用反射性质为创建出的对象中的其他属性赋值，回顾一下反射性质：**

![](https://s1.ax1x.com/2022/05/30/X3PQIg.png)

**利用GetClass方法获取对象运行时的类，再调用getDeclaredField（String name）方法获取此属性的Field对象，再用setAccessible（true）将其设置为可写入的，然后即可逐一赋值。**

**结合上述知识点在AutowireCapableBeanFactory中添加applyPropertyValues(Object bean , PropertyValues props )方法：**

```java
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
```

测试：

```java
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
```

![](https://s1.ax1x.com/2022/05/30/X3iq39.png)

**综上所述，通过以上的代码可以达到运用某一类的class及包含此类属性的ProperityValues集合即可在BeanFactory中生成一个此类的完整对象。**

### XML文件解析和注册Bean

**接下来应为使此SmallSpring框架可以达到从xml文件解析和注册Bean对象的功能。整体设计结构如下图：**

![](https://s1.ax1x.com/2022/05/30/X3FMCQ.png)

**当资源可以加载后，接下来就是解析和注册 Bean 到 Spring 中的操作，这部分实现需要和 AutowireCapableBeanFactory核心类结合起来，因为你所有的解析后的注册动作，都会把 Bean 定义信息放入到这个类中。**

**Resource****是资源的抽象和访问接口，简单写了三个实现类**

**FileSystemResource，文件系统资源的实现类**

**ClassPathResource，classpath下资源的实现类（未添加）**

**UrlResource，对java.net.URL进行资源定位的实现类（未添加）**

```java
public interface Resource {
    InputStream GetInputStream() throws IOException;
}
```

```java
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
```

测试：

```java
@Test
public void TestFileSystemResource() throws IOException {
    FileSystemResource resource = new FileSystemResource("hjw.txt");
    InputStream stream = resource.GetInputStream();
    byte[] b = new byte[stream.available()];
    stream.read(b);
    System.out.println(new String(b));
}
```

![](https://s1.ax1x.com/2022/05/31/X3BqT1.png)

**在测试中Test方法和main方法默认根目录都是与当前Modul同级的**

**创建BeanDifinitionReader接口，通过 loadBeanDefinitions(String) 来从一个地址加载类定义。**

```java
public interface BeanDefinitionReader {
    void loadBeanDefinitions(String path) throws IOException, ParserConfigurationException, SAXException, Exception;
}
```

**实现BeanDefinitionReader的接口抽象类：AbstractBeanDefinitionReader，内置一个Map保存String – beanDefinition键值对内置一个 ResourceLoader resourceLoader，用于保存类加载器。用意在于，使用时，只需要向其 loadBeanDefinitions() 传入一个资源地址，就可以自动调用其类加载器，并把解析到的 BeanDefinition 保存到 Map中去。**

```java
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
```

**编写XmlBeanDefinitionReader来解析和加载Xml文件中对所要用类的配置具体代码如下：**

```java
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
//    在使用的时候传入一个AutowireCapableBeanFactory对象容器
    private AutowireCapableBeanFactory beanFactory;
//    ResourceLoader和Factory的构造器，用来读取Xml文件和实例化工厂
    public XmlBeanDefinitionReader(ResourceLoader loader, AutowireCapableBeanFactory beanFactory) {
        super(loader);
        this.beanFactory = beanFactory;
    }

    /**
     * 获取xml文件的输入流并读取对Bean的配置
     * @param path
     * @throws Exception
     */
    @Override
    public void loadBeanDefinitions(String path) throws Exception {
//        获取文件的输入流
        InputStream inputStream = getLoader().GetResource(path).GetInputStream();
        doLoadBeanDefinitions(inputStream);
    }

    protected void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);
        registerBeanDefinitions(doc);
        inputStream.close();
    }
    protected void registerBeanDefinitions(Document doc) throws Exception {
        Element element = doc.getDocumentElement();
        parseBeanDefinitions(element);
    }
    protected void parseBeanDefinitions(Element element) throws Exception {
        NodeList childNodes = element.getChildNodes();
        for(int i = 0 ; i < childNodes.getLength() ; i++){
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                processBeanDefinition(ele);
            }
        }
    }

    protected void processBeanDefinition(Element ele) throws Exception {
        String name = ele.getAttribute("name");
        String className = ele.getAttribute("class");
        BeanDefinition beanDefinition = new BeanDefinition(new PropertyValues());
        processProperty(ele,beanDefinition);
        beanDefinition.setClassName(className);
        getMap().put(name,beanDefinition);
//        将属性传入Factory
        beanFactory.SetBeanDifinition(name,beanDefinition);
    }

    protected void processProperty(Element ele,BeanDefinition beanDefinition){
        NodeList property = ele.getElementsByTagName("property");
        for( int i = 0 ; i < property.getLength() ; i++){
            Node node = property.item(i);
            if(node instanceof Element){
                Element propele = (Element) node;
                String name = propele.getAttribute("name");
                String value = propele.getAttribute("value");
//                若value非空说明欲设置的value为非引用参数，常规赋值即可
                if(value != null && value.length() > 0){
//                    判断value值是否为int类型
                    if(isnum(value)){
                        beanDefinition.getValues().AddPropertyValue(new PropertyValue(name,Integer.valueOf(value)));
                    }else{
                        beanDefinition.getValues().AddPropertyValue(new PropertyValue(name,value));
                    }
                }else{
//                    value为空，说明此property将引用容器中的其他对象
                    String ref = propele.getAttribute("ref");
//                    新建对其他对象的引用BeanReference
                    BeanReference beanReference = new BeanReference(ref);
                    beanDefinition.getValues().AddPropertyValue(new PropertyValue(name,beanReference));
                }
            }
        }
    }

    protected boolean isnum(String s){
        for(int index = 0; index < s.length() ; index++){
            if( (int)s.charAt(index) <48 || (int)s.charAt(index) > 57){
                return false;
            }
        }
        return true;
    }
}
```

测试：

**Xml**文件定义为：

```xml
<?xml version="1.0" encoding="UTF-8" ?> 
<beans>
    <bean name="Hello" class="Tests.HelloService">
        <property name="name" value="hjw"/>
        <property name="year" value="23"/>
    </bean>
    <bean name="Tobean" class="Tests.ToService">
        <property name="helloService" ref="Hello"/>
    </bean>
</beans>
```

```java
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
```

![](https://s1.ax1x.com/2022/05/31/X3ru8K.png)



## **AOP（Aspect Oriented Programming）**

**1**、什么是AOP 

（1）面向切面编程（方面），利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。**

**（2）通俗描述：不通过修改源代码方式，在主干功能里面添加新功能**

![](https://s1.ax1x.com/2022/05/31/X8lJv6.png)

### **AOP底层使用动态代理**

有两种方式实现动态代理：

#### 使用JDK动态代理（有接口情况）

**创建接口实现类代理对象，增强类的方法**

![](https://s1.ax1x.com/2022/05/31/X8l2qS.png)

**目的：通过代理对象来强化原login方法**

步骤如下：

**使用Proxy类创建接口代理对象**

**JDK中几个关键的类：**

![](https://s1.ax1x.com/2022/05/31/X81QL8.png)

JDK动态代理实例：

```java
public class JDKProxy { public static void main(String[] args) { 
    //创建接口实现类代理对象 
    Class[] interfaces = {UserDao.class}; 
    // Proxy.newProxyInstance(JDKProxy.class.getClassLoader(), interfaces, new InvocationHandler() { 
    // @Override 
    // public Object invoke(Object proxy, Method method, Object[] args) throws Throwable { 
    // return null; 
    // } 
    // }); 
    UserDaoImpl userDao = new UserDaoImpl(); 
    
    UserDao dao = (UserDao)Proxy.newProxyInstance(
        JDKProxy.class.getClassLoader(), 
        interfaces, 
        new UserDaoProxy(userDao));
    
    int result = dao.add(1, 2); 
    System.out.println("result:"+result); 
	} 
} 
//创建代理对象代码 
class UserDaoProxy implements InvocationHandler { 
    //1 把创建的是谁的代理对象，把谁传递过来 
    //有参数构造传递 
    private Object obj; 
    public UserDaoProxy(Object obj) { 
        this.obj = obj; 
    } 
    //增强的逻辑 
    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable { 
        //方法之前 
        System.out.println("方法之前执行...."+method.getName()+" :传递的参数..."+ Arrays.toString(args)); 
        //被增强的方法执行 
        Object res = method.invoke(obj, args); 
        //方法之后 
        System.out.println("方法之后执行...."+obj); return res; 
    	} 
}
```

**Invoke（）方法中增强的为其代理的对象的方法，newProxyInstance一经调用直接执行方法即可，若想根据不同的方法采取不同的增强逻辑可以用method.getName()方法**

#### **使用CGLIB动态代理**

**创建子类的代理对象，增强类的方法**

![](https://s1.ax1x.com/2022/05/31/X8lTx0.png)

**区别：一个是接口实现类的代理对象一个是子类的代理对象**



### **定义AopPorxy接口**

**仅有一个返回Porxy类型的方法：**

```java
public interface AopProxy {
    Object getProxy();
}
```

### **定义AdvisedSupport**类

**用来封装待加强的类及获取其接口class的方法**

```java
public class AdvisedSupport {
    private TargetSource targetSource;

    public AdvisedSupport(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }
}
```

### **定义TargetSource类**

**直接封装待加强的Object类，getTargetClass()方法用于获取其接口，用于Porxy的newProxyInstance（）方法使用**

```java
public class TargetSource {
    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    public Class<?>[] getTargetClass() {
        return this.target.getClass().getInterfaces();
    }

    public Object getTarget() {
        return this.target;
    }
}
```

### **定义JdkDynamicAopProxy类**

**采用JDK代理方法达到对待增强的方法起到选择性增强的作用。**

```java
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private final AdvisedSupport advice;
    private String forcedMethodName;
    public JdkDynamicAopProxy(Object obj , String forcedMethodName) {
        this.advice = new AdvisedSupport(new TargetSource(obj));
        this.forcedMethodName = forcedMethodName;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader() , advice.getTargetSource().getTargetClass(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Object res = method.invoke(advice.getTargetSource().getTarget(), args);
        if(methodName.equals(forcedMethodName)){
            System.out.println("加强执行方法：" + methodName);
        }
        return res;
    }
}
```

测试：

UserDao接口：

```java
public interface UserDao {
    int add(int a,int b);
    String update(String id);
}
```

其实现类

```java
public class UserDaoImpl implements UserDao {

    @Override
    public int add(int a, int b) {
        return a+b;
    }

    @Override
    public String update(String id) {
        return id;
    }
}
```

```java
@Test
public void TestPorxy(){
    UserDaoImpl userDao = new UserDaoImpl();
    JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(userDao, "add");
    UserDao dao = (UserDao)proxy.getProxy();
    System.out.println(dao.add(1,2));
}
```

![](https://s1.ax1x.com/2022/05/31/X8GKQ1.png)








