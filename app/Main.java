package app;
import players.*;
import becker.robots.*;
import tools.*;
import java.util.*;
/**
 * A friendly game of tag
 * @author Eric, Felix, and Richard
 * @version 5/26/2025
 */
public class Main {
	
	final private static int numOfPlayers = 5;
	private static Player[] players = new Player[numOfPlayers];
	private static PlayerRecord[] playerRecords = new PlayerRecord[players.length];
	private static final Random RANDOM = new Random();
	
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
		players[1] = new Attacker(city, 6, 7, Direction.WEST);
		players[2] = new Medic(city, 8, 8, Direction.NORTH);
		players[3] = new Attacker(city, 6, 9, Direction.SOUTH);
		players[4] = new Runner(city, 1, 1, Direction.SOUTH);
		updatePlayerRecord();
		updateTags();
		initializePlayers();
		
		int idx = 0;
		// Game loop
		while (!gameEnd()) {
			System.out.println("HP: " + players[idx].getHp());
//			if (!players[idx].isDefeated()) {
				players[idx].performAction(playerRecords);
//			}
		    InfoRecords attacker = players[idx].getThisInfo(); 
		    PlayerRecord victum = players[idx].getRunnerInfo(); 
		    if (attacker != null) {
		    	double[] chances = calculateChances(attacker, victum);
		    	for (double i : chances) {
		    		System.out.println("Chance: " + i);
		    	}
		    	int attackType = chooseType(chances);
		    	System.out.println("Type: " + attackType);
		    	
		    	
		    	switch(attackType) {
		    	case 0: break;
		    	case 1: players[victum.getPLAYER_ID()].setHp(players[victum.getPLAYER_ID()].getHp() - 20); break;
		    	case 2: players[victum.getPLAYER_ID()].setHp(players[victum.getPLAYER_ID()].getHp() - 40); break;
		    	case 3: players[victum.getPLAYER_ID()].setHp(players[victum.getPLAYER_ID()].getHp() - 100); break;
		    	}
//		    	if (players[victum.getPLAYER_ID()].getHp() <= 0) {
//		    		players[victum.getPLAYER_ID()].setDefeated(true);
//		    	}
		    }
		    updatePlayerRecord(idx);
		    updateTags();
		    
		    idx = (idx + 1) % players.length;
		}
	}
	
	public static double[] calculateChances(InfoRecords attacker, PlayerRecord victum) {
		double attackerStrength = attacker.getStrength();
		double runnerDefense = Main.players[victum.getPLAYER_ID()].getDefense();
		
		double normalHit = attackerStrength + 0.5*runnerDefense;
		double critHit = attackerStrength;
		double knockout = attackerStrength - 0.5*runnerDefense;
		double totalWeights = critHit*3;

		double dodgeChance = runnerDefense / (attackerStrength + runnerDefense);
		double hitChance = 1 - dodgeChance;
	
		double normalChance = hitChance*(normalHit/totalWeights);
		double critChance = hitChance*(critHit/totalWeights);
		double knockChance = hitChance*(knockout/totalWeights);
		
		return new double[] {dodgeChance, normalChance, critChance, knockChance};
	}
	
	public static int chooseType (double[] probabilities) {
		double rand = Main.RANDOM.nextDouble();  // Random double between 0.0 and 1.0
        double cumulative = 0.0;
        
   
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (rand < cumulative) {
                return i;
            }
        }
        return 1;
	}
	/**
	 * still need to decide on what to put on the tags
	 */
	public static void updateTags() {
		for (Player p : Main.players) {
			p.setLabel("" + p.getHp());
		}
	}
	
	public static void initializePlayers() {
		for (Player p : Main.players) {
			p.initialize(playerRecords);
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
	
	/**
	 * Updates the player record of index
	 * @param index - index of the player
	 */
	public static void updatePlayerRecord(int index) {
		Main.playerRecords[index] = new PlayerRecord(players[index]);
	}
}
