package com.project.sound;

import java.net.URISyntaxException;

import javafx.scene.media.Media;

public enum GameplayMusic {

    GAMEPLAY_MUSIC_1("/sound/gameMusic1.mp3"),

    GAMEPLAY_MUSIC_2("/sound/gameMusic2.mp3"),

    GAMEPLAY_MUSIC_3("/sound/gameMusic3.mp3");

    private Media media;

    GameplayMusic(String music) {
        try {
            media = new Media(getClass().getResource(music).toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Media getMedia() {
        return media;
    }

}
