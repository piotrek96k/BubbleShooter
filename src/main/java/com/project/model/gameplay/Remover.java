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

import com.project.model.bubble.Bubble;

public class Remover {

	private Gameplay gameplay;

	private ColorsCounter colorsCounter;

	private Coordinate coordinate;

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
		applyOnSurroundingBubbles(consumers.get(0));
		neighbor.clear();
		if (counter >= 3) {
			removeBubbles();
			findNeighbors();
			removeIfHanging();
			neighbor.clear();
		}
		toDelete.clear();
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
		toDrop.forEach(coordinate->{
			list.add(gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()]);
			gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()]=null;
		});
		Collections.reverse(list);
		Map<Bubble, Dropper> droppersMap = new HashMap<>();
		for (Bubble bubble : list) {
			droppersMap.put(bubble, new Dropper(
					Dropper.convertHeight(bubble.getCenterY(), gameplay.BUBBLES_HEIGHT + Bubble.getDiameter())));
			Thread.sleep(10);
		}
		dropBubbles(list, droppersMap);
	}

	private void dropBubbles(List<Bubble> toDrop, Map<Bubble, Dropper> droppersMap)
			throws InterruptedException {
		List<Bubble> toDelete = new LinkedList<>();
		while (!toDrop.isEmpty()) {
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
				Thread.sleep(1);
			}
			toDrop.removeAll(toDelete);
		}
	}

	private void removeBubbles() {
		toDelete.forEach(coordinate -> {
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Bubble bubble = gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()];
			gameplay.getBubbles()[coordinate.getRow()][coordinate.getColumn()] = null;
			removeBubble(bubble);
		});
	}

	private void removeBubble(Bubble bubble) {
		colorsCounter.decrement(bubble.getColor());
		gameplay.sendBubbleRemovedNotifications(bubble);
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
			if (size != neighbor.size() && firstBubble.getColor().equals(secondBubble.getColor())) {
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