package com.mango.ipc;

/**
 * Author:mango
 * Time:2019/6/20 09:46
 * Version:1.0.0
 * Desc:TODO()
 */

public class Student {

    private String name;

    public Student(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                '}';
    }
}
