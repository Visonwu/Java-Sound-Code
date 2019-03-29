package com.vison.ws.servlet;

import com.vison.ws.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public class MyDispatcherServlet extends HttpServlet {

    private final static String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private final static String SCAN_PACKAGE ="scanPackage";

    private Properties properties = new Properties();

    private List<String> classNames = new ArrayList<>();//所有的类名称

    private Map<String ,Object> ioc = new HashMap<>();//ioc容器

    private List<HandlerMapping> handlerMappings = new ArrayList<>();//url映射

    @Override
    public void init(ServletConfig config) throws ServletException {

        //1.加载配置类
        doLoadConfig(config.getInitParameter(CONTEXT_CONFIG_LOCATION));

        //2.扫描所有的类
        doScanClass(properties.getProperty(SCAN_PACKAGE));

        //3.初始化bean
        doInitBean();

        //4.注入bean
        doInjectBean();

        //5.构造handlerMapping方法
        doInitHandleMapping();
    }


    /**初始化HandlerMapping*/
    private void doInitHandleMapping() {
        if (ioc.isEmpty()) return;
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(MyController.class)) continue;
            //获取controller上的url地址
            String preUrl ="";
            if (clazz.isAnnotationPresent(MyRequestMapping.class)){
                preUrl = clazz.getAnnotation(MyRequestMapping.class).value();
            }

            //获取到方法上的url
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(MyRequestMapping.class)) continue;
                MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                String suffixUrl = annotation.value();
               String url = new String("/"+preUrl+"/"+suffixUrl).replaceAll("/+","/");
               handlerMappings.add(new HandlerMapping(entry.getValue(),method,url));
            }
        }
    }

    /**注入bean*/
    private void doInjectBean() {
        if (ioc.isEmpty()) return;
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(MyAutowired.class)){
                    MyAutowired annotation = field.getAnnotation(MyAutowired.class);
                    String beanNmae = annotation.value().trim();
                    if ("".equals(annotation.value())){
                        beanNmae = field.getName();
                    }
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(),ioc.get(beanNmae));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**实例化bean*/
    private void doInitBean() {
        if (classNames.isEmpty()) return;
        for (String className : classNames) {
            try {
                Class<?> aClass = Class.forName(className);
                Object obj = aClass.newInstance();
                if (aClass.isAnnotationPresent(MyController.class)){
                    ioc.put(lowerFirstCase(aClass.getSimpleName()),obj);
                }else if (aClass.isAnnotationPresent(MyService.class)){
                    String value = aClass.getAnnotation(MyService.class).value();
                    if (!"".equals(value)){
                        ioc.put(value,obj);
                        continue;
                    }
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class<?> i : interfaces) {
                          ioc.put(i.getName(),obj);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    /**扫描所有的类*/
    private void doScanClass(String  packageName) {
        String baseUrl = packageName.replaceAll(".", "/");
        File dir = new File(baseUrl);
        for (File file : dir.listFiles()){
            if (file.isDirectory()){
                doScanClass(packageName+"."+file.getName());
            }else {
                classNames.add(packageName+file.getName().replaceAll(".class","").trim());
            }
        }

    }

    /**加载配置类
     * @param configLocation*/
    private void doLoadConfig(String configLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocation);
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            doDispatch(req,resp);
        }catch (Exception e){
            resp.getWriter().write("Error 500 \n"+Arrays.toString(e.getStackTrace())+e.getMessage());
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HandlerMapping handlerMapping =  getHandlerMapping(req);

        if (handlerMapping == null){
            resp.getWriter().write("404 not Found");
        }

        //获取方法的参数列表
        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();

        //保存所有需要自动赋值的参数值
        Object[] paramValues = new Object[parameterTypes.length];
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll(",\\s", ",");

            if (! handlerMapping.paramIndexMapping.containsKey(value)) continue;

            //如果找到匹配的对象，则开始填充参数值
            int index = handlerMapping.paramIndexMapping.get(value);
            paramValues[index] = convert(parameterTypes[index],value);


            //设置方法中的request和response对象
            Integer reqIndex = handlerMapping.paramIndexMapping.get(HttpServletRequest.class.getName());
            if (reqIndex != null ) {
                paramValues[reqIndex] = req;
            }
            Integer respIndex = handlerMapping.paramIndexMapping.get(HttpServletResponse.class.getName());
            if (respIndex != null ) {
                paramValues[respIndex] = resp;
            }
        }

    }

    //根据类型转换 参数值
    private Object convert(Class<?> parameterType, String value) {
        if (parameterType.isAssignableFrom(String.class)){
            return value;
        }else if (parameterType.isAssignableFrom(Integer.class)){
            return Integer.valueOf(value);
        }
        return value;
    }

    private HandlerMapping getHandlerMapping(HttpServletRequest req) {
        if (handlerMappings.isEmpty()) return null;
        String contextPath = req.getContextPath();
        String url = req.getRequestURI();
        url = url.replace(contextPath,"").replace("/+","/");
        for (HandlerMapping handlerMapping : handlerMappings) {
            if (url.equals(handlerMapping.getUrl())){
                return handlerMapping;
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    class HandlerMapping{
        private Object Controller; //mapping所在的实例
        private Method method; //mapping方法名
        private String url; //url路径
        private Map<String,Integer> paramIndexMapping; //参数顺序

        public HandlerMapping(Object controller, Method method, String url) {
            Controller = controller;
            this.method = method;
            this.url = url;
            paramIndexMapping = new HashMap<>();
            putParamIndexMapping(method);
        }

        public Object getController() {
            return Controller;
        }

        public Method getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }

        public Map<String, Integer> getParamIndexMapping() {
            return paramIndexMapping;
        }

        private void putParamIndexMapping(Method method) {

            //提取方法中添加了注解的参数
            Annotation[][] pa = method.getParameterAnnotations();
            for (int i=0;i<pa.length;i++){
                for (Annotation annotation : pa[i]) {
                    if (annotation instanceof MyRequestParam){
                        String value = ((MyRequestParam) annotation).value();
                        if (! "".equals(value)){
                            paramIndexMapping.put(value,i);
                        }
                    }
                }
            }

            //提取参数中添加了Request，Response;
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                if (type == HttpServletRequest.class ||
                    type == HttpServletResponse.class){
                    paramIndexMapping.put(type.getName(),i);
                }
            }


        }
    }


    /**获取最小的*/
    private String lowerFirstCase(String clazz) {
        char[] chars = clazz.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }
}
