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
public class MedicTester {

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
		for (Player p : MedicTester.players) {
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
		City city = new City(MedicTester.STREET_SIZE, MedicTester.AVENUE_SIZE);

		WallCreator creator = new WallCreator(city);
		creator.createWallRect(0, 0, MedicTester.AVENUE_SIZE, MedicTester.STREET_SIZE);

		MedicTester.players[0] = new Runner(city, 4, 4, Direction.EAST);
		MedicTester.players[1] = new Attacker(city, 6, 7, Direction.WEST);
		MedicTester.players[2] = new Medic(city, 8, 8, Direction.NORTH);
		MedicTester.players[3] = new Attacker(city, 6, 9, Direction.SOUTH);
		MedicTester.players[4] = new Runner(city, 1, 1, Direction.SOUTH);
		updatePlayerRecords();
		updateTags();
		initializePlayers();
		addPowerUps(city);

		int idx = 0;
		// Game loop
		while (!gameEnd()) {
			System.out.println("index: " + idx + ", HP: " + MedicTester.players[idx].getHp());
			if (!MedicTester.players[idx].isDefeated()) {
				MedicTester.players[idx].performAction(MedicTester.playerRecords, MedicTester.powerUps);
				MedicTester.players[idx].sendSignal();
			}

			updatePlayerRecord(idx);
			updateTag(idx);
			idx = (idx + 1) % MedicTester.players.length;
		}
	}
	
	/**
	 * Initialize the power-ups
	 * @param c - the city
	 */
	private static void addPowerUps(City c) {
		for (int i = 0; i < MedicTester.NUM_OF_POWERUPS; i++) {
			int choice = MedicTester.RANDOM.nextInt(3);
			int newStreet = MedicTester.RANDOM.nextInt(MedicTester.STREET_SIZE), newAvenue = RANDOM.nextInt(MedicTester.AVENUE_SIZE);
			switch(choice) {
			case 0: 
				MedicTester.powerUps.add(new LuckPowerUp(c, newStreet, newAvenue));
				break;
			case 1:
				MedicTester.powerUps.add(new SpeedPowerUp(c, newStreet, newAvenue));
				break;
			case 2:
				MedicTester.powerUps.add(new StaminaPowerUp(c, newStreet, newAvenue));
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
		double attackerStrength = MedicTester.players[attacker].getStrength();
		double runnerDefense = MedicTester.players[victum].getDefense();

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
		double rand = MedicTester.RANDOM.nextDouble();  // Random double between 0.0 and 1.0
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
		MedicTester.players[targetID].setHp(players[targetID].getHp() - damageDealt);

		if (MedicTester.players[targetID].getHp() <= 0) {
			MedicTester.players[targetID].setDefeated(true);
			MedicTester.players[targetID].destroy();
		}
	}

	/**
	 * Initializes the player records
	 */
	private static void initializePlayers() {
		for (Player p : MedicTester.players) {
			p.initialize(MedicTester.playerRecords);
		}
	}

	/**
	 * Shows the HP of players on player tags
	 */
	private static void updateTags() {
		for (Player p : MedicTester.players) {
			p.setLabel("" + p.getHp());
		}
	}

	/**
	 * Updates Player records
	 */
	private static void updatePlayerRecords() {
		for (int i = 0; i < MedicTester.players.length; i++) {
			MedicTester.playerRecords[i] = new PlayerRecord(MedicTester.players[i]);
		}
	}

	/**
	 * Overloaded method that updates the player record of index
	 * @param index - index of the player
	 */
	private static void updatePlayerRecord(int index) {
		MedicTester.playerRecords[index] = new PlayerRecord(MedicTester.players[index]);
	}
	
	/**
	 * Updates the tag of player at idx
	 * @param idx - index of the player to update
	 */
	private static void updateTag(int idx) {
		MedicTester.players[idx].setLabel("" + MedicTester.players[idx].getHp());
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

			MedicTester.players[thisID].sendInfo(damageDealt, targetID);
		}
		else if (s.equals("heal")) {
			Player targetPlayer = MedicTester.players[targetID];
			targetPlayer.setHp(targetPlayer.getHp() + 20);
		}

		else if (s.equals("remove")) {
			for (int i = 0; i < MedicTester.powerUps.size(); i ++) {
				if (MedicTester.powerUps.get(i).getID() == targetID) {
					MedicTester.powerUps.remove(i);
					i--;
				}
			}
		}
	}
}
