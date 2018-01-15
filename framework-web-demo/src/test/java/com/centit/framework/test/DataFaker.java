package com.centit.framework.test;

import com.github.javafaker.Faker;

public class DataFaker {
    public static void main(String ... args){
        Faker faker = new Faker();
        System.out.println(faker.address().buildingNumber());
    }
}
