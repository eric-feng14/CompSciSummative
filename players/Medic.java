package players;
import java.awt.Color;
import becker.robots.*;

public class Medic extends Player{
	PlayerRecord[] runnerPriority, attackerPriority, medicPriority;
	
	/**
	 * PlayerRecord constructor
	 * @param c - city
	 * @param s - street
	 * @param a - avenue
	 * @param d - direction
	 */
	public Medic (City c, int s, int a, Direction d) {
		super(c, s, a, d, 3, "Medic", false);
		this.setColor(new Color(133, 248, 108));
	}
	
	/**
	 * Sorts priorities
	 */
	private void sortPriority(PlayerRecord[] players) {
		PlayerRecord[] prevRunnerPriority = this.runnerPriority.clone();
		PlayerRecord[] prevAttackerPriority = this.attackerPriority.clone();
		PlayerRecord[] prevMedicPriority = this.medicPriority.clone();
		
		this.runnerPriority = this.getTypeArray("Runner", players);
		this.attackerPriority = this.getTypeArray("Attacker", players);
		this.medicPriority = this.getTypeArray("Medic", players);
		
		this.updateRunnerSpeeds();
	}
	
	private void updateRunnerSpeeds() {
	
	}
	
	private void getPriorityValues(PlayerRecord[] players) {
		
	}
	
	private int[] getPredictedProximityValues(PlayerRecord[] players) {
		int[] proximity = getProximityValues(players);
		int[] speeds = new int[players.length];
		for (int i = 0; i < players.length; i++) {
			speeds[i] = players[i].getSpeed();
		}
	}
	
	/**
	 * Gets the relative distance values of players
	 * @param players - the list of specific player records
	 * @return - the proximity of each player
	 */
	private int[] getProximityValues(PlayerRecord[] players) {
		int[] proximity = new int[players.length];
		// Gets relative distance values of players
		for (int i = 0; i < players.length; i++) {
			proximity[i] = Math.abs(players[i].getAvenue() - this.getAvenue()) + 
					Math.abs(players[i].getStreet() - this.getStreet());
		}
		return proximity;
	}
	
	

	/**
	 * Performs main action
	 */
	@Override
	public void performAction(PlayerRecord[] players) {
		// Only moves if is defeated
		if (!this.isDefeated()) {
			this.sortPriority(players);
		}
	}
	
	/**
	 * Gets an array of a type of player from main player list
	 * @param type - type of player
	 * @param players - players list
	 * @return - players list of certain type
	 */
	private PlayerRecord[] getTypeArray(String type, PlayerRecord[] players) {
		int count = this.getNumberOfType(type, players);
		PlayerRecord[] playerPriority = new PlayerRecord[count];
		
		// Fills the list
		int index = 0;
		for (int i = 0; i < players.length; i++) {
			// Adds players to priority list
			if (players[i].getTYPE().equals(type)) {
				runnerPriority[index] = players[i];
				index++;
			}
		}
		
		return playerPriority;
	}
	
	/**
	 * Determines number of type in the list of player records
	 * @param type - type
	 * @param players - player record array
	 * @return - number of type in player record
	 */
	private int getNumberOfType(String type, PlayerRecord[] players) {
		int count = 0;
		
		// Determines number of runners and attackers
		for (int i = 0; i < players.length; i++) {
			// Compares equality to type
			if (players[i].getTYPE().equals(type))
				count++;
		}
		
		return count;
	}
	
}
