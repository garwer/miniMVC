package com.linjw.myAnnotation;

import java.lang.reflect.Field;

//上面的Apple類是使用我們自己定義的註解來對類成員進行修飾。接下來，我們獲取我們的註釋信息：

public class Test {
    //psvm
    public static void getFruitInfo(String clas){
        try {
            Class<?> cls = Class.forName(clas);
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                //isAnnotationPresent 判断该程序元素上是否包含指定类型的注解 返回true/false
                //getDeclaredAnnotations 返回直接存在于此元素上的所有注解
                if(field.isAnnotationPresent(FruitName.class)==true){
                    //getAnnotation 返回该程序元素上存在的指定类型的注解 如果该类型的注解不存在则返回null 加s返回全部注解
                    FruitName name = field.getAnnotation(FruitName.class);
                    System.out.println("Fruit Name:"+name.value());
                }
                if(field.isAnnotationPresent(FruitColor.class)){
                    FruitColor color = field.getAnnotation(FruitColor.class);
                    System.out.println("Fruit Color:"+color.fruitColor());
                }
                if(field.isAnnotationPresent(FruitProvider.class)){
                    FruitProvider Provider = field.getAnnotation(FruitProvider.class);
                    System.out.println("Fruit FruitProvider: ProviderID:"+Provider.id()+" Provider:"+Provider.user() +" ProviderAddress:"+Provider.address());
                }
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getFruitInfo("com.linjw.myAnnotation.Apple");
    }
}
