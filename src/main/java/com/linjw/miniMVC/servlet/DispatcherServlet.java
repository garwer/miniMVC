package com.linjw.miniMVC.servlet;

import com.linjw.miniMVC.annotation.*;
import com.linjw.miniMVC.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在Spring MVC中，DispatcherServlet是核心，下面我们来实现它。首先来说，
 * Spring MVC中的DispatcherServlet说到底，还是HttpServlet的子类，
 * 因此我这边自己的DispatcherSerlvet需要extends HttpServlet。
 *
 * WebSocket是什么
 * 以前我们定义一个Servlet，需要在web.xml中去配置，不过在Servlet3.0后出现了基于注解的Servlet。

 仔细观察，你会发现，这个DispatcherServlet是自启动，而且传入了一个参数。

 要知道，在Spring MVC中，要想基于注解，需要在配置中指明扫描的包路径，就像这个样子：

 <context:component-scan base-package="com.zfz.myspringmvc">

 </context:component-scan>

 为了方便，我这里就通过初始化参数直接将需要扫描的基包路径传入。
 */


@WebServlet(name = "dispatcherServlet", urlPatterns = "/*", loadOnStartup = 1, initParams = {@WebInitParam(name="base-package", value = "com.linjw.miniMVC")})
public class DispatcherServlet extends HttpServlet{//ctrl + o 重写父类方法
    @Override
    public void init() throws ServletException {
       // super.init();
    }

    //扫描的基包
    private String basePackage = "";

    //基包下所有的带包路径权限定类名
    private List<String> packageNames = new ArrayList<String>();

    //注解实例化 注解上的名称:实例化对象
    private Map<String, Object> instanceMap = new HashMap<String, Object>();

    //带包路径的权限限定名称:注解上的名称
    private Map<String, String> nameMap = new HashMap<String, String>();

    //url地址和方法映射关系 springMVC就是方法调用链
    private Map<String, Method> urlMethodMap = new HashMap<String, Method>();

    //Method和权限定类名映射关系 为了通过Method找到该方法对象 利用反射执行
    private Map<Method, String> methodPackageMap = new HashMap<Method, String>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        basePackage = config.getInitParameter("base-package");
        System.out.println("basePackage：" + basePackage);
        //扫描基包得到全部带包路径权限定名
        try {
            scanBasePackage(basePackage);
            //把带 $Controller/@Service/@Repository的类实例化放到Map中 key为注解的名称
            instance(packageNames);
            //spring ioc注入
            springIOC();
            //完成url地址与方法的映射关系
            handlerUrlMethodMap();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    //扫描基包 (注意 基包是a.y.z格式 url x/y/z 需要转换)
    private void scanBasePackage(String basePackage) {
        //得到基包下的路径需要对basePackage做转换 替换为/
        //getClass()取得当前对象所属Class对象 getClassLoader()取得该Class对象的类装载器  getResource获取路径ß
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.","/"));
        System.out.println("url:" + url);
        File basePackageFile = new File(url.getPath());
        System.out.println("scan:" + basePackageFile);
        File[] childFiles = basePackageFile.listFiles();
        for (File file : childFiles) {
            if(file.isDirectory()) {//继续跟踪 递归扫描
                scanBasePackage(basePackage + "." + file.getName());
            } else if (file.isFile()) {
                //类似com.linjw...xxx.class 去掉class（后缀名）
                packageNames.add(basePackage + "." + file.getName().split("\\.")[0]);
            }
        }
    }


    //实例化 完成被注解标注的类的实例化 以及和注解名称的映射
    /**
     * new和newInstance
     * 相同：都可以创建一个类的实例
     * 不同：newInstance是通过反射创建对象的，在创建一个类的对象的时候，你可以对该类一无所知，
     * 一些开源框架比如Spring内部大都是通过反射来创建实例的，当然这种方法创建对象的时候必须拥有该类的句柄，
     * 甚至必要的时候还要有相关的权限设置（比如无参构造函数是私有的），
     * 而句柄是可以动态载入的，实际上JVM内部也是这样加载类的。
     * 该方法创建对象的时候，只会调用该类的无参数构造函数，
     * 不会调用其他的有参构造函数。
     * new 后面接类名参数，是最常见的创建实例的方式。这是必须要知道一个明确的类才能使用
     *  newInstance() 是java反射框架中类对象(Class)创建新对象的方法
     *  newInstance()  也经常见于工厂设计模式中，在该模式中，共产类的该方法返回一个工厂bean。
     *  如Factory factory = new Factory();
     *  Object obj = factory.newInstance();
     *
     *  getAnnotation 返回该程序元素上存在的指定类型的注解 如果该类型的注解不存在则返回null 加s返回全部注解
     *  isAnnotationPresent 判断该程序元素上是否包含指定类型的注解 返回true/false
     *  getDeclaredAnnotations 返回直接存在于此元素上的所有注解
     */

    private void instance(List<String> packageNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (packageNames.size() < 1) {
            return;
        }
        for (String string : packageNames) {
            Class c = Class.forName(string);

            if (c.isAnnotationPresent(Controller.class)) {
                Controller controller = (Controller) c.getAnnotation(Controller.class);
                String controllerName = controller.value();
                instanceMap.put(controllerName, c.newInstance());//注解上的名称 实例化对象
                nameMap.put(string, controllerName);
                System.out.println("Controller:" + string + "---value:" + controller.value());
            } else if (c.isAnnotationPresent(Service.class)) {
                Service service = (Service) c.getAnnotation(Service.class);
                String serviceName = service.value();
                instanceMap.put(serviceName, c.newInstance());
                nameMap.put(string, serviceName);
                System.out.println("Service:" + string + "---value:" + service.value());
            } else if (c.isAnnotationPresent(Repository.class)) {
                Repository repository = (Repository)c.getAnnotation(Repository.class);
                String repositoryName = repository.value();
                instanceMap.put(repositoryName, c.newInstance());
                nameMap.put(string, repositoryName);
                System.out.println("Repository:" + string + "---value:" + repository.value());
            }
        }
        System.out.println("instanceMap＝＝" + instanceMap);
    }

    //依赖注入 仿springIOC

    /**
     * Java反射中Field用于获取某个类的属性或该属性的属性值
     * getDeclaredFields
     * getFields()：获得某个类的所有的公共（public）的字段，包括父类中的字段。
     getDeclaredFields()：获得某个类的所有声明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
     * 具体编码如下：
    我们先创建一个POJO

    public class User {
        private long id;
        private String name;
        public void setId(long id) {
            this.id = id;
        }
        public void setName(String name) {
            this.name = name;
        }
        public long getId() {
            return id;
        }
        public String getName() {
            return name;
        }
    }
    再来获取此类中的所有字段
    Field[] fields = User.class.getDeclaredFields();
    获取字段的名称

    String fieldName = field.getName();
    获取字段的修饰符

    int fieldValue = field.getModifiers();//如：private、static、final等
    与某个具体的修饰符进行比较

    Modifier.isStatic(fieldValue)//看此修饰符是否为静态(static)
    获取字段的声明类型


    field.getType()；//返回的是一个class
    与某个类型进行比较

    field.getType() == Timestamp.class
    获取指定对象中此字段的值

    Object fieldObject= field.get(user);//user可以看做是从数据库中查找出来的对象

     */
    private void springIOC() throws IllegalAccessException {
        for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Qualifier.class)) {
                    String name = field.getAnnotation(Qualifier.class).value();
                    field.setAccessible(true);//使用构造器时不进行AccessibleTest类中的成员变量为private,故必须进行此操作 获取类的私有字段
                    field.set(entry.getValue(), instanceMap.get(name));
                }
            }
        }
    }

    //url映射处理 提取url映射到Controller的Method上
    private void handlerUrlMethodMap() throws ClassNotFoundException {
        if (packageNames.size() < 1) {
            return;
        }

        for (String string : packageNames) {
            Class c = Class.forName(string);
            if (c.isAnnotationPresent(Controller.class)) {
                Method[] methods = c.getMethods();
                StringBuffer baseUrl = new StringBuffer();
                if (c.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class);
                    baseUrl.append(requestMapping.value());
                }

                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        //RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class);
                        RequestMapping requestMapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
                        baseUrl.append(requestMapping.value());
                        urlMethodMap.put(baseUrl.toString(), method);
                        System.out.println("method:" + method + "=string:" + string);
                        methodPackageMap.put(method, string);

                    }
                }
                System.out.println("urlMethodMap:" + urlMethodMap);
            }
        }
    }

    //ctrl+o 重写post doGet方法

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("aaaaabbcc");
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.replaceAll(contextPath,"");
        System.out.println("uri:" + uri);
        System.out.println("path:" + path);
        //通过path找到method
        Method method = urlMethodMap.get(path);
        System.out.println("method:" + method);
        if (method!=null) {
            //通过Method拿到Controller对象 准备做反射执行
            String packageName = methodPackageMap.get(method);
            System.out.println(packageName);
            String controllerName = nameMap.get(packageName);
            UserController userController = (UserController) instanceMap.get(controllerName);
            try {
                /**
                 * 首先Method类代表一个方法，所以invoke（调用）就是调用Method类代表的方法。它可以让你实现动态调用，例如你可以动态的传人参数。下面是一个简单的例子。
                 public class MethodTest {
                     public static void main(String[] args) {
                         String [] names ={"tom","tim","allen","alice"};
                         Class<?> clazz = Test.class;
                     try {
                         Method method = clazz.getMethod("sayHi", String.class);
                         for(String name:names)
                            method.invoke(clazz.newInstance(),name);
                         } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                         } catch (IllegalAccessException e) {
                             e.printStackTrace();
                         } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                         } catch (InvocationTargetException e) {
                            e.printStackTrace();
                         } catch (InstantiationException e) {
                             e.printStackTrace();
                         }
                     }
                 }
                 class Test {
                    public void sayHi(String name) {
                         System.out.println("Hi "+name);
                    }
                 }
                 */
                method.setAccessible(true);
                method.invoke(userController);//调用类中的方法 需要先setAccessible越权获取类里面的东西
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
//        PrintWriter out = resp.getWriter();
//        out.println("Hello World");
    }
}
