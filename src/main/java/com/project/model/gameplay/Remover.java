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
import com.project.model.bubble.ColoredBubble;
import com.project.sound.GameplaySoundEffect;
import com.project.sound.SoundPlayer;

public class Remover {

	private Gameplay gameplay;

	private Coordinate coordinate;

	private Object locker;

	private Set<Coordinate> neighbor;

	private Set<Coordinate> toDelete;

	private Set<Coordinate> bombs;

	private List<Consumer<Coordinate>> consumers;

	private int counter;

	private boolean ended;

	private boolean bomb;

	{
		locker = new Object();
		neighbor = new HashSet<Coordinate>();
		toDelete = new LinkedHashSet<Coordinate>();
		bombs = new LinkedHashSet<Coordinate>();
		consumers = new ArrayList<Consumer<Coordinate>>(5);
		consumers.add(this::repeatOnSameColorBubbles);
		consumers.add(this::addNeighbor);
		consumers.add(this::checkIfHanging);
		consumers.add(this::addNeighborAndFindBombs);
		consumers.add(this::checkIfBomb);
	}

	public Remover(Gameplay gameplay) {
		this.gameplay = gameplay;
	}

	public void remove(Coordinate coordinate, boolean isTransparent) {
		if (gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] == null)
			return;
		this.coordinate = coordinate;
		counter = 1;
		neighbor.add(coordinate);
		toDelete.add(coordinate);
		findBubblesToRemove(isTransparent);
		this.coordinate = null;
		gameplay.setStopMoving();
	}

	public boolean shouldBeAReaction(Coordinate coordinate, boolean isTransparent) {
		if (!(gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate
				.getColumn()] instanceof ColoredBubble))
			return false;
		this.coordinate = coordinate;
		counter = 1;
		neighbor.add(coordinate);
		toDelete.add(coordinate);
		applyOnSurroundingBubbles(consumers.get(0));
		this.coordinate = null;
		neighbor.clear();
		toDelete.clear();
		bombs.clear();
		if (counter >= getNumberToCheck(isTransparent))
			return true;
		return false;
	}

	public void removeHangers(Set<Coordinate> deleted) {
		toDelete = deleted;
		synchronized (locker) {
			findNeighbors();
			removeIfHanging();
		}
		neighbor.clear();
		toDelete.clear();
		gameplay.setStopMoving();
	}

	private void findBubblesToRemove(boolean isTransparent) {
		if (gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] instanceof BombBubble) {
			findBombedBubbles();
		} else {
			applyOnSurroundingBubbles(consumers.get(0));
			neighbor.clear();
			if (counter >= getNumberToCheck(isTransparent))
				synchronized (locker) {
					removeBubbles();
					findNeighbors();
					removeIfHanging();
					neighbor.clear();
					runFindingBombedBubbles();
				}
			else {
				applyOnSurroundingBubbles(consumers.get(4));
				if (bomb) {
					runFindingBombedBubbles();
					bomb = false;
				}
			}
		}
		bombs.clear();
		toDelete.clear();
	}

	private int getNumberToCheck(boolean isTransparent) {
		if (isTransparent)
			return 2;
		return 3;

	}

	private void runFindingBombedBubbles() {
		List<Coordinate> bombs = new ArrayList<Coordinate>(this.bombs);
		this.bombs.clear();
		for (Coordinate coordinate : bombs) {
			toDelete.clear();
			this.coordinate = coordinate;
			findBombedBubbles();
		}
	}

	private void findBombedBubbles() {
//		Set<Coordinate> toDelete = new LinkedHashSet<Coordinate>();
		bombs.add(coordinate);
		toDelete = repeatFindingBombedBubbles(this.bombs);
//		this.toDelete = toDelete;
		synchronized (locker) {
			removeBombedBubbles();
			findNeighbors();
			removeIfHanging();
			neighbor.clear();
		}
	}

	private Set<Coordinate> repeatFindingBombedBubbles(Set<Coordinate> bombs) {
		Set<Coordinate> toDelete = new LinkedHashSet<Coordinate>();
		Set<Coordinate> oldBombs = new LinkedHashSet<Coordinate>(this.bombs);
		for (Coordinate coordinate : bombs) {
			if (gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] != null) {
//				for(int i =0; i< 2; i++) {
				this.toDelete.add(coordinate);
				toDelete.add(coordinate);
				findNeighborAndBombs();
				neighbor.removeAll(toDelete);
//				this.toDelete.clear();
//				this.toDelete.addAll(neighbor);
				toDelete.addAll(neighbor);
//				}
				neighbor.clear();
				this.toDelete.clear();
			}
		}
		Set<Coordinate> newBombs = new LinkedHashSet<Coordinate>(this.bombs);
		newBombs.removeAll(oldBombs);
		if (!newBombs.isEmpty())
			toDelete.addAll(repeatFindingBombedBubbles(newBombs));
		return toDelete;

	}

	private void removeBombedBubbles() {
		boolean[] removed = { false };
		Runnable[] runnable = { null };
		runnable[0] = () -> {
			synchronized (locker) {
				SoundPlayer.getInstance().playGameplaySoundEffect(GameplaySoundEffect.EXPLOSION);
				for (Coordinate coordinate : toDelete) {
					Bubble bubble = gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()];
					gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] = null;
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
				Bubble bubble = gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()];
				gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] = null;
				removeBubble(bubble);
				SoundPlayer.getInstance().playGameplaySoundEffect(GameplaySoundEffect.POP);
				if (counter[0] == toDelete.size()) {
					gameplay.getTimer().cancelTask(runnable[0]);
					locker.notifyAll();
				}
			}
		};
		return runnable;
	}

	private void removeBubble(Bubble bubble) {
		if (bubble instanceof ColoredBubble) {
			ColoredBubble coloredBubble = (ColoredBubble) bubble;
			for (int i = 0; i < coloredBubble.getColorsQuantity(); i++)
				gameplay.getColorsCounter().decrement(coloredBubble.getColors().get(i));
		}
		gameplay.sendBubbleRemovedNotifications(bubble);
	}

	private void findNeighbors() {
		toDelete.forEach(coordinate -> {
			this.coordinate = coordinate;
			applyOnSurroundingBubbles(consumers.get(1));
		});
	}

	private void findNeighborAndBombs() {
		toDelete.forEach(coordinate -> {
			this.coordinate = coordinate;
			applyOnSurroundingBubbles(consumers.get(3));
		});
	}

	private void removeIfHanging() {
		toDelete.clear();
		Set<Coordinate> toDrop = new LinkedHashSet<Coordinate>();
		neighbor.forEach(coordinate -> {
			Bubble bubble = gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()];
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
		if (!toDrop.isEmpty()) {
			SoundPlayer.getInstance().playGameplaySoundEffect(GameplaySoundEffect.FALLING);
			try {
				initDroppers(toDrop);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void initDroppers(Set<Coordinate> toDrop) throws InterruptedException {
		List<Bubble> list = new LinkedList<Bubble>();
		toDrop.forEach(coordinate -> {
			list.add(gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()]);
			gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] = null;
		});
		Collections.reverse(list);
		Map<Bubble, Dropper> droppersMap = new HashMap<Bubble, Dropper>();
		int delay = 10;
		for (int i = 0; i < list.size(); i++) {
			Bubble bubble = list.get(i);
			double startHeight = Dropper.convertHeight(bubble.getCenterY(),
					BubblesTab.BUBBLES_HEIGHT + Bubble.DIAMETER);
			Dropper dropper = new Dropper(startHeight, delay + delay * i);
			droppersMap.put(bubble, dropper);
		}
		dropBubbles(list, droppersMap);
	}

	private void dropBubbles(List<Bubble> toDrop, Map<Bubble, Dropper> droppersMap) throws InterruptedException {
		List<Bubble> toDelete = new LinkedList<Bubble>();
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
			double paneHeight = Dropper.convertHeight(height, BubblesTab.BUBBLES_HEIGHT + Bubble.DIAMETER);
			bubble.setCenterY(paneHeight);
			gameplay.sendBubbleChangedNotifications(bubble);
			if (height <= Bubble.DIAMETER / 2) {
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
		if (column < gameplay.getBubblesTab().getBubbles()[0].length - 1) {
			consumer.accept(new Coordinate(row, column + 1));
			if (ended)
				return;
		}
		if (row < gameplay.getBubblesTab().getBubbles().length - 1)
			applyOnAdjacentBubbles(consumer, 1);
	}

	private void applyOnAdjacentBubbles(Consumer<Coordinate> consumer, int offset) {
		int row = coordinate.getRow();
		int column = coordinate.getColumn();
		consumer.accept(new Coordinate(row + offset, column));
		if (ended)
			return;
		int rowToCheck = row + gameplay.getBubblesTab().getRowOffset();
		if (rowToCheck % 2 == 0 && column > 0)
			consumer.accept(new Coordinate(row + offset, column - 1));
		else if (rowToCheck % 2 == 1 && column < gameplay.getBubblesTab().getBubbles()[0].length - 1)
			consumer.accept(new Coordinate(row + offset, column + 1));
	}

	private void repeatOnSameColorBubbles(Coordinate newCoordinate) {
		Coordinate oldCoordinate = coordinate;
		Bubble firstBubble = gameplay.getBubblesTab().getBubbles()[oldCoordinate.getRow()][oldCoordinate.getColumn()];
		Bubble secondBubble = gameplay.getBubblesTab().getBubbles()[newCoordinate.getRow()][newCoordinate.getColumn()];
		if (secondBubble != null) {
			if (secondBubble instanceof BombBubble) {
				bombs.add(newCoordinate);
				neighbor.add(newCoordinate);
			} else if (checkIfSameColor(firstBubble, secondBubble)) {
				toDelete.add(newCoordinate);
				int size = neighbor.size();
				neighbor.add(newCoordinate);
				if (size != neighbor.size()) {
					counter++;
					this.coordinate = newCoordinate;
					applyOnSurroundingBubbles(consumers.get(0));
					this.coordinate = oldCoordinate;
				}
			}
		}
	}

	private boolean checkIfSameColor(Bubble firstBubble, Bubble secondBubble) {
		ColoredBubble firstColoredBubble = (ColoredBubble) firstBubble;
		ColoredBubble secondColoredBubble = (ColoredBubble) secondBubble;
		for (int i = 0; i < firstColoredBubble.getColorsQuantity(); i++)
			for (int j = 0; j < secondColoredBubble.getColorsQuantity(); j++)
				if (firstColoredBubble.getColors().get(i).equals(secondColoredBubble.getColors().get(j)))
					return true;
		return false;
	}

	private void addNeighbor(Coordinate coordinate) {
		if (gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()] != null)
			neighbor.add(coordinate);
	}

	private void addNeighborAndFindBombs(Coordinate coordinate) {
		Bubble bubble = gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()];
		if (bubble != null) {
			neighbor.add(coordinate);
			if (bubble instanceof BombBubble)
				bombs.add(coordinate);
		}
	}

	private void checkIfBomb(Coordinate coordinate) {
		Bubble bubble = gameplay.getBubblesTab().getBubbles()[coordinate.getRow()][coordinate.getColumn()];
		if (bubble instanceof BombBubble)
			bomb = true;
	}

	private void checkIfHanging(Coordinate newCoordinate) {
		if (gameplay.getBubblesTab().getBubbles()[newCoordinate.getRow()][newCoordinate.getColumn()] != null) {
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