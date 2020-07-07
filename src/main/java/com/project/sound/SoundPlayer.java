package com.project.sound;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.project.exception.IllegalValueException;
import com.project.preferences.PreferencesKey;
import com.project.preferences.UserPreferences;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class SoundPlayer {

	private static SoundPlayer soundPlayer;

	private static Media menuMusicMedia;

	private Pane pane;

	private GameplayMusicPlayer gameplayMusicPlayer;

	private MediaPlayer menuMusicPlayer;

	private MediaView menuMusicView;

	private Set<MediaPlayer> gameplayEffectsPlayers;

	private double musicVolume;

	private double effectsVolume;

	private boolean musicMuted;

	private boolean effectsMuted;

	static {
		try {
			menuMusicMedia = new Media(SoundPlayer.class.getResource("/sound/menuMusic.mp3").toURI().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	{
		gameplayEffectsPlayers = new HashSet<MediaPlayer>();
		effectsMuted = UserPreferences.PREFERENCES.getBoolean(PreferencesKey.EFFECTS_MUTED.getKey(), false);
		musicMuted = UserPreferences.PREFERENCES.getBoolean(PreferencesKey.MUSIC_MUTED.getKey(), false);
		effectsVolume = UserPreferences.PREFERENCES.getDouble(PreferencesKey.EFFECTS_VOLUME.getKey(), 0.5);
		musicVolume = UserPreferences.PREFERENCES.getDouble(PreferencesKey.MUSIC_VOLUME.getKey(), 0.5);
	}

	private class GameplayMusicPlayer implements Runnable {

		private MediaView mediaView;

		private MediaPlayer mediaPlayer;

		private int number;

		private int counter;

		{
			Random random = new Random();
			number = random.nextInt(GameplayMusic.values().length);
		}

		@Override
		public void run() {
			if (mediaView != null)
				pane.getChildren().remove(mediaView);
			if (mediaPlayer != null)
				mediaPlayer.dispose();
			Media media = GameplayMusic.values()[number].getMedia();
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setMute(musicMuted);
			mediaPlayer.setVolume(musicVolume);
			mediaView = new MediaView(mediaPlayer);
			pane.getChildren().add(mediaView);
			mediaPlayer.setOnEndOfMedia(this);
			mediaPlayer.play();
			if (++counter >= 25) {
				counter = 0;
				number++;
				if (number >= GameplayMusic.values().length)
					number = 0;
			}
		}

		public void stop() {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.dispose();
			}
			if (mediaView != null)
				pane.getChildren().remove(mediaView);
		}

		public void muteOrUnmute() {
			if (mediaPlayer != null)
				mediaPlayer.setMute(musicMuted);
		}

		public void setVolume() {
			if (mediaPlayer != null)
				mediaPlayer.setVolume(musicVolume);
		}

	}

	public static synchronized SoundPlayer getInstance() {
		if (soundPlayer == null)
			soundPlayer = new SoundPlayer();
		return soundPlayer;
	}

	public void init(Pane pane) {
		this.pane = pane;
		playMenuMusic();
	}

	private void playGameplayMusic() {
		if (gameplayMusicPlayer == null) {
			gameplayMusicPlayer = new GameplayMusicPlayer();
			gameplayMusicPlayer.run();
		}
	}

	private void stopGameplayMusic() {
		if (gameplayMusicPlayer != null)
			gameplayMusicPlayer.stop();
		gameplayMusicPlayer = null;
		gameplayEffectsPlayers.forEach(MediaPlayer::stop);
	}

	public void playGameplaySoundEffect(GameplaySoundEffect effect) {
		Platform.runLater(() -> {
			MediaPlayer player = new MediaPlayer(effect.getMedia());
			player.setMute(effectsMuted);
			player.setVolume(effectsVolume);
			MediaView mediaView = new MediaView(player);
			pane.getChildren().add(mediaView);
			gameplayEffectsPlayers.add(player);
			player.setOnEndOfMedia(() -> stopGameplayEffectsPlayers(player, mediaView));
			player.setOnStopped(() -> stopGameplayEffectsPlayers(player, mediaView));
			player.play();
		});
	}

	private void stopGameplayEffectsPlayers(MediaPlayer player, MediaView mediaView) {
		pane.getChildren().remove(mediaView);
		gameplayEffectsPlayers.remove(player);
		player.dispose();
	}

	public void pauseGameplaySoundEffects() {
		gameplayEffectsPlayers.forEach(MediaPlayer::pause);
	}

	public void resumeGameplaySoundEffects() {
		gameplayEffectsPlayers.forEach(MediaPlayer::play);
	}

	private void playMenuMusic() {
		if (menuMusicView != null)
			pane.getChildren().remove(menuMusicView);
		if (menuMusicPlayer != null)
			menuMusicPlayer.dispose();
		menuMusicPlayer = new MediaPlayer(menuMusicMedia);
		menuMusicPlayer.setMute(musicMuted);
		menuMusicPlayer.setVolume(musicVolume);
		menuMusicView = new MediaView(menuMusicPlayer);
		pane.getChildren().add(menuMusicView);
		menuMusicPlayer.setOnEndOfMedia(this::playMenuMusic);
		menuMusicPlayer.play();
	}

	private void stopMenuMusic() {
		if (menuMusicPlayer != null) {
			menuMusicPlayer.stop();
			menuMusicPlayer.dispose();
		}
		if (menuMusicView != null)
			pane.getChildren().remove(menuMusicView);
		menuMusicPlayer = null;
	}

	public void switchMenuGameplayMusic() {
		Platform.runLater(() -> {
			if (gameplayMusicPlayer == null) {
				stopMenuMusic();
				playGameplayMusic();
			} else {
				stopGameplayMusic();
				playMenuMusic();
			}
		});
	}

	public void muteOrUnmuteMusic() {
		musicMuted = !musicMuted;
		if (menuMusicPlayer != null)
			menuMusicPlayer.setMute(musicMuted);
		if (gameplayMusicPlayer != null)
			gameplayMusicPlayer.muteOrUnmute();
	}

	public void muteOrUnmuteEffects() {
		effectsMuted = !effectsMuted;
		gameplayEffectsPlayers.forEach(player -> player.setMute(effectsMuted));
	}

	public boolean isMusicMuted() {
		return musicMuted;
	}

	public boolean isEffectsMuted() {
		return effectsMuted;
	}

	public void setMusicVolume(double musicVolume) {
		checkIfCorrectVolumeValue(musicVolume);
		this.musicVolume = musicVolume;
		if (gameplayMusicPlayer != null)
			gameplayMusicPlayer.setVolume();
		if (menuMusicPlayer != null)
			menuMusicPlayer.setVolume(musicVolume);
	}

	public double getMusicVolume() {
		return musicVolume;
	}

	public void setEffectsVolume(double effectsVolume) {
		checkIfCorrectVolumeValue(effectsVolume);
		this.effectsVolume = effectsVolume;
		gameplayEffectsPlayers.forEach(player -> player.setVolume(effectsVolume));
	}

	public double getEffectsVolume() {
		return effectsVolume;
	}

	private void checkIfCorrectVolumeValue(double level) {
		if (level < 0.0 || level > 1.0)
			throw new IllegalValueException("Volume Level must be between 0.0 and 1.0");
	}

	public void saveUserPreferences() {
		UserPreferences.PREFERENCES.putBoolean(PreferencesKey.EFFECTS_MUTED.getKey(), effectsMuted);
		UserPreferences.PREFERENCES.putBoolean(PreferencesKey.MUSIC_MUTED.getKey(), musicMuted);
		UserPreferences.PREFERENCES.putDouble(PreferencesKey.EFFECTS_VOLUME.getKey(), effectsVolume);
		UserPreferences.PREFERENCES.putDouble(PreferencesKey.MUSIC_VOLUME.getKey(), musicVolume);
	}

}