package com.cy.wbs;

import android.os.Handler;
import android.os.Looper;

/**
 * @author tangtt
 * UITimer 类似与Timer, task相当于TimerTask，只是task在ui线程中执行
 *
 */
public class UITimer {
	private Handler handler = new Handler(Looper.getMainLooper());
	
	private boolean cancelled = false;
	
	public UITimer() {
	}
	
	/**
	 * 仅在UI线程执行一次任务
	 * @param task： 需要执行的任务
	 */
	public void runOnce(Runnable task) {
		runOnce(task,0);
	}
	

	/**
	 * 在延时一段时间后执行一次任务
	 * @param task
	 * @param delayMills 
	 * 		延时时间
	 */
	public void runOnce(Runnable task,long delayMills) {
		schedule(task,delayMills,0,0);
	}
	
	
	/**
	 * Schedule a task for repeated fixed-delay execution after a specific time
     * has been reached.
	 * @param task：                             需要执行的任务
	 * @param periodMillis:  执行周期，以毫秒为单位
	 */
	public void schedule(Runnable task,long periodMillis) {
		schedule(task,0,periodMillis,INFINITE);
	}
	
	
		

	/**
	 * Schedule a task for repeated fixed-delay execution after a specific time
     * has been reached.
	 * @param task：                             需要执行的任务
	 * @param delayMillis :  第一次执行的延时时间
	 * @param periodMillis:  执行周期，以毫秒为单位
	 */
	public void schedule(Runnable task,long delayMillis,long periodMillis) {
		schedule(task,delayMillis,periodMillis,INFINITE);
	}
	
	
	/**
	 * Schedule a task for repeated fixed-delay execution after a specific time
     * has been reached.当任务执行完times次后，不在继续执行
	 * @param task：                             需要执行的任务
	 * @param delayMillis :  第一次执行的延时时间
	 * @param periodMillis:  执行周期，以毫秒为单位
	 * @param times:         执行次数,{@link #INFINITE}为无穷次
	 */
	public void schedule(Runnable task,long delayMillis,long periodMillis,int times) {
		schedule(task,delayMillis,periodMillis,INFINITE,INFINITE,true);
	}
	
	
	/**
	 * 周期性地执行任务直到达到给定次数
	 * @param task：                             需要执行的任务
	 * @param periodMillis:  执行周期，以毫秒为单位
	 * @param times:         执行次数,{@link #INFINITE}为无穷次
	 */
	public void scheduleTimes(Runnable task,long periodMillis,int times) {
		schedule(task,0,periodMillis,times,INFINITE,true);
	}
	
	/**
	 * 在给定时间内，周期性地执行任务
	 * @param task：                             需要执行的任务
	 * @param periodMillis:  执行周期，以毫秒为单位
	 * @param durationSecs:  执行时间,{@link #INFINITE}为无穷
	 */
	public void scheduleDuration(Runnable task,long periodMillis,int durationSecs) {
		schedule(task,0,periodMillis,INFINITE,durationSecs,false);
	}
	
	/**
	 * Schedule a task for repeated fixed-delay execution after a specific time
     * has been reached.当任务执行完times次后，不在继续执行
	 * @param task：                             需要执行的任务
	 * @param delayMillis :  第一次执行的延时时间
	 * @param periodMillis:  执行周期，以毫秒为单位
	 * @param times:         执行次数,{@link #INFINITE}为无穷次
	 */
	private void schedule(Runnable task,long delayMillis,long periodMillis,
			int times,int duration,boolean checkTimes) {
		cancelled = false;
		//可能用户已经在执行一个task，此时先移除之前的task
		handler.removeCallbacks(r);
		
		this.task = task;
		this.interval = periodMillis;
		this.checkTimes = checkTimes;
		if(checkTimes) {
			this.times = times;
		} else {
			this.duration = duration;
			this.startTime = System.currentTimeMillis();
		}
		
		if(delayMillis <= 0) {
			handler.post(r);
		} else {
			handler.postDelayed(r, delayMillis);
		}
	}
	
	public void cancel() {
		cancelled = true;
		handler.removeCallbacks(r);
	}
	
	
	
	/**
	 * 获取剩余执行次数
	 * @return 
	 */
	public int getRemainTimes() {
		return times;
	}
	
	

	private Runnable r = new Runnable() {
		@Override
		public void run() {
			task.run();
			
			if(cancelled) {
				return;
			}
						
			if(checkTimes) {				
				if(times == INFINITE) {
					handler.postDelayed(this,interval);
				} else if(times > 1){
					times--;
					handler.postDelayed(this,interval);
				}
			} else {
				if(duration == INFINITE) {
					handler.postDelayed(this,interval);
				} else {
					long currTime = System.currentTimeMillis();
					if((currTime - startTime) / 1000 < duration ) {
						handler.postDelayed(this,interval);
					}
				}
			}
		}
	};
	
	
	private Runnable task;
	private long interval;
	private int times;
	private int duration;
	private long startTime;
	private boolean checkTimes;
	
	public final static int INFINITE = -1;
	
}
