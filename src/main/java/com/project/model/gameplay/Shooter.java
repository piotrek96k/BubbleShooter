package com.project.model.gameplay;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.project.function.TriFunction;
import com.project.model.bubble.Bubble;
import com.project.model.bubble.BubbleColor;
import com.project.model.bubble.ColoredBubble;
import com.project.model.bubble.DestroyingBubble;
import com.project.model.bubble.TransparentBubble;

import javafx.geometry.Point2D;

public class Shooter {

	private Gameplay gameplay;

	private Mover mover;

	private List<TriFunction<Point2D, Point2D, Double, Point2D>> functions;

	{
		functions = new ArrayList<TriFunction<Point2D, Point2D, Double, Point2D>>(3);
		functions.add(this::firstApply);
		functions.add(this::secondApply);
		functions.add(this::thirdApply);
	}

	public Shooter(Gameplay gameplay) {
		this.gameplay = gameplay;
	}

	private class Mover implements Runnable {

		private Point2D point;

		private Point2D coefficients;

		private int counter = 1;

		Set<Coordinate> deleted = new LinkedHashSet<Coordinate>();

		private TriFunction<Point2D, Point2D, Double, Point2D> function;

		public Mover(Point2D point, Point2D coefficients, TriFunction<Point2D, Point2D, Double, Point2D> function) {
			this.point = point;
			this.coefficients = coefficients;
			this.function = function;
		}

		@Override
		public void run() {
			point = function.apply(point, coefficients, Bubble.getDiameter() / 6);
			if (counter == 3) {
				if (gameplay.getBubblesTab().getBubbleToThrow() instanceof DestroyingBubble)
					moveDestroyingBubble();
				else
					moveBubble();
				counter = 1;
			}
			counter++;
			if (mover != null) {
				gameplay.getBubblesTab().getBubbleToThrow().setCenterX(point.getX());
				gameplay.getBubblesTab().getBubbleToThrow().setCenterY(point.getY());
				gameplay.sendBubbleChangedNotifications(gameplay.getBubblesTab().getBubbleToThrow());
			}
		}

		private void moveBubble() {
			if (checkIfNeedToChangePath(point)) {
				changePath(point, coefficients, this::restartTimer);
				return;
			}
			if (willThereBeACollision(point))
				stop();
		}

		private void stop() {
			gameplay.getTimer().cancelTask(mover);
			mover = null;
		}

		private void moveDestroyingBubble() {
			if (checkIfNeedToChangePath(point) || point.getY() < Bubble.getDiameter() / 2) {
				new Thread(() -> gameplay.getRemover().removeHangers(deleted), "Removing Thread").start();
				stop();
				gameplay.sendBubbleRemovedNotifications(gameplay.getBubblesTab().getBubbleToThrow());
			}
			for (Coordinate coordinate : getCoordinates(point, Bubble.getDiameter() / 4)) {
				Bubble bubble = gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()];
				if (bubble != null) {
					if (bubble instanceof ColoredBubble) {
						ColoredBubble coloredBubble = (ColoredBubble) bubble;
						for (BubbleColor color : coloredBubble.getColors())
							gameplay.getColorsCounter().decrement(color);
					}
					deleted.add(coordinate);
					gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] = null;
					gameplay.sendBubbleRemovedNotifications(bubble);
				}
			}
		}

		private Void restartTimer(Point2D point, Point2D coefficients,
				TriFunction<Point2D, Point2D, Double, Point2D> function) {
			gameplay.getTimer().cancelTask(mover);
			createNewTimerTask(point, coefficients, function);
			return null;
		}

		private boolean willThereBeACollision(Point2D point) {
			List<Coordinate> coordinates = getCoordinates(point, Bubble.getDiameter() / 2);
			for (Coordinate coordinate : coordinates) {
				Bubble bubble = gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()];
				if (bubble != null || point.getY() < Bubble.getDiameter() / 2) {
					double distance;
					if (bubble != null)
						distance = calculateDistance(point, bubble);
					else
						distance = calculateDistance(point, coordinate);
					if (distance < Bubble.getDiameter() - 2
							&& gameplay.getBubblesTab().getBubbleToThrow() instanceof TransparentBubble)
						return removeWithTransparentBubble(coordinate, point);
					else if (distance < Bubble.getDiameter() - Bubble.getOffset()) {
						addBubbleOnFreeLocation(coordinates, point);
						return true;
					}
				}
			}
			return false;
		}

		private boolean removeWithTransparentBubble(Coordinate coordinate, Point2D point) {
			if (!checkIfSameColorAsTransparent(coordinate)) {
				if (point.getY() < Bubble.getDiameter() / 2) {
					gameplay.sendBubbleRemovedNotifications(gameplay.getBubblesTab().getBubbleToThrow());
					gameplay.setStopMoving();
					return true;
				}
				return false;
			}
			boolean remove = gameplay.getRemover().shouldBeAReaction(coordinate, true);
			if (checkIfSameColorAsTransparent(coordinate) && remove) {
				new Thread(() -> gameplay.getRemover().remove(coordinate, true), "Removing Thread").start();
				gameplay.sendBubbleRemovedNotifications(gameplay.getBubblesTab().getBubbleToThrow());
			} else if (point.getY() < Bubble.getDiameter() / 2) {
				gameplay.sendBubbleRemovedNotifications(gameplay.getBubblesTab().getBubbleToThrow());
				gameplay.setStopMoving();
				return true;
			}
			return remove;
		}

		private boolean checkIfSameColorAsTransparent(Coordinate coordinate) {
			Bubble bubble = gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()];
			if (!(bubble instanceof ColoredBubble))
				return false;
			ColoredBubble coloredBubble = (ColoredBubble) bubble;
			TransparentBubble transparentBubble = (TransparentBubble) gameplay.getBubblesTab().getBubbleToThrow();
			for (BubbleColor color : coloredBubble.getColors())
				if (transparentBubble.getColor().equals(color))
					return true;
			return false;
		}

		private void addBubbleOnFreeLocation(List<Coordinate> coordinates, Point2D point) {
			Coordinate result = null;
			double distance = Double.MAX_VALUE;
			for (Coordinate coordinate : coordinates) {
				if (gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] == null) {
					double newDistance = calculateDistance(point, coordinate);
					if (newDistance < distance) {
						distance = newDistance;
						result = coordinate;
					}
				}
			}
			addBubble(result);
		}

		private void addBubble(Coordinate coordinate) {
			Bubble bubble = gameplay.getBubblesTab().getBubbleToThrow();
			gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] = bubble;
			bubble.setCenterX(gameplay.getBubblesTab().getCenterX(coordinate.getRow(), coordinate.getColumn()));
			bubble.setCenterY(gameplay.getBubblesTab().getCenterY(coordinate.getRow()));
			if (bubble instanceof ColoredBubble) {
				ColoredBubble coloredBubble = (ColoredBubble) bubble;
				for (int i = 0; i < coloredBubble.getColorsQuantity(); i++)
					gameplay.getColorsCounter().increment(coloredBubble.getColors().get(i));
			}
			gameplay.sendBubbleChangedNotifications(gameplay.getBubblesTab().getBubbleToThrow());
			new Thread(() -> gameplay.getRemover().remove(coordinate, false), "Removing Thread").start();
		}

		private List<Integer> getRows(double y, double withdraw) {
			int first = (int) ((y - withdraw) / gameplay.getBubblesTab().ROW_HEIGHT);
			int second = (int) ((y + withdraw) / gameplay.getBubblesTab().ROW_HEIGHT);
			List<Integer> result = new ArrayList<Integer>();
			if (first >= 0 && first < gameplay.getBubblesTab().ROWS) {
				result.add(first);
				if (first + 2 == second && first + 1 < gameplay.getBubblesTab().ROWS)
					result.add(first + 1);
				if (second < gameplay.getBubblesTab().ROWS)
					result.add(second);
			}
			return result;
		}

		private List<Coordinate> getCoordinates(Point2D point, double withdraw) {
			double x = point.getX();
			double y = point.getY();
			List<Coordinate> coordinates = new ArrayList<Coordinate>();
			for (int row : getRows(y, withdraw)) {
				double rowOffset = 0;
				int rowToCheck = row + gameplay.getBubblesTab().getRowOffset();
				if (rowToCheck % 2 == 1)
					rowOffset = Bubble.getDiameter() / 2;
				int column = (int) ((x - rowOffset - withdraw) / Bubble.getDiameter());
				if (column >= 0)
					coordinates.add(new Coordinate(row, column));
				column = (int) ((x - rowOffset + withdraw) / Bubble.getDiameter());
				if (column < gameplay.getBubblesTab().COLUMNS)
					coordinates.add(new Coordinate(row, column));
			}
			return coordinates;
		}

	}

	public List<Point2D> getLinePoints(double x, double y) {
		List<Point2D> result = new ArrayList<Point2D>();
		if (y < gameplay.getBubblesTab().BUBBLES_HEIGHT + Bubble.getDiameter() && !gameplay.getTimer().isPaused()) {
			Point2D point = getStartPoint();
			Point2D coefficients = getCoefficients(x, y, point);
			TriFunction<Point2D, Point2D, Double, Point2D> function = chooseFunctionToApplay(point, coefficients);
			result.add(point);
			result.addAll(getLinePoints(point, coefficients, function));
		}
		return result;
	}

	private List<Point2D> getLinePoints(Point2D point, Point2D coefficients,
			TriFunction<Point2D, Point2D, Double, Point2D> function) {
		List<Point2D> result = new ArrayList<Point2D>();
		boolean collide;
		do {
			point = function.apply(point, coefficients, 1.0);
			if (checkIfNeedToChangePath(point)) {
				result.add(point);
				result.addAll(changePath(point, coefficients, this::getLinePoints));
				return result;
			}
			collide = checkIfLineCollideWithBall(point);
		} while (!collide && point.getY() > 0);
		result.add(point);
		return result;
	}

	private boolean checkIfLineCollideWithBall(Point2D point) {
		Coordinate coordinate = getCoordinate(point);
		if (coordinate == null)
			return false;
		Bubble bubble = gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()];
		if (bubble == null)
			return false;
		double distance = calculateDistance(point, coordinate);
		if (distance > Bubble.getDiameter() / 2)
			return false;
		return true;
	}

	private int getRow(double y) {
		return (int) (y / gameplay.getBubblesTab().ROW_HEIGHT);
	}

	private Coordinate getCoordinate(Point2D point) {
		double x = point.getX();
		double y = point.getY();
		int row = getRow(y);
		if (row < 0 || row >= gameplay.getBubblesTab().ROWS)
			return null;
		double rowOffset = 0;
		int rowToCheck = row + gameplay.getBubblesTab().getRowOffset();
		if (rowToCheck % 2 == 1)
			rowOffset = Bubble.getDiameter() / 2;
		int column = (int) ((x - rowOffset) / Bubble.getDiameter());
		if (column < 0 || column >= gameplay.getBubblesTab().COLUMNS)
			return null;
		return new Coordinate(row, column);
	}

	public void throwBubble(double x, double y) {
		if (!gameplay.isFinishedProperty().get() && !gameplay.getTimer().isPaused() && !gameplay.isMoving()
				&& y < gameplay.getBubblesTab().BUBBLES_HEIGHT + Bubble.getDiameter()) {
			gameplay.setStartMoving();
			Point2D point = getStartPoint();
			Point2D coefficients = getCoefficients(x, y, point);
			TriFunction<Point2D, Point2D, Double, Point2D> function = chooseFunctionToApplay(point, coefficients);
			createNewTimerTask(point, coefficients, function);
		}
	}

	private Point2D getStartPoint() {
		BubblesTab tab = gameplay.getBubblesTab();
		double x = tab.WIDTH / 2;
		double y = tab.BUBBLES_HEIGHT + (tab.HEIGHT - tab.BUBBLES_HEIGHT) / 2;
		return new Point2D(x, y);
	}

	private Point2D getCoefficients(double x, double y, Point2D startPoint) {
		double a = (y - startPoint.getY()) / (x - startPoint.getX());
		double b = startPoint.getY() - a * startPoint.getX();
		return new Point2D(a, b);
	}

	private TriFunction<Point2D, Point2D, Double, Point2D> chooseFunctionToApplay(Point2D point, Point2D coefficients) {
		TriFunction<Point2D, Point2D, Double, Point2D> result;
		if (Math.abs(coefficients.getX()) >= 1)
			result = functions.get(0);
		else if (coefficients.getX() >= 0)
			result = functions.get(1);
		else
			result = functions.get(2);
		return result;
	}

	private void createNewTimerTask(Point2D point, Point2D coefficients,
			TriFunction<Point2D, Point2D, Double, Point2D> function) {
		mover = new Mover(point, coefficients, function);
		gameplay.getTimer().schedule(mover, 2);
	}

	private Point2D firstApply(Point2D point, Point2D coefficients, double value) {
		double y = point.getY() - value;
		double x = (y - coefficients.getY()) / coefficients.getX();
		return new Point2D(x, y);
	}

	private Point2D secondApply(Point2D point, Point2D coefficients, double value) {
		double x = point.getX() - value;
		double y = coefficients.getX() * x + coefficients.getY();
		return new Point2D(x, y);
	}

	private Point2D thirdApply(Point2D point, Point2D coefficients, double value) {
		double x = point.getX() + value;
		double y = coefficients.getX() * x + coefficients.getY();
		return new Point2D(x, y);
	}

	private double calculateDistance(Point2D point, Coordinate coordinate) {
		double dx = gameplay.getBubblesTab().getCenterX(coordinate.getRow(), coordinate.getColumn()) - point.getX();
		double dy = gameplay.getBubblesTab().getCenterY(coordinate.getRow()) - point.getY();
		double distance = Math.pow(Math.pow(dx, 2) + Math.pow(dy, 2), 0.5);
		return distance;
	}

	private double calculateDistance(Point2D point, Bubble ball) {
		double dx = point.getX() - ball.getCenterX();
		double dy = point.getY() - ball.getCenterY();
		double distance = Math.pow(Math.pow(dx, 2) + Math.pow(dy, 2), 0.5);
		return distance;
	}

	private boolean checkIfNeedToChangePath(Point2D point) {
		if (point.getX() <= Bubble.getDiameter() / 2)
			return true;
		if (point.getX() >= gameplay.getBubblesTab().WIDTH - Bubble.getDiameter() / 2)
			return true;
		return false;
	}

	private <T> T changePath(Point2D point, Point2D coefficients,
			TriFunction<Point2D, Point2D, TriFunction<Point2D, Point2D, Double, Point2D>, T> triFunction) {
		double a = coefficients.getX();
		double b = coefficients.getY();
		double x;
		if (point.getX() <= Bubble.getDiameter() / 2)
			x = Bubble.getDiameter() / 2;
		else
			x = gameplay.getBubblesTab().WIDTH - Bubble.getDiameter() / 2;
		double y = a * x + b;
		Point2D newPoint = new Point2D(x, y);
		Point2D newCoefficients = new Point2D(-a, y + a * x);
		TriFunction<Point2D, Point2D, Double, Point2D> newFunction = chooseFunctionToApplay(newPoint, newCoefficients);
		return triFunction.apply(newPoint, newCoefficients, newFunction);
	}

}