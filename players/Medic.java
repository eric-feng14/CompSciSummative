package players;
import java.awt.Color;
import becker.robots.*;

public class Medic extends Player{
	private PlayerRecord[] prevPlayers;
	private PlayerRecord[] runnerPriority, attackerPriority, medicPriority;
	
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
		players = this.getUpdatedSpeeds(players);
		PlayerRecord[] prevRunnerPriority = this.runnerPriority.clone();
		PlayerRecord[] prevAttackerPriority = this.attackerPriority.clone();
		PlayerRecord[] prevMedicPriority = this.medicPriority.clone();
		
		this.runnerPriority = this.getTypeArray("Runner", players);
		this.attackerPriority = this.getTypeArray("Attacker", players);
		this.medicPriority = this.getTypeArray("Medic", players);
	}
	
	/**
	 * Returns player records with speeds filled
	 * @param players - players
	 * @return - records with speeds filled
	 */
	private PlayerRecord[] getUpdatedSpeeds(PlayerRecord[] players){
		PlayerRecord[] updatedPlayers = new PlayerRecord[players.length];
	
		// Sets to default of 1 if no moves have been made
		if (this.prevPlayers == null) {
			final int DEFAULT_SET_SPEED = 1;
			for (int i = 0; i < players.length; i++) {
				updatedPlayers[i] = players[i];
				updatedPlayers[i].setSpeed(DEFAULT_SET_SPEED);
			}
		} else {
			// Assumes the maximum speed robot has traveled is its regular speed
			for (int i = 0; i < players.length; i++) {
				updatedPlayers[i] = players[i];
				int newSpeed = Math.abs(players[i].getAvenue() - this.prevPlayers[i].getAvenue()) + 
						Math.abs(players[i].getStreet() - this.prevPlayers[i].getStreet());
				
				// TODO Advanced speed assumption with FATIGUE
				if (newSpeed > this.prevPlayers[i].getSpeed()) {
					updatedPlayers[i].setSpeed(newSpeed);
				}
			}
		}
		
		return updatedPlayers;
	}
	
	/**
	 * Gets priority values of all for all attackers
	 */
	public void getAttackerPriorityValues() {
		int[] predictedProximity = this.getPredictedAttackerProximities(this.attackerPriority);
		// Evaluates all attackers and sorts according to immanence of threat
		for (int i = 0; i < this.attackerPriority.length; i++) {
			for (int j = i; j > 0; i--) {
				if (predictedProximity[j] < predictedProximity[j-1]) {
					int temp = predictedProximity[j-1];
					predictedProximity[j-1] = predictedProximity[j];
					predictedProximity[j] = temp;
					
					PlayerRecord temp2 = this.attackerPriority[j-1];
					this.attackerPriority[j-1] = this.attackerPriority[j];
					this.attackerPriority[j] = temp2;
				}
			}
		}
	}
	
	/**
	 * Returns predicted proximity of robots that are attacking
	 * pre: predictions are of attackers
	 * @param players - players
	 * @return - updated proximities according to speed.
	 */
	private int[] getPredictedAttackerProximities(PlayerRecord[] players) {
		int[] proximity = getProximityValues(players);
		// Updates proximity 
		for (int i = 0; i < players.length; i++) {
			proximity[i] = proximity[i] - players[i].getSpeed();
		}
		
		return proximity;
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
		
		this.prevPlayers = players;
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
