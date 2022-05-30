package IOC;

public class BeanReference {
    private String name;
    private Object bean;

//    为什么不用有Object类的构造器：将来要与在Factory中的对象建立关系，而非直接new一个新对象
    public BeanReference(String name) {
        this.name = name;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }
}
