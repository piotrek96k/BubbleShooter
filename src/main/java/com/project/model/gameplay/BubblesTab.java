package com.project.model.gameplay;

import java.util.Random;

import com.project.model.bubble.BombBubble;
import com.project.model.bubble.Bubble;
import com.project.model.bubble.BubbleColor;
import com.project.model.bubble.OrdinaryBubble;

public class BubblesTab {

	public static final double HEIGHT_COEFFICIENT = Math.pow(3, 0.5) / 2;

	public final int ROWS;

	public final int COLUMNS;

	public final double ROW_HEIGHT;

	public final double HEIGHT;

	public final double WIDTH;

	public final double BUBBLES_HEIGHT;

	private Bubble[][] bubbles;

	private Bubble bubbleToThrow;

	private Bubble nextBubble;

	private Runnable goDown = new GoDown();

	private Gameplay gameplay;

	private Random random = new Random();

	private Object locker = new Object();

	private boolean[] isWaiting = { false };

	private int rowOffset;

	public BubblesTab(Gameplay gameplay, int rows, int columns, double diameter) {
		ROWS = rows + 1;
		COLUMNS = columns;
		ROW_HEIGHT = diameter * HEIGHT_COEFFICIENT;
		BUBBLES_HEIGHT = (rows - 1) * ROW_HEIGHT + diameter;
		HEIGHT = BUBBLES_HEIGHT + 3 * diameter;
		WIDTH = (COLUMNS + 0.5) * diameter;
		bubbles = new Bubble[ROWS][COLUMNS];
		this.gameplay = gameplay;
	}

	private class GoDown implements Runnable {

		@Override
		public void run() {
			Runnable runnable = () -> {
				synchronized (locker) {
					while (gameplay.isMoving()) {
						try {
							isWaiting[0] = true;
							locker.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					isWaiting[0] = false;
					if (!gameplay.isFinishedProperty().get())
						moveBubblesDown();
				}
			};
			Thread thread = new Thread(runnable, "Go Down Thread");
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
						gameplay.sendBubbleChangedNotifications(bubbles[i][j]);
						if (i == ROWS - 1)
							gameplay.finishGame();
						;
					}
				}
			initRow();
		}

		private void initRow() {
			double yCoordinate = getCenterY(0);
			for (int j = 0; j < COLUMNS; j++) {
				double xCoordinate = getCenterX(0, j);
				BubbleColor color = getRandomBubbleColorIfColorExists();
				gameplay.getColorsCounter().increment(color);
				bubbles[0][j] = new OrdinaryBubble(xCoordinate, yCoordinate, color);
				gameplay.sendBubbleAddedNotifications(bubbles[0][j]);
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
		nextBubble = new OrdinaryBubble(WIDTH / 2, BUBBLES_HEIGHT + (HEIGHT - BUBBLES_HEIGHT) / 2,
				getRandomBubbleColor());
		gameplay.sendBubbleAddedNotifications(nextBubble);
		setBubbleToThrow();
		createNewTask();
	}

	private void initBubbles() {
		for (int i = 0; i < ROWS / 2; i++) {
			double yCoordinate = getCenterY(i);
			for (int j = 0; j < COLUMNS; j++) {
				double xCoordinate = getCenterX(i, j);
				BubbleColor color = getRandomBubbleColor();
				gameplay.getColorsCounter().increment(color);
				bubbles[i][j] = new OrdinaryBubble(xCoordinate, yCoordinate, color);
				gameplay.sendBubbleAddedNotifications(bubbles[i][j]);
			}
		}
	}

	private void createNewTask() {
		gameplay.getTimer().schedule(goDown, 30_000);
	}

	public void setBubbleToThrow() {
		bubbleToThrow = nextBubble;
		bubbleToThrow.setCenterX(WIDTH / 2);
		gameplay.sendBubbleChangedNotifications(bubbleToThrow);
		int randomNumber = random.nextInt(10);
		if (randomNumber == 0)
			nextBubble = new BombBubble(WIDTH / 3, BUBBLES_HEIGHT + (HEIGHT - BUBBLES_HEIGHT) / 2);
		else {
			BubbleColor color = getRandomBubbleColorIfColorExists();
			nextBubble = new OrdinaryBubble(WIDTH / 3, BUBBLES_HEIGHT + (HEIGHT - BUBBLES_HEIGHT) / 2, color);
		}
		gameplay.sendBubbleAddedNotifications(nextBubble);
	}

	public void ifNeedChangeNextBubble() {
		boolean changed = false;
		if (nextBubble instanceof OrdinaryBubble) {
			OrdinaryBubble bubble = (OrdinaryBubble) nextBubble;
			while (gameplay.getColorsCounter().getQuantity(bubble.getColor()) == 0) {
				changed = true;
				bubble.setColor(getRandomBubbleColor());
			}
			if (changed)
				gameplay.sendBubbleChangedNotifications(nextBubble);
		}
		setBubbleToThrow();
	}

	private BubbleColor getRandomBubbleColorIfColorExists() {
		BubbleColor color;
		do
			color = getRandomBubbleColor();
		while (gameplay.getColorsCounter().getQuantity(color) == 0);
		return color;
	}

	public BubbleColor getRandomBubbleColor() {
		BubbleColor[] colors = BubbleColor.values();
		return colors[random.nextInt(colors.length)];
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

	public Bubble[][] getBubbles() {
		return bubbles;
	}

	public Bubble getBubbleToThrow() {
		return bubbleToThrow;
	}

	public Bubble getNextBubble() {
		return nextBubble;
	}

	public Object getLocker() {
		return locker;
	}

	public int getRowOffset() {
		return rowOffset;
	}

	public boolean isWaiting() {
		return isWaiting[0];
	}

}