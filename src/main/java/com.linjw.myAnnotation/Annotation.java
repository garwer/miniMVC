package com.linjw.myAnnotation;


import java.lang.annotation.*;

//几个常用注解
//@Override、@SuppressWarnings等等，@Override表示重寫父類的方法，@SuppressWarnings表示忽略警告信息。



//jdk自带注解
/**
 * @Target：

　　　@Target说明了Annotation所修饰的对象范围：Annotation可被用于 packages、types（类、接口、枚举、Annotation类型）、类型成员（方法、构造方法、成员变量、枚举值）、方法参数和本地变量（如循环变量、catch参数）。在Annotation类型的声明中使用了target可更加明晰其修饰的目标。
　　作用：用于描述注解的使用范围（即：被描述的注解可以用在什么地方）
　　取值(ElementType)有：

　　　　1.CONSTRUCTOR:用于描述构造器
　　　　2.FIELD:用于描述域
　　　　3.LOCAL_VARIABLE:用于描述局部变量
　　　　4.METHOD:用于描述方法
　　　　5.PACKAGE:用于描述包
　　　　6.PARAMETER:用于描述参数
　　　　7.TYPE:用于描述类、接口(包括注解类型) 或enum声明
 */

@Target(ElementType.TYPE)
@interface Table {
    /**
     * 数据表名称注解，默认值为类名称
     * @return
     */
    public String tableName() default "className";
}


//注解Table 可以用于注解类、接口(包括注解类型) 或enum声明,而注解NoDBColumn仅可用于注解类的成员变量。

@Target(ElementType.FIELD)
@interface NoDBColumn {

}

/**
 *@Retention：

　　@Retention定义了该Annotation被保留的时间长短：某些Annotation仅出现在源代码中，而被编译器丢弃；而另一些却被编译在class文件中；编译在class文件中的Annotation可能会被虚拟机忽略，而另一些在class被装载时将被读取（请注意并不影响class的执行，因为Annotation与class在使用上是被分离的）。使用这个meta-Annotation可以对 Annotation的“生命周期”限制。

　　作用：表示需要在什么级别保存该注释信息，用于描述注解的生命周期（即：被描述的注解在什么范围内有效）

　　取值（RetentionPoicy）有：

　　　　1.SOURCE:在源文件中有效（即源文件保留）
　　　　2.CLASS:在class文件中有效（即class保留）
　　　　3.RUNTIME:在运行时有效（即运行时保留）
　　    Retention meta-annotation类型有唯一的value作为成员，它的取值来自java.lang.annotation.RetentionPolicy的枚举类型值。具体实例如下：
 *
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Column {
    public String name() default "fieldName";
    public String setFuncName() default "setField";
    public String getFuncName() default "getField";
    public boolean defaultDBValue() default false;
}

//Column注解的的RetentionPolicy的属性值是RUTIME,这样注解处理器可以通过反射，获取到该注解的属性值，从而去做一些运行时的逻辑处理


/**
 * 　@Documented:
　　 @Documented用于描述其它类型的annotation应该被作为被标注的程序成员的公共API，因此可以被例如javadoc此类的工具文档化。Documented是一个标记注解，没有成员。
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Column1 {
    public String name() default "fieldName";
    public String setFuncName() default "setField";
    public String getFuncName() default "getField";
    public boolean defaultDBValue() default false;
}

//@Documented 将此注解包含在 javadoc 中 ，它代表着此注解会被javadoc工具提取成文档。在doc文档中的内容会因为此注解的信息内容不同而不同。

/**
 *　　@Inherited：

　　@Inherited 元注解是一个标记注解，@Inherited阐述了某个被标注的类型是被继承的。如果一个使用了@Inherited修饰的annotation类型被用于一个class，则这个annotation将被用于该class的子类。

　　注意：@Inherited annotation类型是被标注过的class的子类所继承。类并不从它所实现的接口继承annotation，方法并不从它所重载的方法继承annotation。

　　当@Inherited annotation类型标注的annotation的Retention是RetentionPolicy.RUNTIME，则反射API增强了这种继承性。如果我们使用java.lang.reflect去查询一个@Inherited annotation类型的annotation时，反射代码检查将展开工作：检查class和其父类，直到发现指定的annotation类型被发现，或者到达类继承结构的顶层。
 *
 */

@Inherited
@interface Greeting {
    public enum FontColor{ BULE,RED,GREEN};
    String name();
    FontColor fontColor() default FontColor.GREEN;
}

