package com.piotrek.sound;

import java.net.URISyntaxException;

import javafx.scene.media.Media;

public enum GameplaySoundEffect {

    BURNING("/sound/burning.mp3"),

    EXPLOSION("/sound/explosion.mp3"),

    FALLING("/sound/falling.mp3"),

    GHOST("/sound/ghost.mp3"),

    POP("/sound/pop.mp3"),

    THROW("/sound/throw.mp3");

    private Media media;

    GameplaySoundEffect(String effect) {
        try {
            media = new Media(getClass().getResource(effect).toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Media getMedia() {
        return media;
    }

}