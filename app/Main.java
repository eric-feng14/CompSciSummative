package app;
import players.*;
import becker.robots.*;
import tools.*;

/**
 * A friendly game of tag
 * @author Eric, Felix, and Richard
 * @version 5/26/2025
 */
public class Main {
	
	final public static int numOfPlayers = 5;
	private static Player[] players = new Player[numOfPlayers];
	private static PlayerRecord[] playerRecords = new PlayerRecord[players.length];
	
	/**
	 * 
	 * @param numOfTargets number of runners + number of medics
	 * @return
	 */
	public static boolean gameEnd() {
		for (Player p : players) {
			if (! p.isDefeated()) { //if there is a player that hasn't been the defeated, the game continues
				return false;
			}
		}
		return true; //all enemies have been defeated, game should end
	}
	
	public static void main(String[] args) {
		City city = new City(10, 24);

		WallCreator creator = new WallCreator(city);
		creator.createWallRect(0, 0, 24, 13);
		
		players[0] = new Runner(city, 4, 4, Direction.EAST);
		players[1] = new Attacker(city, 6, 6, Direction.WEST);
		players[2] = new Medic(city, 8, 8, Direction.NORTH);
		players[3] = new Attacker(city, 4, 5, Direction.SOUTH);
		players[4] = new Medic(city, 1, 1, Direction.SOUTH);
		updatePlayerRecord();
		updateTags();
		initializePlayers();
		
		int idx = 0;
		// Game loop
		while (!gameEnd()) {
		    players[idx].performAction(playerRecords);
		    idx = (idx + 1) % players.length;
		    updatePlayerRecord();
		    updateTags();
		}
	}
	
	/**
	 * still need to decide on what to put on the tags
	 */
	public static void updateTags() {
		for (Player p : Main.players) {
			p.setLabel("" + p.getPLAYER_ID());
		}
	}
	
	public static void initializePlayers() {
		for (Player p : Main.players) {
			p.initialize(playerRecords);
			updatePlayerRecord();
		}
	}
	
	/**
	 * Updates Player records
	 */
	public static void updatePlayerRecord() {
		for (int i = 0; i < Main.players.length; i++) {
			Main.playerRecords[i] = new PlayerRecord(players[i]);
		}
	}
}
