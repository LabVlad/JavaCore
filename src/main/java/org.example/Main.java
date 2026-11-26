package org.example;


import org.example.taskOne.StringBuilderSnapshot;

public class Main {
    public static void main(String[] args) {

        StringBuilderSnapshot a = new StringBuilderSnapshot("111");
        a.append("222");
        System.out.println(a); //111222
        a.undo();
        System.out.println(a);///111
        a.append("333");
        System.out.println(a);///111333
        a.append("444");
        System.out.println(a);//111333444
        a.undo();
        a.undo();
        System.out.println(a);//111
        a.insert(2, "555");
        System.out.println(a);//115551
        a.insert(6, "666");
        System.out.println(a);//115551666
        a.undo();
        System.out.println(a);//115551


    }
}