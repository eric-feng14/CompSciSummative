package players;
import java.util.ArrayList;
import java.awt.Color;
import playerMods.*;
import becker.robots.*;

/**
 * The Medic class: Healing Runners
 * @author Richard
 * @version 6/9/2025
 */
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
		super(c, s, a, d, 4, "Medic", false);
//		this.setColor(new Color(133, 248, 108));
		this.setColor(Color.GREEN);
	}
	
	/**
	 * Performs main action
	 */
	@Override
	public void performAction(PlayerRecord[] players) {
		this.stamina = this.obtainSpeed(); // Resets number of turns allowed
		
		players = this.getUpdatedSpeeds(players);
		this.runnerPriority = this.getTypeArray("Runner", players);
		this.attackerPriority = this.getTypeArray("Attacker", players);
		this.medicPriority = this.getTypeArray("Medic", players);
		
		// Only moves if is defeated
		if (!this.isDefeated()) {
			
			Movement nextMovement = this.getEscapeMovement(this.attackerPriority);
			this.escapeMove(nextMovement);
		}
		
		this.prevPlayers = players;
	}
	
	/**
	 * Moves the movement
	 * @param m - movement
	 */
	private void escapeMove(Movement m) {
		int distanceCovered = 0;
		// Caps movement at stamina
		if (m.getDistance() > this.stamina)
			m.setDistance(this.stamina);
		this.turnTo(m.getDirection());
		// Moves if has stamina
		while (distanceCovered < m.getDistance() && this.frontIsClear() && this.stamina > 0) {
			this.move();
			this.stamina--;
			distanceCovered++;
		}
		// Moves more if it hits wall
		if (distanceCovered < m.getDistance()) {
			this.escapeMove(this.getEscapeMovement(this.attackerPriority, new Direction[] {this.getDirection()}));
		}
	}
	
	/**
	 * Gets one escape movement
	 * @param attackers - attackers
	 * @param barredDirections - directions that cannot be run
	 * @return - the movement to move
	 */
	private Movement getEscapeMovement(PlayerRecord[] attackers, Direction[] barredDirections) {
		Movement movement = new Movement(Direction.EAST, 0);
		
		int[] predicted = this.sortAttackerPriorities(attackers);
		int imminentNum = 0;
		
		// Gets predictedFuture values copied from predicted
		for(int i = 0; i < attackers.length; i++) {
			if (predicted[i] <= 0) {
				imminentNum++;
			}
		}
		
		// Escapes if it is threatened by imminent tagging
		if (imminentNum > 0) {
			// Advisory variables
			Direction[] possibleEscapes = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
			// Checks if there are barred directions
			if (barredDirections != null) {
				// Picks out all barred directions
				for (int i = 0; i < possibleEscapes.length; i++) {
					// Checks for the barred directions
					for (int j = 0; j < barredDirections.length; j++) {
						// If the barred directions exist
						if (possibleEscapes[i] == barredDirections[j])
							possibleEscapes[i] = null;
					}
				}
			}
			
			// Records and status
			int[] imminentProximities = new int[imminentNum];
			PlayerRecord[] imminentThreats = new PlayerRecord[imminentNum];
			
			int counter = 0;
			// Puts everything into an array
			for(int i = 0; i < attackers.length; i++) {
				// Only if threat is imminent (enough to tag player)
				if (predicted[i] <= 0) {
					imminentProximities[counter] = predicted[i];
					imminentThreats[counter] = attackers[i];
				}
			}
			
			// MAIN LOOP OF MOVEMENT DETERMINATION
			
			int runDistance = 0;
//			int escapeDirH = 3;
//			int escapeDirV = 3;
//			for (int i = 0; i < imminentThreats.length; i++) {
//				AmbiguousMovement escape = this.getPossibleEscapes(imminentThreats[i], imminentProximities[i]);
//				if (escape.getDistance() > runDistance) {
//					runDistance = escape.getDistance();
//				}
//				if (escape.getDirectionH() < 3 && escape.getDirectionH() != escapeDirH && escapeDirH != 0) {
//					escapeDirH = escape.getDirectionH();
//				}
//			}
			for (int i = 0; i < imminentThreats.length; i++) {
				AmbiguousMovement escape = this.getPossibleEscapes(imminentThreats[i], imminentProximities[i]);
				// Removes impossible horizontal directions 
				switch(escape.getDirectionH()) {
				case 1:
					possibleEscapes[3] = null;
					break;
				case 2:
					possibleEscapes[1] = null;
					break;
				default:
				}
				
				// Removes impossible vertical directions
				switch(escape.getDirectionV()) {
				case 1:
					possibleEscapes[2] = null;
					break;
				case 2:
					possibleEscapes[0] = null;
					break;
				default:
				}
				
				// Gets the final run distance
				if (escape.getDistance() > runDistance) {
					runDistance = escape.getDistance();
				}
			}
			
			Direction[] possibleDirections = this.removeNulls(possibleEscapes);
			
			// Moves towards direction options
			if (possibleDirections.length != 0) {
				int randomDirection = (int) (Math.random() * possibleDirections.length);
				movement = new Movement(possibleDirections[randomDirection], runDistance);
			} else {
				Direction direction;
				// Gets a random direction
				switch((int)(Math.random()*4)) {
				case 0:
					direction = Direction.EAST;
					break;
				case 1:
					direction = Direction.NORTH;
					break;
				case 2:
					direction = Direction.WEST;
					break;
				default:
					direction = Direction.SOUTH;
					
				}
				movement = new Movement(direction, this.obtainSpeed());
			}
		}
		return movement;
	}
	
//	private AmbiguousMovement getHealMovement(PlayerRecord[] runners) {
//		int[] proximity = this.getProximityValues(runners);
//		
//	}
	
	private AmbiguousMovement getHealMovement(PlayerRecord[] runners, int s, int a) {
		int[] proximity = this.getProximityValues(runners, s, a);
		
	}
	
	/**
	 * Gets escape movement
	 * @param attackers - attackers
	 * @return - the next movement
	 */
	private Movement getEscapeMovement(PlayerRecord[] attackers) {
		return this.getEscapeMovement(attackers, null);
	}
	
	private AmbiguousMovement getPossibleEscapes(PlayerRecord attacker, int predDist) {
		AmbiguousMovement maneuver = new AmbiguousMovement(3, 3, 0);
		if (attacker == null) {
			return maneuver;
		}
		
		int streetDifference = this.getStreet() - attacker.getStreet();
		int avenueDifference = this.getAvenue() - attacker.getAvenue();
		
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
		maneuver.setDistance(-1*(predDist)+1);
		
		return maneuver;
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
				} else {
					updatedPlayers[i].setSpeed(this.prevPlayers[i].getSpeed());
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
		return this.getProximityValues(players, this.getStreet(), this.getAvenue());
	}
	
	/**
	 * Gets the relative distance values of players
	 * @param players - the list of specific player records
	 * @param s - street
	 * @param a - avenue
	 * @return - the proximity of each player
	 */
	private int[] getProximityValues(PlayerRecord[] players, int s, int a) {
		int[] proximity = new int[players.length];
		// Gets relative distance values of players
		for (int i = 0; i < players.length; i++) {
			proximity[i] = Math.abs(players[i].getAvenue() - a) + 
					Math.abs(players[i].getStreet() - s);
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
	
	private Direction[] removeNulls(Direction[] d) {
		int indexNum = 0;
		// gets array number
		for (int i = 0; i < d.length; i++) {
			if(d[i] != null)
				indexNum++;
		}
		Direction[] directions = new Direction[indexNum];
		// fills directions
		int counter = 0;
		for (int i = 0; i < d.length; i++) {
			// gets all not null
			if(d[i] != null) {
				directions[counter] = d[i];
				counter++;
			}
		}
		
		return directions;
	}
	
	/**
	 * Moves if can move
	 */
	@Override
	public void move() {
		// Moves if front is clear
		if(this.frontIsClear()) {
			super.move();
		}
	}
}
