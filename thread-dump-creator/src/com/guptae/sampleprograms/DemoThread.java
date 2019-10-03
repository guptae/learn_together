package com.guptae.sampleprograms;

public class DemoThread implements Runnable {

	@Override
	public void run() {

		// moving thread2 to timed waiting state 
        try
        { 
            Thread.sleep(1500); 
        }  
        catch (InterruptedException e)  
        { 
            e.printStackTrace(); 
        } 
          
        System.out.println("State of thread1 while it called join() method on thread2 -"+ 
            TestThreadState.thread1.getState()); 
        try
        { 
            Thread.sleep(20000); 
        }  
        catch (InterruptedException e)  
        { 
            e.printStackTrace(); 
        }      

	}

}
