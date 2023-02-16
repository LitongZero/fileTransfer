package com.ltz.entity;

public class Myfile {
    private String name;
    private String length;

    public Myfile(String name, String length) {
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public Myfile setName(String name) {
        this.name = name;
        return this;
    }

    public String getLength() {
        return length;
    }

    public Myfile setLength(String length) {
        this.length = length;
        return this;
    }
}
