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
	private static Player[]  players = new Player[numOfPlayers];
	
	/**
	 * 
	 * @param numOfTargets number of runners + number of medics
	 * @return
	 */
	public static boolean gameEnd(int numOfTargets) {
		int totalEnemiesDefeated = 0;
		for (Player p : players) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		City city = new City(13, 24);

		WallCreator creator = new WallCreator(city);
		creator.createWallRect(0, 0, 24, 13);
		
		//Create the robots
		Player[] players = new Player[numOfPlayers];
		players[0] = new Attacker(city, 0, 0, Direction.EAST);
		players[1] = new Medic(city, 0, 1, Direction.EAST);
		players[2] = new Runner(city, 1, 0, Direction.EAST);
		
		int idx = 0;
		while (idx < players.length) {
			
			idx++;
		}
	}
}
