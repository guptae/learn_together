package com.guptae.profiler.application;

public class MyATM {
    private static final int account = 10;

    public static void withdrawMoney(int amount) throws InterruptedException {
        Thread.sleep(amount); //processing going on here
        System.out.println("[Application] Successful Withdrawal of [" + amount + "] units!");

    }
    
    public static void depositMoney(int amount) throws InterruptedException {
        Thread.sleep(2000); //processing going on here
        System.out.println("[Application] Successful deposit of [" + amount + "] units!");

    }    
}
