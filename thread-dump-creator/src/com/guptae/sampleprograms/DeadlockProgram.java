package com.guptae.sampleprograms;

public class DeadlockProgram {

	public static void main(String[] args) throws Exception {

		Object resourceA = new Object();
		Object resourceB = new Object();
		
		Thread threadLockingAFirst = new Thread(new DeadlockRunnable(resourceA, resourceB));
		Thread threadLockingBFirst = new Thread(new DeadlockRunnable(resourceB, resourceA));
		
		threadLockingAFirst.start();
		Thread.sleep(500);
		threadLockingBFirst.start();
	}

	public static class DeadlockRunnable implements Runnable
	{
		private final Object firstResource;
		private final Object secondResource;
		
		public DeadlockRunnable(Object firstObj, Object secondObj)
		{
			this.firstResource = firstObj;
			this.secondResource = secondObj;
		}
		
		public void run()
		{
			try{
				synchronized (firstResource) {
					printThreadInfo(firstResource);
					Thread.sleep(1000);
					synchronized (secondResource) {
						printThreadInfo(secondResource);
					}
				}
			} catch(InterruptedException e)
			{
				System.out.println("Interruption Occurred: " + e);
			}
		}
		
		public void printThreadInfo(Object resource)
		{
			System.out.println(Thread.currentThread().getName() + " is locked on " + resource);
		}
	}
}
