package com.guptae.profiler.application;

public class MyATMApplication {

    public void run(String[] args) throws Exception {
        System.out.println("[Application] Starting ATM application");
        for(int i=1; i<args.length; i++)
        {
            MyATM.withdrawMoney(Integer.parseInt(args[i]));
            Thread.sleep(2000);
        }
    }

}