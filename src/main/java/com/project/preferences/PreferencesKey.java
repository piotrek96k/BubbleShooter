package com.project.preferences;

public enum PreferencesKey {

	EFFECTS_MUTED("effectsMute"),

	MUSIC_MUTED("musicMute"),

	EFFECTS_VOLUME("effectsVolume"),

	MUSIC_VOLUME("musicVolume");

	private String key;

	private PreferencesKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}
