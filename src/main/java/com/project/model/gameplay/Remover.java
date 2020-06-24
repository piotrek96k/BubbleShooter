package com.project.model.gameplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.project.model.bubble.BombBubble;
import com.project.model.bubble.Bubble;
import com.project.model.bubble.OrdinaryBubble;

public class Remover {

	private Gameplay gameplay;

	private ColorsCounter colorsCounter;

	private Coordinate coordinate;

	private Object locker = new Object();

	private Set<Coordinate> neighbor = new HashSet<>();

	private Set<Coordinate> toDelete = new LinkedHashSet<>();

	private List<Consumer<Coordinate>> consumers = new ArrayList<>(3);

	private int counter;

	private boolean ended;

	{
		consumers.add(this::checkIfSameColor);
		consumers.add(this::addNeighbor);
		consumers.add(this::checkIfHanging);
	}

	public Remover(Gameplay gameplay, ColorsCounter colorsCounter) {
		this.gameplay = gameplay;
		this.colorsCounter = colorsCounter;
	}

	public void remove(Coordinate coordinate) {
		if (gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()] == null)
			return;
		this.coordinate = coordinate;
		counter = 1;
		neighbor.add(coordinate);
		toDelete.add(coordinate);
		findBubblesToRemove();
		this.coordinate = null;
		gameplay.setStopMoving();
	}

	private void findBubblesToRemove() {
		if (gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()] instanceof BombBubble) {
			findBombedBubbles();
		} else {
			applyOnSurroundingBubbles(consumers.get(0));
			neighbor.clear();
			if (counter >= 3)
				synchronized (locker) {
					removeBubbles();
					findNeighbors();
					removeIfHanging();
					neighbor.clear();
				}
		}
		toDelete.clear();
	}

	private void findBombedBubbles() {
		Set<Coordinate> toDelete = new LinkedHashSet<Coordinate>();
		this.toDelete.add(coordinate);
		for (int i = 0; i < 2; i++) {
			findNeighbors();
			neighbor.removeAll(toDelete);
			this.toDelete.clear();
			this.toDelete.addAll(neighbor);
			toDelete.addAll(neighbor);
			neighbor.clear();
		}
		this.toDelete = toDelete;
		synchronized (locker) {
			removeBombedBubbles();
			findNeighbors();
			removeIfHanging();
			neighbor.clear();
		}
	}

	private void removeBombedBubbles() {
		boolean[] removed = {false};
		Runnable[] runnable = { null };
		runnable[0] = () -> {
			synchronized (locker) {
				for (Coordinate coordinate : toDelete) {
					Bubble bubble = gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()];
					gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()] = null;
					removeBubble(bubble);
				}
				removed[0] = true;
				gameplay.getTimer().cancelTask(runnable[0]);
				locker.notifyAll();
			}
		};
		gameplay.getTimer().schedule(runnable[0], 300);
		while (!removed[0])
			try {
				locker.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	private void removeBubbles() {
		int[] counter = { 0 };
		List<Coordinate> toDelete = new ArrayList<Coordinate>(this.toDelete);
		Runnable[] runnable = getRemovingRunnable(toDelete, counter);
		gameplay.getTimer().schedule(runnable[0], 40);
		while (counter[0] != toDelete.size())
			try {
				locker.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	private Runnable[] getRemovingRunnable(List<Coordinate> toDelete, int[] counter) {
		Runnable[] runnable = { null };
		runnable[0] = () -> {
			synchronized (locker) {
				Coordinate coordinate = toDelete.get(counter[0]++);
				Bubble bubble = gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()];
				gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()] = null;
				removeBubble(bubble);
				if (counter[0] == toDelete.size()) {
					gameplay.getTimer().cancelTask(runnable[0]);
					locker.notifyAll();
				}
			}
		};
		return runnable;
	}

	private void removeBubble(Bubble bubble) {
		if (bubble instanceof OrdinaryBubble)
			colorsCounter.decrement(((OrdinaryBubble) bubble).getColor());
		gameplay.sendBubbleRemovedNotifications(bubble);
	}

	private void findNeighbors() {
		toDelete.forEach(coordinate -> {
			this.coordinate = coordinate;
			applyOnSurroundingBubbles(consumers.get(1));
		});
	}

	private void removeIfHanging() {
		toDelete.clear();
		Set<Coordinate> toDrop = new LinkedHashSet<>();
		neighbor.forEach(coordinate -> {
			Bubble bubble = gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()];
			if (bubble != null && coordinate.getRow() != 0) {
				toDelete.clear();
				this.coordinate = coordinate;
				toDelete.add(coordinate);
				applyOnSurroundingBubbles(consumers.get(2));
				if (!ended)
					toDrop.addAll(toDelete);
				ended = false;
			}
		});
		if (!toDrop.isEmpty())
			try {
				initDroppers(toDrop);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	private void initDroppers(Set<Coordinate> toDrop) throws InterruptedException {
		List<Bubble> list = new LinkedList<>();
		toDrop.forEach(coordinate -> {
			list.add(gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()]);
			gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()] = null;
		});
		Collections.reverse(list);
		Map<Bubble, Dropper> droppersMap = new HashMap<>();
		int delay = 10;
		for (int i = 0; i < list.size(); i++) {
			Bubble bubble = list.get(i);
			double startHeight = Dropper.convertHeight(bubble.getCenterY(),
					gameplay.BUBBLES_HEIGHT + Bubble.getDiameter());
			Dropper dropper = new Dropper(startHeight, delay + delay * i);
			droppersMap.put(bubble, dropper);
		}
		dropBubbles(list, droppersMap);
	}

	private void dropBubbles(List<Bubble> toDrop, Map<Bubble, Dropper> droppersMap) throws InterruptedException {
		List<Bubble> toDelete = new LinkedList<>();
		Runnable[] runnable = { null };
		runnable[0] = () -> {
			synchronized (locker) {
				moveBubbles(toDrop, droppersMap, toDelete);
				toDrop.removeAll(toDelete);
				if (toDrop.isEmpty()) {
					gameplay.getTimer().cancelTask(runnable[0]);
					locker.notifyAll();
				}
			}
		};
		gameplay.getTimer().schedule(runnable[0], 10);
		while (!toDrop.isEmpty())
			locker.wait();
	}

	private void moveBubbles(List<Bubble> toDrop, Map<Bubble, Dropper> droppersMap, List<Bubble> toDelete) {
		toDelete.clear();
		for (Bubble bubble : toDrop) {
			Dropper dropper = droppersMap.get(bubble);
			double height = dropper.getHeight(System.currentTimeMillis());
			double paneHeight = Dropper.convertHeight(height, gameplay.BUBBLES_HEIGHT + Bubble.getDiameter());
			bubble.setCenterY(paneHeight);
			gameplay.sendBubbleChangedNotifications(bubble);
			if (height <= Bubble.getDiameter() / 2) {
				removeBubble(bubble);
				toDelete.add(bubble);
			}
		}
	}

	private void applyOnSurroundingBubbles(Consumer<Coordinate> consumer) {
		int row = coordinate.getRow();
		int column = coordinate.getColumn();
		if (row > 0) {
			applyOnAdjacentBubbles(consumer, -1);
			if (ended)
				return;
		}
		if (column > 0) {
			consumer.accept(new Coordinate(row, column - 1));
			if (ended)
				return;
		}
		if (column < gameplay.getBubbles()[0].length - 1) {
			consumer.accept(new Coordinate(row, column + 1));
			if (ended)
				return;
		}
		if (row < gameplay.getBubbles().length - 1)
			applyOnAdjacentBubbles(consumer, 1);
	}

	private void applyOnAdjacentBubbles(Consumer<Coordinate> consumer, int offset) {
		int row = coordinate.getRow();
		int column = coordinate.getColumn();
		consumer.accept(new Coordinate(row + offset, column));
		if (ended)
			return;
		int rowToCheck = row + gameplay.getRowOffset();
		if (rowToCheck % 2 == 0 && column > 0)
			consumer.accept(new Coordinate(row + offset, column - 1));
		else if (rowToCheck % 2 == 1 && column < gameplay.getBubbles()[0].length - 1)
			consumer.accept(new Coordinate(row + offset, column + 1));
	}

	private void checkIfSameColor(Coordinate newCoordinate) {
		Coordinate oldCoordinate = coordinate;
		Bubble firstBubble = gameplay.getBubbles()[oldCoordinate.getRow()][oldCoordinate.getColumn()];
		Bubble secondBubble = gameplay.getBubbles()[newCoordinate.getRow()][newCoordinate.getColumn()];
		if (secondBubble != null) {
			int size = neighbor.size();
			neighbor.add(newCoordinate);
			if (size != neighbor.size()
					&& ((OrdinaryBubble) firstBubble).getColor().equals(((OrdinaryBubble) secondBubble).getColor())) {
				counter++;
				toDelete.add(newCoordinate);
				this.coordinate = newCoordinate;
				applyOnSurroundingBubbles(consumers.get(0));
				this.coordinate = oldCoordinate;
			}
		}
	}

	private void addNeighbor(Coordinate coordinate) {
		if (gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()] != null)
			neighbor.add(coordinate);
	}

	private void checkIfHanging(Coordinate newCoordinate) {
		if (gameplay.getBubbles()[newCoordinate.getRow()][newCoordinate.getColumn()] != null) {
			if (newCoordinate.getRow() == 0) {
				ended = true;
				return;
			}
			int size = toDelete.size();
			toDelete.add(newCoordinate);
			if (toDelete.size() != size) {
				Coordinate oldCoordinate = coordinate;
				coordinate = newCoordinate;
				applyOnSurroundingBubbles(consumers.get(2));
				coordinate = oldCoordinate;
			}
		}
	}

}