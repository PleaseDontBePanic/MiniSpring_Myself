package IOC;

import java.util.ArrayList;
import java.util.List;

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
