package com.linjw.myAnnotation;

public class Apple {

    @FruitName(value="FuShi Apple")
    private String fruitName;

    @FruitColor(fruitColor= FruitColor.Color.RED)
    private String fruitColor;

    @FruitProvider(id=1,user="Tom",address="China")
    private FruitProvider provider;

}
