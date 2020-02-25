package com.guptae.profiler.application;

public class Launcher {
	public static void main(String[] args) throws Exception {
        if(args[0].equals("StartMyAtmApplication")) {
            new MyATMApplication().run(args);
        } /*else if(args[0].equals("LoadAgent")) {
            new AgentLoader().run(args);
        }*/
    }
}
