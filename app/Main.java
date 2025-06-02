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
	
	final public static int numOfPlayers = 6;
	private static Player[]  players = new Player[numOfPlayers];
	private static PlayerRecord[] playerRecords = new PlayerRecord[players.length];
	
	/**
	 * 
	 * @param numOfTargets number of runners + number of medics
	 * @return
	 */
	public static boolean gameEnd() {
		for (Player p : players) {
			if (! p.isDefeated) { //if there is a player that hasn't been the defeated, the game continues
				return false;
			}
		}
		return true; //all enemies have been defeated, game should end
	}
	
	public static void main(String[] args) {
		City city = new City(13, 24);

		WallCreator creator = new WallCreator(city);
		creator.createWallRect(0, 0, 24, 13);
		
		//Create the robots
		players[0] = new Attacker(city, 0, 0, Direction.EAST);
		players[1] = new Medic(city, 0, 1, Direction.EAST);
		players[2] = new Runner(city, 1, 0, Direction.EAST);
		updatePlayerRecord();
		int idx = 0;
		// Game loop
		while (!gameEnd()) {
			updatePlayerRecord();
		    players[idx].doThing(playerRecords);
		    idx = (idx + 1) % players.length;
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
