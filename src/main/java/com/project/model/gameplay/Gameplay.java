package com.project.model.gameplay;

import java.util.LinkedList;
import java.util.List;

import com.project.model.bubble.Bubble;
import com.project.model.listener.BubbleListener;
import com.project.model.listener.ChangeListener;
import com.project.model.mode.GameMode;
import com.project.sound.SoundPlayer;
import com.project.timer.PausableTimer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;

public class Gameplay {

	private boolean moving;

	private boolean victorious;

	private GameMode gameMode;

	private BubblesTab bubblesTab;

	private Shooter shooter;

	private Remover remover;

	private ColorsCounter colorsCounter;

	private TimeCounter timeCounter;

	private PointsCounter pointsCounter;

	private PausableTimer timer;

	private BooleanProperty finished;

	private List<BubbleListener> bubbleAddedListeners;

	private List<BubbleListener> bubbleRemovedListeners;

	private List<BubbleListener> bubbleChangedListeners;

	private List<ChangeListener> moveListeners;

	private List<ChangeListener> timeListeners;

	private List<ChangeListener> pointsListeners;

	private List<ChangeListener> comboListeners;

	{
		timer = new PausableTimer(true);
		finished = new SimpleBooleanProperty();
		bubbleAddedListeners = new LinkedList<BubbleListener>();
		bubbleRemovedListeners = new LinkedList<BubbleListener>();
		bubbleChangedListeners = new LinkedList<BubbleListener>();
		moveListeners = new LinkedList<ChangeListener>();
		timeListeners = new LinkedList<ChangeListener>();
		pointsListeners = new LinkedList<ChangeListener>();
		comboListeners = new LinkedList<ChangeListener>();
	}

	public Gameplay(GameMode gameMode) {
		this.gameMode = gameMode;
		colorsCounter = new ColorsCounter();
		timeCounter = new TimeCounter(this);
		remover = new Remover(this);
		shooter = new Shooter(this);
		bubblesTab = new BubblesTab(this);
		pointsCounter = new PointsCounter(this);
		Bubble.setOffset(5.0);
		SoundPlayer.getInstance().switchMenuGameplayMusic();
	}

	public void setStopMoving() {
		if (checkIfGameEnded())
			finishGame();
		else
			bubblesTab.setBubbleToThrow();
		moving = false;
		synchronized (bubblesTab.getLocker()) {
			if (bubblesTab.isWaiting())
				bubblesTab.getLocker().notifyAll();
		}
	}

	private boolean checkIfGameEnded() {
		for (Bubble bubble : bubblesTab.getBubbles()[BubblesTab.ROWS - 1])
			if (bubble != null)
				return true;
		for (Bubble bubble : bubblesTab.getBubbles()[0])
			if (bubble != null)
				return false;
		victorious = true;
		return true;
	}

	public void setStartMoving() {
		moving = true;
	}

	public void pauseOrResume() {
		if (timer.isPaused()) {
			timer.resume();
			SoundPlayer.getInstance().resumeGameplaySoundEffects();
		} else {
			timer.pause();
			SoundPlayer.getInstance().pauseGameplaySoundEffects();
		}
	}

	public void finishGame() {
		timer.cancel();
		SoundPlayer.getInstance().switchMenuGameplayMusic();
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

	public void addMoveListener(ChangeListener listener) {
		moveListeners.add(listener);
	}

	public void removeMoveListener(ChangeListener listener) {
		moveListeners.remove(listener);
	}

	public void sendMoveNotifications() {
		for (ChangeListener listener : moveListeners)
			listener.changed();
	}

	public void addTimeListener(ChangeListener listener) {
		timeListeners.add(listener);
	}

	public void removeTimeListener(ChangeListener listener) {
		timeListeners.remove(listener);
	}

	public void sendTimeNotifications() {
		for (ChangeListener listener : timeListeners)
			listener.changed();
	}

	public void addPointsListener(ChangeListener listener) {
		pointsListeners.add(listener);
	}

	public void removePointsListener(ChangeListener listener) {
		pointsListeners.remove(listener);
	}

	public void sendPointsNotofications() {
		for (ChangeListener listener : pointsListeners)
			listener.changed();
	}

	public void addComboListener(ChangeListener listener) {
		comboListeners.add(listener);
	}

	public void removeComboListener(ChangeListener listener) {
		comboListeners.remove(listener);
	}

	public void sendComboNotifications() {
		for (ChangeListener listener : comboListeners)
			listener.changed();
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

	public TimeCounter getTimeCounter() {
		return timeCounter;
	}

	public PointsCounter getPointsCounter() {
		return pointsCounter;
	}

	public PausableTimer getTimer() {
		return timer;
	}

	public int getCombo() {
		return pointsCounter.getCombo();
	}

	public long getPoints() {
		return pointsCounter.getPoints();
	}

	public ReadOnlyBooleanProperty getFinishedProperty() {
		return (ReadOnlyBooleanProperty) finished;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public long getTime() {
		return timeCounter.getTime();
	}

	public boolean isMoving() {
		return moving;
	}

	public boolean isPaused() {
		return timer.isPaused();
	}

	public boolean isVictorious() {
		return victorious;
	}

}