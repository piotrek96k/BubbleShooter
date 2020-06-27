package com.project.model.gameplay;

import java.util.LinkedList;
import java.util.List;

import com.project.model.bubble.Bubble;
import com.project.model.bubble.BubbleColor;
import com.project.model.listener.BubbleListener;
import com.project.model.listener.MoveListener;
import com.project.timer.PausableTimer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;

public class Gameplay {

	private boolean isMoving = false;

	private BubblesTab bubblesTab;

	private Shooter shooter;

	private Remover remover;

	private ColorsCounter colorsCounter;

	private PausableTimer timer = new PausableTimer(true);

	private BooleanProperty finished = new SimpleBooleanProperty();

	private List<BubbleListener> bubbleAddedListeners = new LinkedList<>();

	private List<BubbleListener> bubbleRemovedListeners = new LinkedList<>();

	private List<BubbleListener> bubbleChangedListeners = new LinkedList<>();

	private List<MoveListener> moveListeners = new LinkedList<>();

	public Gameplay(int rows, int columns, double diameter) {
		bubblesTab = new BubblesTab(this, rows, columns, diameter);
		colorsCounter = new ColorsCounter(BubbleColor.values().length);
		remover = new Remover(this);
		shooter = new Shooter(this);
		Bubble.setDiameter(diameter);
		Bubble.setOffset(5.0);
	}

	public void init() {
		bubblesTab.init();
	}

	public void setStopMoving() {
		if (checkIfGameEnded())
			finishGame();
		else
			bubblesTab.changeNextBubbleIfNeeded();
		isMoving = false;
		synchronized (bubblesTab.getLocker()) {
			if (bubblesTab.isWaiting())
				bubblesTab.getLocker().notifyAll();
		}
	}

	private boolean checkIfGameEnded() {
		for (Bubble bubble : bubblesTab.getBubbles()[bubblesTab.ROWS - 1])
			if (bubble != null)
				return true;
		for (Bubble bubble : bubblesTab.getBubbles()[0])
			if (bubble != null)
				return false;
		return true;
	}

	public void setStartMoving() {
		isMoving = true;
	}

	public void pauseOrResume() {
		if (timer.isPaused())
			timer.resume();
		else
			timer.pause();
	}

	public void finishGame() {
		timer.cancel();
		System.out.println("Koniec Gry");
		finished.set(true);
	}

	public void throwBubble(double x, double y) {
		synchronized (bubblesTab.getLocker()) {
			shooter.throwBubble(x, y);
		}
	}

	public List<Point2D> getLinePoints(double x, double y) {
		return shooter.getLinePoints(x, y);
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

	public BubblesTab getBubblesTab() {
		return bubblesTab;
	}

	public Shooter getShooter() {
		return shooter;
	}

	public Remover getRemover() {
		return remover;
	}

	public ColorsCounter getColorsCounter() {
		return colorsCounter;
	}

	public PausableTimer getTimer() {
		return timer;
	}

	public ReadOnlyBooleanProperty isFinishedProperty() {
		return (ReadOnlyBooleanProperty) finished;
	}

	public boolean isMoving() {
		return isMoving;
	}

}