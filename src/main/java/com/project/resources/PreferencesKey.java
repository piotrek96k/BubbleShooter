package com.project.resources;

public enum PreferencesKey {

    EFFECTS_MUTED("effectsMute"),

    MUSIC_MUTED("musicMute"),

    EFFECTS_VOLUME("effectsVolume"),

    MUSIC_VOLUME("musicVolume");

    private final String key;

    PreferencesKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
