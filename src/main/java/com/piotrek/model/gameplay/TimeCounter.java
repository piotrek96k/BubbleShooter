package com.piotrek.model.gameplay;

public class TimeCounter {

    private final Gameplay gameplay;

    private long time;

    private final long[] lastTime;

    public TimeCounter(Gameplay gameplay) {
        this.gameplay = gameplay;
        lastTime = new long[]{System.currentTimeMillis()};
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

    public static String getSimpleFormattedTime(long time) {
        StringBuilder builder = new StringBuilder();
        appendTime(builder, time / 1000, 0);
        return builder.toString();
    }

    public static String getFullFormattedTime(long time) {
        time /= 1000;
        int seconds = (int) time % 60;
        time /= 60;
        int minutes = (int) time % 60;
        time /= 60;
        int hours = (int) time % 24;
        time /= 24;
        int days = (int) time;
        return String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
    }

    private static void appendTime(StringBuilder builder, long time, int number) {
        if (time / 60 > 0 && number < 2) {
            appendTime(builder, time / 60, number + 1);
            time %= 60;
        } else if (time / 24 > 0 && number == 2) {
            builder.append(time / 24);
            builder.append("d ");
            time %= 24;
        }
        builder.append(String.format("%02d", time));
        if (number != 0)
            builder.append('\u2236');
    }

    public long getTime() {
        return time;
    }

}