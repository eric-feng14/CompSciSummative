package app;
import players.*;
import powerUps.*;
import becker.robots.*;
import tools.*;
import java.util.*;
/**
 * A friendly game of tag -> main application class that we collectively worked on
 * @author Eric, Felix, and Richard
 * @version Due date: June 13 2025
 */
public class Main {

	final private static int NUM_OF_PLAYERS = 5, NUM_OF_POWERUPS = 6;
	final private static int STREET_SIZE = 13, AVENUE_SIZE = 24;
	private static Player[] players = new Player[NUM_OF_PLAYERS];
	private static PlayerRecord[] playerRecords = new PlayerRecord[players.length];
	private static ArrayList<EnhancedThing> powerUps = new ArrayList<EnhancedThing>();
	private static final Random RANDOM = new Random();

	/**
	 * Determines when to end the game
	 * @param numOfTargets number of runners + number of medics
	 * @return true if all players are defeated
	 */
	public static boolean gameEnd() {
		for (Player p : Main.players) {
			if (! p.isDefeated() && p.getTYPE() != "Attacker") { //if there is a player that hasn't been the defeated, the game continues
				return false;
			}
		}
		return true; //all enemies have been defeated, game should end
	}

	/**
	 * The main method
	 * @param args
	 */
	public static void main(String[] args) {
		City city = new City(Main.STREET_SIZE, Main.AVENUE_SIZE);

		WallCreator creator = new WallCreator(city);
		creator.createWallRect(0, 0, Main.AVENUE_SIZE, Main.STREET_SIZE);

		Main.players[0] = new Runner(city, 4, 4, Direction.EAST);
		Main.players[1] = new Attacker(city, 6, 7, Direction.WEST);
		Main.players[2] = new Medic(city, 8, 8, Direction.NORTH);
		Main.players[3] = new Attacker(city, 6, 9, Direction.SOUTH);
		Main.players[4] = new Runner(city, 1, 1, Direction.SOUTH);
		updatePlayerRecords();
		updateTags();
		initializePlayers();
		addPowerUps(city);

		int idx = 0;
		// Game loop
		while (!gameEnd()) {
			System.out.println("index: " + idx + ", HP: " + Main.players[idx].getHp());
			if (!Main.players[idx].isDefeated()) {
				Main.players[idx].performAction(Main.playerRecords, Main.powerUps);
				Main.players[idx].sendSignal();
			}

			updatePlayerRecord(idx);
			updateTag(idx);
			idx = (idx + 1) % Main.players.length;
		}
	}
	
	/**
	 * Initialize the power-ups
	 * @param c - the city
	 */
	private static void addPowerUps(City c) {
		for (int i = 0; i < Main.NUM_OF_POWERUPS; i++) {
			int choice = Main.RANDOM.nextInt(3);
			int newStreet = Main.RANDOM.nextInt(Main.STREET_SIZE), newAvenue = RANDOM.nextInt(Main.AVENUE_SIZE);
			switch(choice) {
			case 0: 
				Main.powerUps.add(new LuckPowerUp(c, newStreet, newAvenue));
				break;
			case 1:
				Main.powerUps.add(new SpeedPowerUp(c, newStreet, newAvenue));
				break;
			case 2:
				Main.powerUps.add(new StaminaPowerUp(c, newStreet, newAvenue));
				break;
			}
		}
	}
	
	/**
	 * Calculates the chances of each type of hit based on the attacker's strength and the runner/medic's defense
	 * @param attacker - the playerID of the attacker
	 * @param victum - the playerID of the target
	 * @return An array of doubles of the chances of each type of hit
	 */
	private static double[] calculateChances(int attacker, int victum) {
		double attackerStrength = Main.players[attacker].getStrength();
		double runnerDefense = Main.players[victum].getDefense();

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
	
	/**
	 * Select a type of hit 
	 * @param probabilities - the probabilities of each type of hit
	 * @return the index of the type of hit
	 */
	private static int chooseType (double[] probabilities) {
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
	 * Subtract HP to target from demageDealt
	 * @param damageDealt - damage to subtract
	 * @param targetID - the playerID of target
	 */
	private static void performAttack(int damageDealt, int targetID) {
		Main.players[targetID].setHp(players[targetID].getHp() - damageDealt);

		if (Main.players[targetID].getHp() <= 0) {
			Main.players[targetID].setDefeated(true);
			Main.players[targetID].destroy();
		}
	}

	/**
	 * Initializes the player records
	 */
	private static void initializePlayers() {
		for (Player p : Main.players) {
			p.initialize(Main.playerRecords);
		}
	}

	/**
	 * Shows the HP of players on player tags
	 */
	private static void updateTags() {
		for (Player p : Main.players) {
			p.setLabel("" + p.getHp());
		}
	}

	/**
	 * Updates Player records
	 */
	private static void updatePlayerRecords() {
		for (int i = 0; i < Main.players.length; i++) {
			Main.playerRecords[i] = new PlayerRecord(Main.players[i]);
		}
	}

	/**
	 * Overloaded method that updates the player record of index
	 * @param index - index of the player
	 */
	private static void updatePlayerRecord(int index) {
		Main.playerRecords[index] = new PlayerRecord(Main.players[index]);
	}
	
	/**
	 * Updates the tag of player at idx
	 * @param idx - index of the player to update
	 */
	private static void updateTag(int idx) {
		Main.players[idx].setLabel("" + Main.players[idx].getHp());
	}

	/**
	 * Signals main to do action specified in string
	 * @param s - signal message (string)
	 */
	public static void signal(String s, int thisID, int targetID) {
		if (s.equals("attack")) {
			double[] chances = calculateChances(thisID, targetID);
			int attackType = chooseType(chances);
			int damageDealt;
			switch(attackType) {
			case 1: damageDealt = Player.getNormalHit(); break;
			case 2: damageDealt = Player.getCriticalHit(); break;
			case 3: damageDealt = Player.getKnockout(); break;
			default: damageDealt = 0; break; //there is a chance of dodging
			}
			performAttack(damageDealt, targetID);
			updatePlayerRecord(targetID);
			updateTag(targetID);

			Main.players[thisID].sendInfo(damageDealt, targetID);
		}
		else if (s.equals("heal")) {
			Player targetPlayer = Main.players[targetID];
			if (targetPlayer != null) {
				targetPlayer.setHp(targetPlayer.getHp() + 20);
			}
		}

		else if (s.equals("remove")) {
			for (int i = 0; i < Main.powerUps.size(); i ++) {
				if (Main.powerUps.get(i).getID() == targetID) {
					Main.powerUps.remove(i);
					i--;
				}
			}
		}
	}
}
