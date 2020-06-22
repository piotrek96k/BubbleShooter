package com.project.model.gameplay;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.project.model.bubble.Bubble;
import com.project.model.bubble.BubbleColor;
import com.project.model.listener.BubbleListener;
import com.project.model.listener.MoveListener;
import com.project.model.timer.PausableTimer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;

public class Gameplay {

	public static final double HEIGHT_COEFFICIENT = Math.pow(3, 0.5) / 2;

	public final int ROWS;

	public final int COLUMNS;

	public final double ROW_HEIGHT;

	public final double HEIGHT;

	public final double WIDTH;

	public final double BUBBLES_HEIGHT;

	private boolean[] isMoving = { false };

	private boolean[] isWaiting = { false };

	private int rowOffset;

	private Shooter shooter;

	private Remover remover;

	private ColorsCounter colorsCounter;

	private Bubble[][] bubbles;

	private Bubble bubbleToThrow;

	private Bubble nextBubble;

	private Object locker = new Object();

	private PausableTimer timer = new PausableTimer(true);

	private BooleanProperty finished = new SimpleBooleanProperty();

	private Runnable goDown = new GoDown();

	private List<BubbleListener> bubbleAddedListeners = new LinkedList<>();

	private List<BubbleListener> bubbleRemovedListeners = new LinkedList<>();

	private List<BubbleListener> bubbleChangedListeners = new LinkedList<>();

	private List<MoveListener> moveListeners = new LinkedList<>();

	public Gameplay(int rows, int columns, double diameter) {
		ROWS = rows + 1;
		COLUMNS = columns;
		ROW_HEIGHT = diameter * HEIGHT_COEFFICIENT;
		BUBBLES_HEIGHT = (rows - 1) * ROW_HEIGHT + diameter;
		HEIGHT = BUBBLES_HEIGHT + 3 * diameter;
		WIDTH = (COLUMNS + 0.5) * diameter;
		bubbles = new Bubble[ROWS][COLUMNS];
		colorsCounter = new ColorsCounter();
		remover = new Remover(this, colorsCounter);
		shooter = new Shooter(this, remover, colorsCounter);
		Bubble.setDiameter(diameter);
		Bubble.setOffset(2.0);
		finished.addListener(observable -> System.out.println("Koniec gry"));
	}

	private class GoDown implements Runnable {

		@Override
		public void run() {
			Runnable runnable = () -> {
				synchronized (locker) {
					while (isMoving[0]) {
						try {
							isWaiting[0] = true;
							locker.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (!finished.get())
						moveBubblesDown();
				}
			};
			Thread thread = new Thread(runnable, "goDownThread");
			thread.setDaemon(true);
			thread.start();
		}

		private void moveBubblesDown() {
			switchRowOffset();
			for (int i = ROWS - 1; i > 0; i--)
				for (int j = 0; j < COLUMNS; j++) {
					if (bubbles[i - 1][j] != null) {
						bubbles[i][j] = bubbles[i - 1][j];
						bubbles[i - 1][j] = null;
						bubbles[i][j].setCenterY(getCenterY(i));
						sendBubbleChangedNotifications(bubbles[i][j]);
						if (i == ROWS - 1)
							finished.set(true);
					}
				}
			initRow();
		}

		private void initRow() {
			double yCoordinate = getCenterY(0);
			for (int j = 0; j < COLUMNS; j++) {
				double xCoordinate = getCenterX(0, j);
				BubbleColor color;
				do
					color = getRandomBubbleColor();
				while (colorsCounter.getQuantity(color) == 0);
				colorsCounter.increment(color);
				bubbles[0][j] = new Bubble(xCoordinate, yCoordinate, color);
				sendBubbleAddedNotifications(bubbles[0][j]);
			}
		}

		private void switchRowOffset() {
			if (rowOffset == 0)
				rowOffset = 1;
			else
				rowOffset = 0;
		}

	}

	public void init() {
		initBubbles();
		nextBubble = new Bubble(WIDTH / 2, BUBBLES_HEIGHT + (HEIGHT - BUBBLES_HEIGHT) / 2, getRandomBubbleColor());
		sendBubbleAddedNotifications(nextBubble);
		setBubbleToThrow();
		createNewTask();
		finished.addListener(observable -> timer.cancel());
	}

	private void createNewTask() {
		timer.schedule(goDown, 30_000, 30_000);
	}

	public void setBubbleToThrow() {
		bubbleToThrow = nextBubble;
		bubbleToThrow.setCenterX(WIDTH / 2);
		sendBubbleChangedNotifications(bubbleToThrow);
		BubbleColor color;
		do
			color = getRandomBubbleColor();
		while (colorsCounter.getQuantity(color) == 0);
		nextBubble = new Bubble(WIDTH / 3, BUBBLES_HEIGHT + (HEIGHT - BUBBLES_HEIGHT) / 2, color);
		sendBubbleAddedNotifications(nextBubble);
	}

	private void initBubbles() {
		for (int i = 0; i < ROWS / 2; i++) {
			double yCoordinate = getCenterY(i);
			for (int j = 0; j < COLUMNS; j++) {
				double xCoordinate = getCenterX(i, j);
				BubbleColor color = getRandomBubbleColor();
				colorsCounter.increment(color);
				bubbles[i][j] = new Bubble(xCoordinate, yCoordinate, color);
				sendBubbleAddedNotifications(bubbles[i][j]);
			}
		}
	}

	public void setStopMoving() {
		if (checkIfGameEnded()) {
			finished.set(true);
		} else {
			boolean changed = false;
			while (colorsCounter.getQuantity(nextBubble.getColor()) == 0) {
				changed = true;
				nextBubble.setColor(getRandomBubbleColor());
			}
			if (changed)
				sendBubbleChangedNotifications(nextBubble);
			setBubbleToThrow();
		}
		isMoving[0] = false;
		synchronized (locker) {
			if (isWaiting[0]) {
				isWaiting[0] = false;
				locker.notifyAll();
			}
		}
	}

	private boolean checkIfGameEnded() {
		for (Bubble bubble : bubbles[ROWS - 1])
			if (bubble != null)
				return true;
		for (Bubble bubble : bubbles[0])
			if (bubble != null)
				return false;
		return true;
	}

	public void setStartMoving() {
		isMoving[0] = true;
	}

	public double getCenterY(int row) {
		return row * ROW_HEIGHT + 0.5 * Bubble.getDiameter();
	}

	public double getCenterX(int row, int column) {
		double result = column * Bubble.getDiameter() + 0.5 * Bubble.getDiameter();
		int rowToCheck = row + rowOffset;
		if (rowToCheck % 2 == 1)
			result += 0.5 * Bubble.getDiameter();
		return result;
	}

	public void pauseOrResume() {
		if (timer.isPaused())
			timer.resume();
		else
			timer.pause();
	}

	public void throwBubble(double x, double y) {
			shooter.throwBubble(x, y);
	}

	public List<Point2D> getLinePoints(double x, double y) {
		return shooter.getLinePoints(x, y);
	}

	public BubbleColor getRandomBubbleColor() {
		Random random = new Random();
		BubbleColor[] colors = BubbleColor.values();
		return colors[random.nextInt(colors.length)];
	}

	public void finishGame() {
		finished.set(true);
	}

	private void sendNotifications(List<BubbleListener> listeners, Bubble bubble) {
		listeners.forEach(listener -> listener.bubbleChanged(bubble));
	}

	public void sendBubbleAddedNotifications(Bubble bubble) {
		sendNotifications(bubbleAddedListeners, bubble);
	}

	public void sendBubbleRemovedNotifications(Bubble bubble) {
		sendNotifications(bubbleRemovedListeners, bubble);
	}

	public void sendBubbleChangedNotifications(Bubble bubble) {
		sendNotifications(bubbleChangedListeners, bubble);
	}

	public void addBubbleAddedListener(BubbleListener listener) {
		bubbleAddedListeners.add(listener);
	}

	public void removeBubbleAddedListener(BubbleListener listener) {
		bubbleAddedListeners.remove(listener);
	}

	public void addBubbleRemovedListener(BubbleListener listener) {
		bubbleRemovedListeners.add(listener);
	}

	public void removeBubbleRemovedListener(BubbleListener listener) {
		bubbleRemovedListeners.remove(listener);
	}

	public void addBubbleChangedListener(BubbleListener listener) {
		bubbleChangedListeners.add(listener);
	}

	public void removeBubbleChangedListener(BubbleListener listener) {
		bubbleChangedListeners.remove(listener);
	}

	public void addMoveListener(MoveListener listener) {
		moveListeners.add(listener);
	}

	public void removeMoveListener(MoveListener listener) {
		moveListeners.remove(listener);
	}

	public void sendMovedNotofications() {
		for (MoveListener listener : moveListeners)
			listener.moved();
	}

	public Bubble[][] getBubbles() {
		return bubbles;
	}

	public Bubble getBubbleToThrow() {
		return bubbleToThrow;
	}

	public Bubble getNextBubble() {
		return nextBubble;
	}

	public PausableTimer getTimer() {
		return timer;
	}

	public ReadOnlyBooleanProperty getFinishedProperty() {
		return (ReadOnlyBooleanProperty) finished;
	}

	public boolean isMoving() {
		return isMoving[0];
	}

	public int getRowOffset() {
		return rowOffset;
	}

}