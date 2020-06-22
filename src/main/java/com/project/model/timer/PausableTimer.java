package com.project.model.timer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PausableTimer {

	private boolean isPaused = false;

	private boolean isCanceled;

	private Timer timer;

	private Object locker = new Object();

	private Map<Runnable, Long> timesMap = new HashMap<>();

	private Map<Runnable, Long> delaysMap = new HashMap<>();

	private Map<Runnable, TimerTask> timerTasks = new HashMap<>();

	public PausableTimer(boolean isDeamon) {
		timer = new Timer(isDeamon);
	}

	public void schedule(Runnable runnable, long initDelay, long delay) {
		if (isCanceled)
			throw new IllegalStateException();
		if (!isPaused) {
			delaysMap.put(runnable, delay);
			createTimerTask(runnable, initDelay, delay);
		}
	}

	public void cancelTask(Runnable runnable) {
		synchronized (locker) {
			if (isCanceled)
				throw new IllegalStateException();
			TimerTask timerTask = timerTasks.get(runnable);
			timerTasks.remove(runnable, timerTask);
			timerTask.cancel();
			timesMap.remove(runnable, timesMap.get(runnable));
			delaysMap.remove(runnable, delaysMap.get(runnable));
		}
	}

	private void createTimerTask(Runnable runnable, long initDelay, long delay) {
		timesMap.put(runnable, System.currentTimeMillis());
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				synchronized (locker) {
					runnable.run();
					TimerTask task = timerTasks.get(runnable);
					if (task == null) {
						timesMap.remove(runnable, timesMap.get(runnable));
						return;
					}
					timesMap.put(runnable, System.currentTimeMillis());
				}
			}
		};
		timerTasks.put(runnable, timerTask);
		timer.schedule(timerTask, initDelay, delay);
	}

	public void pause() {
		synchronized (locker) {
			if (isCanceled)
				throw new IllegalStateException();
			if (!isPaused) {
				for (Runnable runnable : timesMap.keySet()) {
					long time = timesMap.get(runnable);
					timesMap.put(runnable, delaysMap.get(runnable) - (System.currentTimeMillis() - time));
				}
				for (TimerTask timerTask : timerTasks.values())
					timerTask.cancel();
				timerTasks.clear();
				isPaused = true;
			}
		}
	}

	public void resume() {
		synchronized (locker) {
			if (isCanceled)
				throw new IllegalStateException();
			if (isPaused) {
				for (Runnable runnable : timesMap.keySet())
					createTimerTask(runnable, timesMap.get(runnable), delaysMap.get(runnable));
				isPaused = false;
			}
		}
	}

	public boolean isPaused() {
		synchronized (locker) {
			return isPaused;
		}
	}

	public void cancel() {
		synchronized (locker) {
			timer.cancel();
			isCanceled = true;
		}
	}

}