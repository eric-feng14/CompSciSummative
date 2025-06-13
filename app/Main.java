package app;
import players.*;
import powerUps.*;
import becker.robots.*;
import tools.*;
import java.util.*;
/**
 * A friendly game of tag
 * @author Eric, Felix, and Richard
 * @version 5/26/2025
 */
public class Main {
	
	final private static int NUM_OF_PLAYERS = 5, NUM_OF_POWERUPS = 6;
	final private static int STREET_SIZE = 13, AVENUE_SIZE = 24;
	private static Player[] players = new Player[NUM_OF_PLAYERS];
	private static PlayerRecord[] playerRecords = new PlayerRecord[players.length];
	private static EnhancedThing[] powerUps = new EnhancedThing[NUM_OF_POWERUPS];
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
		City city = new City(STREET_SIZE, AVENUE_SIZE);

		WallCreator creator = new WallCreator(city);
		creator.createWallRect(0, 0, AVENUE_SIZE, STREET_SIZE);
		
		players[0] = new Runner(city, 4, 4, Direction.EAST);
		players[1] = new Attacker(city, 6, 7, Direction.WEST);
		players[2] = new Medic(city, 8, 8, Direction.NORTH);
		players[3] = new Attacker(city, 6, 9, Direction.SOUTH);
		players[4] = new Runner(city, 1, 1, Direction.SOUTH);
		updatePlayerRecords();
		updateTags();
		initializePlayers();
		addPowerUps(city);
		
		int idx = 0;
		// Game loop
		while (!gameEnd()) {
			System.out.println("HP: " + players[idx].getHp());
			players[idx].performAction(playerRecords, powerUps);
			players[idx].sendSignal();
			
			updatePlayerRecord(idx);
			updateTag(idx);
		    idx = (idx + 1) % players.length;
		}
	}
	
	private static void addPowerUps(City c) {
		for (int i = 0; i < powerUps.length; i++) {
			int choice = RANDOM.nextInt(3);
			System.out.println(choice);
			int newStreet = RANDOM.nextInt(STREET_SIZE), newAvenue = RANDOM.nextInt(AVENUE_SIZE);
			switch(choice) {
			case 0: 
				powerUps[i] = new LuckPowerUp(c, newStreet, newAvenue);
				break;
			case 1:
				powerUps[i] = new SpeedPowerUp(c, newStreet, newAvenue);
				break;
			case 2:
				powerUps[i] = new StaminaPowerUp(c, newStreet, newAvenue);
				break;
			}
		}
	}
	
	public static void handlePowerUps() {
		System.out.println("test");
	}
	
	private static double[] calculateChances(int attacker, int victum) {
		double attackerStrength = players[attacker].getStrength();
		double runnerDefense = players[victum].getDefense();
		
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
	
	private static int chooseType (double[] probabilities) {
		double rand = RANDOM.nextDouble();  // Random double between 0.0 and 1.0
        double cumulative = 0.0;
        
   
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (rand < cumulative) {
                return i;
            }
        }
        return 1;
	}
	
	private static void performAttack(int attackType, int targetID) {
		switch(attackType) {
    	case 0: break;
    	case 1: players[targetID].setHp(players[targetID].getHp() - Player.getNormalHit()); break;
    	case 2: players[targetID].setHp(players[targetID].getHp() - Player.getCriticalHit()); break;
    	case 3: players[targetID].setHp(players[targetID].getHp() - Player.getKnockout()); break;
    	}
		
		if (players[targetID].getHp() <= 0) {
    		players[targetID].setDefeated(true);
    	}
	}
	
	private static void initializePlayers() {
		for (Player p : players) {
			p.initialize(playerRecords);
		}
	}
	
	/**
	 * still need to decide on what to put on the tags
	 */
	private static void updateTags() {
		for (Player p : players) {
			p.setLabel("" + p.getHp());
		}
	}
	
	/**
	 * Updates Player records
	 */
	private static void updatePlayerRecords() {
		for (int i = 0; i < players.length; i++) {
			playerRecords[i] = new PlayerRecord(players[i]);
		}
	}
	
	/**
	 * Overloaded method that updates the player record of index
	 * @param index - index of the player
	 */
	private static void updatePlayerRecord(int index) {
		playerRecords[index] = new PlayerRecord(players[index]);
	}
	
	private static void updateTag(int idx) {
		players[idx].setLabel("" + players[idx].getHp());
	}
	
	/**
	 * Signals main to do action specified in string
	 * @param s - signal message (string)
	 */
	public static void signal(String s, int thisID, int targetID) {
		if (s.equals("attack")) {
			double[] chances = calculateChances(thisID, targetID);
			int attackType = chooseType(chances);
			performAttack(attackType, targetID);
			updatePlayerRecord(targetID);
			updateTag(targetID);
		}
		else
		if (s.equals("heal")) {}
	}
}
