package com.project.model.gameplay;

public class TimeCounter {

	private Gameplay gameplay;

	private long time;

	private long[] lastTime;

	public TimeCounter(Gameplay gameplay) {
		this.gameplay = gameplay;
		lastTime = new long[] { System.currentTimeMillis() };
		gameplay.getTimer().schedule(this::addElapsedTime, 1000);
		gameplay.getTimer().isPausedProperty().addListener(event -> pauseTimeCounting());
	}

	private void pauseTimeCounting() {
		if (gameplay.isPaused())
			addElapsedTime();
		else
			lastTime[0] = System.currentTimeMillis();
	}

	private void addElapsedTime() {
		long currentTime = System.currentTimeMillis();
		time += currentTime - lastTime[0];
		lastTime[0] = currentTime;
		gameplay.sendTimeNotifications();
	}

	public String getTime() {
		StringBuilder builder = new StringBuilder();
		appendTime(builder, time/1000, 0);
		return builder.toString();
	}

	private void appendTime(StringBuilder builder, long time, int number) {
		if (time / 60 > 0 && number < 2) {
			appendTime(builder, time/60, number+1);
			time %= 60;
		} else if (time / 24 > 0 && number == 2) {
			builder.append(time/24);
			builder.append("d ");
			time %= 24;
		}
		if(time<10)
			builder.append('0');
		builder.append(time);
		if (number != 0)
			builder.append(':');
	}

}