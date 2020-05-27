package test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


public class TestArrayBlockingQueue {

	static ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(5);
	
	public static class RunA implements Runnable{
		int i;
		public RunA(int i){
			this.i = i;
		}
		@Override
		public void run() {
				while(true){
					System.out.println("offer"+i+" size="+queue.size());
					while(!queue.offer("haha")){
						queue.poll();
						System.out.println("poll"+i+" size="+queue.size());
					}
					List<String> list = Arrays.asList(queue.toArray(new String[queue.size()]));
					System.out.println("list"+i+" size="+list.size());
				}
		}
	}
	public static void main(String[] str){
		for(int i=0;i<10;i++){
			RunA runA = new RunA(i);
			Thread t = new Thread(runA);
			t.start();
		}
	}
}
