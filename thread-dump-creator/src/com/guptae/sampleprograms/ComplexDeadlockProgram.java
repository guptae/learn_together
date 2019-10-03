package com.guptae.sampleprograms;

public class ComplexDeadlockProgram {

	public static void main(String[] args) throws Exception {

		Object resourceA = new Object();
		Object resourceB = new Object();
		Object resourceC = new Object();
		
		Thread threadLockingAFirst = new Thread(new DeadlockRunnable(resourceA, resourceB));
		Thread threadLockingBFirst = new Thread(new DeadlockRunnable(resourceB, resourceC));
		Thread threadLockingCFirst = new Thread(new DeadlockRunnable(resourceC, resourceA));
		
		threadLockingAFirst.start();
		Thread.sleep(500);
		threadLockingBFirst.start();
		Thread.sleep(500);
		threadLockingCFirst.start();
	}

	public static class DeadlockRunnable implements Runnable
	{
		private final Object firstResource;
		private final Object secondResource;
//		private final Object thirdResource;
		
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
