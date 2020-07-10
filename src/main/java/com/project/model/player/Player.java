package com.project.model.player;

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

import com.project.model.gameplay.Gameplay;
import com.project.model.gameplay.PointsCounter;
import com.project.model.mode.DifficultyLevel;
import com.project.model.mode.GameMode;

public class Player implements Serializable {

	private static final String PLAYERS_FILE;

	private static final long serialVersionUID;

	private static List<Player> survivalModePlayers;

	private static Map<DifficultyLevel, List<Player>> arcadeModePlayers;

	private static Comparator<Player> survivalModeComparator;

	private static Comparator<Player> arcadeModeComparator;

	private String name;

	private int id;

	private long points;

	private long time;

	static {
		PLAYERS_FILE = "gracze.gr";
		serialVersionUID = 2383760553340651268L;
		survivalModePlayers = new ArrayList<Player>();
		arcadeModePlayers = new HashMap<DifficultyLevel, List<Player>>();
		for (DifficultyLevel level : DifficultyLevel.values())
			arcadeModePlayers.put(level, new ArrayList<Player>());
		Comparator<Player> timeComparator = Comparator.comparing(Player::getTime).reversed();
		Comparator<Player> pointsComparator = Comparator.comparing(Player::getPoints);
		survivalModeComparator = pointsComparator.thenComparing(timeComparator);
		arcadeModeComparator = timeComparator.thenComparing(pointsComparator);
		read();
	}

	private Player(long points, long time) {
		this(null, points, time);
	}

	private Player(String name, long points, long time) {
		this.name = name;
		this.points = points;
		this.time = time;
	}

	public static void addPlayer(Gameplay gameplay, Optional<String> name) {
		if (!name.isPresent() || name.isEmpty())
			return;
		GameMode gameMode = gameplay.getGameMode();
		Player newPlayer = new Player(name.get(), gameplay.getPoints(), gameplay.getTime());
		if (gameMode.equals(GameMode.SURVIVAL_MODE))
			addPlayerToSet(newPlayer, survivalModePlayers, survivalModeComparator);
		else if (gameplay.isVictorious())
			addPlayerToSet(newPlayer, arcadeModePlayers.get(gameMode.getDifficultyLevel()), arcadeModeComparator);
	}

	private static void addPlayerToSet(Player newPlayer, List<Player> players, Comparator<Player> comparator) {
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

	public static boolean willBeAddedToList(Gameplay gameplay) {
		GameMode gameMode = gameplay.getGameMode();
		Player newPlayer = new Player(gameplay.getPoints(), gameplay.getTime());
		if (gameMode.equals(GameMode.SURVIVAL_MODE))
			return comparePlayers(newPlayer, survivalModePlayers, survivalModeComparator);
		else if (gameplay.isVictorious())
			return comparePlayers(newPlayer, arcadeModePlayers.get(gameMode.getDifficultyLevel()),
					arcadeModeComparator);
		return false;
	}

	private static boolean comparePlayers(Player newPlayer, List<Player> players, Comparator<Player> comparator) {
		if (players.size() < 10)
			return true;
		for (Player player : players)
			if (comparator.compare(newPlayer, player) > 0)
				return true;
		return false;
	}

	public static List<Player> getSurvivalModePlayers() {
		return survivalModePlayers;
	}

	public static List<Player> getArcadeModePlayers(DifficultyLevel level) {
		return arcadeModePlayers.get(level);
	}

	private static void read() {
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
				e.printStackTrace();
			}
	}

	private static void write() {
		try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(PLAYERS_FILE))) {
			List<Object> toWrite = new ArrayList<Object>(2);
			toWrite.add(arcadeModePlayers);
			toWrite.add(survivalModePlayers);
			stream.writeObject(toWrite);
		} catch (IOException e) {
			e.printStackTrace();
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

	public String getTime() {
		long time = this.time / 1000;
		int seconds = (int) time % 60;
		time /= 60;
		int minutes = (int) time % 60;
		time /= 60;
		int hours = (int) time % 24;
		time /= 24;
		int days = (int) time;
		return String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
	}

	@Override
	public String toString() {
		return "Player [ id=" + id + ", name=" + name + ", points=" + points + ", time=" + time + "]";
	}

}