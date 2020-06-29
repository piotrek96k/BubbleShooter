package com.project.timer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PausableTimer {

	private boolean isPaused;

	private boolean isCanceled;

	private Timer timer;

	private Map<Runnable, Long> timesMap;

	private Map<Runnable, Long> delaysMap;

	private Map<Runnable, Long> initDelaysMap;

	private Map<Runnable, TimerTask> timerTasks;

	{
		timesMap = new HashMap<Runnable, Long>();
		delaysMap = new HashMap<Runnable, Long>();
		initDelaysMap = new HashMap<Runnable, Long>();
		timerTasks = new HashMap<Runnable, TimerTask>();
	}

	public PausableTimer(boolean isDeamon) {
		timer = new Timer(isDeamon);
	}

	public synchronized void schedule(Runnable runnable, long delay) {
		if (isCanceled)
			throw new IllegalStateException();
		if (!isPaused) {
			delaysMap.put(runnable, delay);
			initDelaysMap.put(runnable, 0L);
			createTimerTask(runnable, delay, delay);
		}
	}

	public synchronized void cancelTask(Runnable runnable) {
		if (isCanceled)
			throw new IllegalStateException();
		TimerTask timerTask = timerTasks.get(runnable);
		if (timerTask != null) {
			timerTasks.remove(runnable, timerTask);
			timerTask.cancel();
			timesMap.remove(runnable, timesMap.get(runnable));
			delaysMap.remove(runnable, delaysMap.get(runnable));
			initDelaysMap.remove(runnable, initDelaysMap.get(runnable));
		}
	}

	private void createTimerTask(Runnable runnable, long initDelay, long delay) {
		timesMap.put(runnable, System.currentTimeMillis());
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				runnable.run();
				TimerTask task = timerTasks.get(runnable);
				if (task == null) {
					timesMap.remove(runnable, timesMap.get(runnable));
					return;
				}
				if (initDelaysMap.get(runnable) != 0L)
					initDelaysMap.put(runnable, 0L);
				timesMap.put(runnable, System.currentTimeMillis());
			}
		};
		timerTasks.put(runnable, timerTask);
		timer.schedule(timerTask, initDelay, delay);
	}

	public synchronized void pause() {
		if (isCanceled)
			throw new IllegalStateException();
		if (!isPaused) {
			for (Runnable runnable : timesMap.keySet()) {
				long time = timesMap.get(runnable);
				time = (System.currentTimeMillis() - time) + initDelaysMap.get(runnable);
				timesMap.put(runnable, time);
			}
			for (TimerTask timerTask : timerTasks.values())
				timerTask.cancel();
			timerTasks.clear();
			isPaused = true;
		}
	}

	public synchronized void resume() {
		if (isCanceled)
			throw new IllegalStateException();
		if (isPaused) {
			for (Runnable runnable : timesMap.keySet()) {
				initDelaysMap.put(runnable, timesMap.get(runnable));
				long time = Math.abs(delaysMap.get(runnable) - timesMap.get(runnable));
				createTimerTask(runnable, time, delaysMap.get(runnable));
			}
			isPaused = false;
		}
	}

	public synchronized boolean isPaused() {
		return isPaused;
	}

	public synchronized void cancel() {
		isCanceled = true;
		timer.cancel();
	}

}