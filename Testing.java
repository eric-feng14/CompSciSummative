import players.*;
import becker.robots.*;
import tools.*;

/**
 * A friendly game of tag
 * @author Eric, Felix, and Richard
 * @version 5/26/2025
 */
public class Testing {
	
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
			if (! p.isDefeated()) { //if there is a player that hasn't been the defeated, the game continues
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
		
//		players[1] = new Medic(city, 0, 1, Direction.EAST);
		players[0] = new Runner(city, 1, 0, Direction.EAST);
		players[1] = new Attacker(city, 0, 0, Direction.EAST);
		players[2] = new Attacker(city, 12, 23, Direction.WEST);
//		players[4] = new Medic(city, 4, 4, Direction.SOUTH);
		players[3] = new Medic(city, 7, 7, Direction.NORTH);
		players[4] = new Attacker(city, 0, 1, Direction.EAST);
		players[5] = new Runner(city, 9, 1, Direction.WEST);
		Medic medic = new Medic(city, 1, 1, Direction.EAST);
		updatePlayerRecord();
		updateTags();
		initializePlayers();
		
	}
	
	public static void initializePlayers() {
		for (Player p : Testing.players) {
			p.initialize(playerRecords);
		}
	}
	
	public static void updateTags() {
		for (Player p : Testing.players) { 
			p.setLabel(p.getPLAYER_ID() + " " + p.getHP());
		}
	}
	
	/**
	 * Updates Player records
	 */
	public static void updatePlayerRecord() {
		for (int i = 0; i < Testing.players.length; i++) {
			Testing.playerRecords[i] = new PlayerRecord(players[i]);
		}
	}
}