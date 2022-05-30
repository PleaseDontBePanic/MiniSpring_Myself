package AOP;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
