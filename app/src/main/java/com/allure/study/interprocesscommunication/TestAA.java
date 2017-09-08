package com.allure.study.interprocesscommunication;

/**
 * Created by Allure on 2017/9/8.
 */

public class TestAA {

    public static void main(String[] args) {

        test();

    }

    private static void test() {
        try {
            System.out.print(getS());
        } catch (Exception e) {
            System.out.print("exception");
            e.printStackTrace();
        }
        System.out.print(getS1());

    }

    private static String getS() {
        return null;
    }

    private static String getS1() {
        return "2";

    }
}
