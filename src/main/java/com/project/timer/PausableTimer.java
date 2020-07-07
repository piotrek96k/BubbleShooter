package com.project.timer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PausableTimer {

	private boolean canceled;

	private BooleanProperty paused;

	private Timer timer;

	private Map<Runnable, Long> timesMap;

	private Map<Runnable, Long> delaysMap;

	private Map<Runnable, Long> initDelaysMap;

	private Map<Runnable, TimerTask> timerTasks;

	{
		paused = new SimpleBooleanProperty(false);
		timesMap = new HashMap<Runnable, Long>();
		delaysMap = new HashMap<Runnable, Long>();
		initDelaysMap = new HashMap<Runnable, Long>();
		timerTasks = new HashMap<Runnable, TimerTask>();
	}

	public PausableTimer(boolean isDeamon) {
		timer = new Timer(isDeamon);
	}

	public synchronized void schedule(Runnable runnable, long delay) {
		if (canceled)
			throw new IllegalStateException();
		if (!paused.get()) {
			delaysMap.put(runnable, delay);
			initDelaysMap.put(runnable, 0L);
			createTimerTask(runnable, delay, delay);
		}
	}

	public synchronized void cancelTask(Runnable runnable) {
		if (canceled)
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
		if (canceled)
			throw new IllegalStateException();
		if (!paused.get()) {
			for (Runnable runnable : timesMap.keySet()) {
				long time = timesMap.get(runnable);
				time = (System.currentTimeMillis() - time) + initDelaysMap.get(runnable);
				timesMap.put(runnable, time);
			}
			for (TimerTask timerTask : timerTasks.values())
				timerTask.cancel();
			timerTasks.clear();
			paused.set(true);
		}
	}

	public synchronized void resume() {
		if (canceled)
			throw new IllegalStateException();
		if (paused.get()) {
			for (Runnable runnable : timesMap.keySet()) {
				initDelaysMap.put(runnable, timesMap.get(runnable));
				long time = Math.abs(delaysMap.get(runnable) - timesMap.get(runnable));
				createTimerTask(runnable, time, delaysMap.get(runnable));
			}
			paused.set(false);
		}
	}

	public synchronized ReadOnlyBooleanProperty isPausedProperty() {
		return (ReadOnlyBooleanProperty) paused;
	}

	public synchronized boolean isPaused() {
		return paused.get();
	}

	public synchronized void cancel() {
		canceled = true;
		timer.cancel();
	}

}