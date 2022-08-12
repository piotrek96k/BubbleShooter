package com.piotrek.model.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.piotrek.exception.ReadingFileException;
import com.piotrek.exception.WritingFileException;
import com.piotrek.model.gameplay.Gameplay;
import com.piotrek.model.gameplay.PointsCounter;
import com.piotrek.model.gameplay.TimeCounter;
import com.piotrek.model.mode.DifficultyLevel;
import com.piotrek.model.mode.GameMode;

public class Player implements Serializable {

    private static final long serialVersionUID;

    private static final String PLAYERS_PATH;

    private static final String PLAYERS_FILE;

    private static List<Player> survivalModePlayers;

    private static Map<DifficultyLevel, List<Player>> arcadeModePlayers;

    private static Comparator<Player> survivalModeComparator;

    private static Comparator<Player> arcadeModeComparator;

    private final String name;

    private int id;

    private final long points;

    private final long time;

    static {
        serialVersionUID = 2383760553340651268L;
        PLAYERS_PATH = System.getProperty("user.home") + "/AppData/Local/BubbleShooter";
        PLAYERS_FILE = PLAYERS_PATH + "/scores.sc";
    }

    private Player(long points, long time) {
        this(null, points, time);
    }

    private Player(String name, long points, long time) {
        this.name = name;
        this.points = points;
        this.time = time;
    }

    public static void addPlayer(Gameplay gameplay, Optional<String> name)
            throws ReadingFileException, WritingFileException {
        readIfNull();
        if (name.isEmpty())
            return;
        GameMode gameMode = gameplay.getGameMode();
        Player newPlayer = new Player(name.get(), gameplay.getPoints(), gameplay.getTime());
        if (gameMode.equals(GameMode.SURVIVAL_MODE))
            addPlayerToSet(newPlayer, survivalModePlayers, survivalModeComparator);
        else if (gameplay.isVictorious())
            addPlayerToSet(newPlayer, arcadeModePlayers.get(gameMode.getDifficultyLevel()), arcadeModeComparator);
    }

    private static void addPlayerToSet(Player newPlayer, List<Player> players, Comparator<Player> comparator)
            throws WritingFileException {
        for (int i = 0; i < players.size(); i++) {
            if (comparator.compare(newPlayer, players.get(i)) > 0) {
                newPlayer.setId(i + 1);
                if (players.size() < 10) {
                    Player player = players.get(players.size() - 1);
                    player.setId(players.size() + 1);
                    players.add(player);
                }
                for (int j = players.size() - 2; j >= i; j--) {
                    players.get(j).setId(j + 2);
                    players.set(j + 1, players.get(j));
                }
                players.set(i, newPlayer);
                write();
                return;
            }
        }
        if (players.size() < 10) {
            newPlayer.setId(players.size() + 1);
            players.add(newPlayer);
            write();
        }
    }

    public static int willBeAddedToList(Gameplay gameplay) throws ReadingFileException {
        readIfNull();
        GameMode gameMode = gameplay.getGameMode();
        Player newPlayer = new Player(gameplay.getPoints(), gameplay.getTime());
        if (gameMode.equals(GameMode.SURVIVAL_MODE))
            return comparePlayers(newPlayer, survivalModePlayers, survivalModeComparator);
        else if (gameplay.isVictorious())
            return comparePlayers(newPlayer, arcadeModePlayers.get(gameMode.getDifficultyLevel()),
                    arcadeModeComparator);
        return -1;
    }

    private static int comparePlayers(Player newPlayer, List<Player> players, Comparator<Player> comparator) {
        for (int i = 0; i < players.size(); i++) {
            if (comparator.compare(newPlayer, players.get(i)) > 0)
                return i;
        }
        if (players.size() < 10)
            return players.size();
        return -1;
    }

    public static List<Player> getSurvivalModePlayers() throws ReadingFileException {
        readIfNull();
        return survivalModePlayers;
    }

    public static List<Player> getArcadeModePlayers(DifficultyLevel level) throws ReadingFileException {
        readIfNull();
        return arcadeModePlayers.get(level);
    }

    private static void readIfNull() throws ReadingFileException {
        if (survivalModePlayers == null || arcadeModePlayers == null) {
            survivalModePlayers = new ArrayList<>();
            arcadeModePlayers = new HashMap<>();
            for (DifficultyLevel level : DifficultyLevel.values())
                arcadeModePlayers.put(level, new ArrayList<>());
            Comparator<Player> timeComparator = Comparator.comparing(Player::getUnmodifiedTime).reversed();
            Comparator<Player> pointsComparator = Comparator.comparing(Player::getUnmodifiedPoints);
            survivalModeComparator = pointsComparator.thenComparing(timeComparator);
            arcadeModeComparator = timeComparator.thenComparing(pointsComparator);
            read();
        }
    }

    private static void read() throws ReadingFileException {
        File file = new File(PLAYERS_FILE);
        if (file.exists())
            try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file))) {
                Object object = stream.readObject();
                if (object instanceof List<?>)
                    for (Object obj : (List<?>) object) {
                        if (obj instanceof List<?>) {
                            for (Object o : (List<?>) obj)
                                if (o instanceof Player)
                                    survivalModePlayers.add((Player) o);
                        } else if (obj instanceof Map<?, ?>) {
                            ((Map<?, ?>) obj).forEach((key, value) -> {
                                if (key instanceof DifficultyLevel && value instanceof List<?>) {
                                    List<Player> list = arcadeModePlayers.get(key);
                                    for (Object o : (List<?>) value)
                                        if (o instanceof Player)
                                            list.add((Player) o);
                                }
                            });
                        }
                    }
            } catch (IOException | ClassNotFoundException e) {
                throw new ReadingFileException();
            }
    }

    private static void write() throws WritingFileException {
        File file = new File(PLAYERS_PATH);
        if (!file.exists() && !file.mkdirs())
            throw new WritingFileException();
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(PLAYERS_FILE))) {
            List<Object> toWrite = new ArrayList<>(2);
            toWrite.add(arcadeModePlayers);
            toWrite.add(survivalModePlayers);
            stream.writeObject(toWrite);
        } catch (IOException e) {
            throw new WritingFileException();
        }
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getPoints() {
        return PointsCounter.getFormattedPoints(points);
    }

    private long getUnmodifiedTime() {
        return time;
    }

    private long getUnmodifiedPoints() {
        return points;
    }

    public String getTime() {
        return TimeCounter.getFullFormattedTime(time);
    }

    @Override
    public String toString() {
        return "Player [ id=" + id + ", name=" + name + ", points=" + points + ", time=" + time + "]";
    }

}