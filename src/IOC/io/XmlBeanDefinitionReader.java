package IOC.io;

import IOC.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;

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
