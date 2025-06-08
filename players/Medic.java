package players;
import java.util.ArrayList;
import java.awt.Color;
import playerMods.AmbiguousMovement;
import becker.robots.*;

public class Medic extends Player{
	private PlayerRecord[] prevPlayers;
	private PlayerRecord[] runnerPriority, attackerPriority, medicPriority;
	
	private int stamina;
	
	/**
	 * PlayerRecord constructor
	 * @param c - city
	 * @param s - street
	 * @param a - avenue
	 * @param d - direction
	 */
	public Medic (City c, int s, int a, Direction d) {
		super(c, s, a, d, 3, "Medic", false);
//		this.setColor(new Color(133, 248, 108));
		this.setColor(Color.GREEN);
	}
	
	/**
	 * Performs main action
	 */
	@Override
	public void performAction(PlayerRecord[] players) {
		this.stamina = this.obtainSpeed(); // Resets number of turns allowed
		
		this.runnerPriority = this.getTypeArray("Runner", players);
		this.attackerPriority = this.getTypeArray("Attacker", players);
		this.medicPriority = this.getTypeArray("Medic", players);
		
		// Only moves if is defeated
		if (!this.isDefeated()) {
			this.sortPriority(players);
		}
		
		this.prevPlayers = players;
	}
	
	/**
	 * Sorts priorities
	 * @param - players that need to be sorted
	 */
	private void sortPriority(PlayerRecord[] players) {
		players = this.getUpdatedSpeeds(players);
		
		int[] attackerPredicted = this.sortAttackerPriorities();
		this.getEscapeMovements(this.attackerPriority, null);
	}
	
	private AmbiguousMovement[] getEscapeMovements(PlayerRecord[] attackers, Direction[] barredDirections) {
		
		int[] predicted = this.sortAttackerPriorities(attackers);
		int immanentNum = 0;
		
		// Gets predictedFuture values copied from predicted
		for(int i = 0; i < attackers.length; i++) {
			if (predicted[i] <= 0) {
				immanentNum++;
			}
		}
		
		// Escapes if it is threatened by immanent tagging
		if (immanentNum > 0) {
			// Advisory variables
			ArrayList<AmbiguousMovement> movements = new ArrayList<AmbiguousMovement>(); // All movements needed to escape
			Direction[] possibleEscapes = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
			
			// Records and status
			int[] immanentProximities = new int[immanentNum];
			PlayerRecord[] immanentThreats = new PlayerRecord[immanentNum];
			
			int counter = 0;
			// Puts everything into an array
			for(int i = 0; i < attackers.length; i++) {
				// Only if threat is immanent (enough to tag player)
				if (predicted[i] <= 0) {
					immanentProximities[counter] = predicted[i];
					immanentThreats[counter] = attackers[i];
				}
			}
			
			// MAIN LOOP OF MOVEMENT DETERMINATION
			// TODO movement determination
			for (int i = 0; i < immanentThreats.length; i++) {
				this.getPossibleEscapes((immanentThreats[i]));
			}
			
		}
		
		return null; // TODO return statement stub
	}
	
	private AmbiguousMovement getPossibleEscapes(PlayerRecord attacker) {
		AmbiguousMovement maneuver = new AmbiguousMovement(0, 0, 0);
		
		int streetDifference = this.getStreet() - attacker.getStreet();
		int avenueDifference = this.getAvenue() - attacker.getAvenue();
		
		// GETTING NEXT DIRECTION
		Direction horizontal;
		
		// Determines generic nature of next vertical movement
		if (streetDifference < 0) {
			maneuver.setDirectionV(1);
		} else if (streetDifference > 0) {
			maneuver.setDirectionV(2);
		} else {
			maneuver.setDirectionV(3);
		}
		
		// Determines generic nature of next horizontal movement
		if (avenueDifference < 0) {
			maneuver.setDirectionH(2);
		} else if (avenueDifference > 0) {
			maneuver.setDirectionH(1);
		} else {
			maneuver.setDirectionH(3);
		}
		
		return null; // TODO return stub
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
	private int[] sortAttackerPriorities() {
		return this.sortAttackerPriorities(this.attackerPriority);
	}
	
	/**
	 * Gets priority values of all for all attackers
	 * @param attackers - array of all attackers
	 */
	private int[] sortAttackerPriorities(PlayerRecord[] attackers) {
		int[] predictedProximity = this.getPredictedAttackerProximities(attackers);
		
		// Evaluates all attackers and sorts according to immanence of threat
		for (int i = 0; i < attackers.length; i++) {
			for (int j = i; j > 0; j--) {
				if (predictedProximity[j] < predictedProximity[j-1]) {
					int temp = predictedProximity[j-1];
					predictedProximity[j-1] = predictedProximity[j];
					predictedProximity[j] = temp;
					
					PlayerRecord temp2 = attackers[j-1];
					attackers[j-1] = attackers[j];
					attackers[j] = temp2;
				}
			}
		}
		
		return predictedProximity;
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
				playerPriority[index] = players[i];
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
