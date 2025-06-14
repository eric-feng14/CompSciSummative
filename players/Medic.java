package players;
import java.util.ArrayList;

import app.Main;

import java.awt.Color;
import playerMods.*;
import becker.robots.*;
import powerUps.*;

/**
 * The Medic class: Healing Runners
 * @author Richard
 * @version 6/9/2025
 */
public class Medic extends Player{
	private static final int BASE_HEAL = 20, BLESSED_HEAL = 40, DIVINE_HEAL = 60, HOLY_RESSURECTION = 100;
	private PlayerRecord[] prevPlayers;
	private PlayerRecord[] runnerPriority, attackerPriority, medicPriority;
	
	private int steps;
	
	/**
	 * PlayerRecord constructor
	 * @param c - city
	 * @param s - street
	 * @param a - avenue
	 * @param d - direction
	 */
	public Medic (City c, int s, int a, Direction d) {
		super(c, s, a, d, 6, "Medic", false);
		this.setColor(Color.GREEN);
	}
	
	/**
	 * Performs main action
	 */
	@Override
	public void performAction(PlayerRecord[] players, ArrayList<EnhancedThing> powerups) {
		this.steps = this.obtainSpeed(); // Resets number of turns allowed
		
		players = this.getUpdatedSpeeds(players);
		this.runnerPriority = this.getTypeArray("Runner", players);
		this.attackerPriority = this.getTypeArray("Attacker", players);
		this.medicPriority = this.getTypeArray("Medic", players);
		
		// Only moves if is defeated
		if (!this.isDefeated()) {
			
			Movement nextMovement = this.getEscapeMovement(this.attackerPriority);
			this.escapeMove(nextMovement);
			if (this.runnerPriority.length != 0 && this.runnerPriority != null)
				this.moveTo(this.runnerPriority[0].getStreet(), this.runnerPriority[0].getAvenue());
		}
		
		this.prevPlayers = players;
	}
	
	/**
	 * Moves the movement
	 * @param m - movement
	 */
	private void escapeMove(Movement m) {
		int distanceCovered = 0;
		// Caps movement at steps
		if (m.getDistance() > this.steps)
			m.setDistance(this.steps);
		this.turnTo(m.getDirection());
		// Moves if has steps
		while (distanceCovered < m.getDistance() && this.frontIsClear() && this.steps > 0) {
			this.move();
			this.steps--;
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
			
			AmbiguousMovement[] escapes = new AmbiguousMovement[imminentThreats.length];
			for (int i = 0; i < imminentThreats.length; i++) {
				escapes[i] = this.getPossibleEscapes(imminentThreats[i], imminentProximities[i]);
				// Gets the final run distance
				if (escapes[i].getDistance() > runDistance) {
					runDistance = escapes[i].getDistance();
				}
			}
			Direction[] possibleDirections = this.getDirectionsFromAmbiguous(escapes);
			if (barredDirections != null) {
				// Picks out all barred directions
				for (int i = 0; i < possibleDirections.length; i++) {
					// Checks for the barred directions
					for (int j = 0; j < barredDirections.length; j++) {
						// If the barred directions exist
						if (possibleDirections[i] == barredDirections[j])
							possibleDirections[i] = null;
					}
				}
			}
			
			Direction[] optimalDirections = new Direction[0];
			
			ArrayList<AmbiguousMovement> healPaths = this.getHealMovements();
			
			// Gets the difference of heal paths and possible directions of escape
			for (int i = 0; i < healPaths.size(); i++) {
				Direction[] healDir = this.getDirectionsFromAmbiguous(new AmbiguousMovement[]{healPaths.get(i)});
				Direction[] tempDir = new Direction[]{null, null, null, null};
				// Fills tempDir
//				for (int j = 0; j < tempDir.length; j++) {
//					tempDir[i] = possibleDirections[i];
//				}
				// Fills optimal
				for (int j = 0; j < healDir.length; j++) {
					// Fills individual
					for (int k = 0; k < possibleDirections.length; i++) {
						if (healDir[j] == possibleDirections[k]) {
							// Determines the directions present
							switch(healDir[j]) {
							case NORTH:
								tempDir[0] = Direction.NORTH;
								break;
							case EAST:
								tempDir[1] = Direction.EAST;
								break;
							case SOUTH:
								tempDir[2] = Direction.SOUTH;
								break;
							case WEST:
								tempDir[3] = Direction.WEST;
							default:
								
							}
						}
					}
				}
				tempDir = this.removeNulls(tempDir);
				if (tempDir.length > 0) {
					optimalDirections = tempDir;
					break;
				}
			}
			
			if (optimalDirections.length != 0) {
				int randomDirection = (int) (Math.random() * possibleDirections.length);
				movement = new Movement(possibleDirections[randomDirection], runDistance);
			} else {
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
		}
		return movement;
	}
	
//	private AmbiguousMovement getHealMovement(PlayerRecord[] runners) {
//		int[] proximity = this.getProximityValues(runners);
//		
//	}
	
	/**
	 * 
	 * @param runners
	 * @param s
	 * @param a
	 * @return
	 */
	private ArrayList<AmbiguousMovement> getHealMovements() {
		int[] proximity = this.getProximityValues(this.runnerPriority, this.getStreet(), this.getAvenue());
		
		// Sorts proximity and runners using insertion
		for (int i = 0; i < this.runnerPriority.length; i++) {
			// Swaps until sorted
			for (int j = i; j > 0; j--) {
				// Swaps if not sorted
				if (proximity[j] < proximity[j-1]) {
					int proxTemp = proximity[j];
					PlayerRecord runnerTemp = this.runnerPriority[j];
					
					proximity[j] = proximity[j-1];
					proximity[j-1] = proxTemp;
					
					this.runnerPriority[j] = this.runnerPriority[j-1];
					this.runnerPriority[j-1] = runnerTemp;
				}
			}
		}
		
		ArrayList<AmbiguousMovement> patients = new ArrayList<AmbiguousMovement>();
		// Gets all possible runner targets
		for (int i = 0; i < (int)(runnerPriority.length); i++) {
			// Adds all hurt runners
			if (this.runnerPriority[i].getHP() < 100) {
				patients.add(this.getAmbiguousHealPlan(this.runnerPriority[i], proximity[i]));
			}
		}
		
		return patients;
	}
	
	private AmbiguousMovement getAmbiguousHealPlan(PlayerRecord patient, int predDist) {
		AmbiguousMovement maneuver = new AmbiguousMovement(3, 3, 0);
		if (patient == null) {
			return maneuver;
		}
		
		int streetDifference = this.getStreet() - patient.getStreet();
		int avenueDifference = this.getAvenue() - patient.getAvenue();
		
		// Determines generic nature of next vertical movement
		if (streetDifference < 0) {
			maneuver.setDirectionV(2);
		} else if (streetDifference > 0) {
			maneuver.setDirectionV(1);
		} else {
			maneuver.setDirectionV(0);
		}
		
		// Determines generic nature of next horizontal movement
		if (avenueDifference < 0) {
			maneuver.setDirectionH(1);
		} else if (avenueDifference > 0) {
			maneuver.setDirectionH(2);
		} else {
			maneuver.setDirectionH(0);
		}
		maneuver.setDistance(predDist);
		return maneuver;
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
				if (type == "Attacker" || !players[i].isDefeated()) {
					playerPriority[index] = players[i];
					index++;
				}
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
		if(this.frontIsClear() && this.steps > 0) {
			super.move();
		}
		this.sendSignal();
	}
	
	/**
	 * Gets one ambiguous movement from a collection
	 * @param movements - collection of ambiguous movements
	 * @return - one ultimate ambiguous movement
	 */
	private Direction[] getDirectionsFromAmbiguous(AmbiguousMovement[] movements) {
		Direction[] possibleDirections = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
		for (int i = 0; i < movements.length; i++) {
			// Removes impossible horizontal directions 
			switch(movements[i].getDirectionH()) {
			case 1:
				possibleDirections[3] = null;
				break;
			case 0:
				possibleDirections[3] = null;
			case 2:
				possibleDirections[1] = null;
				break;
			default:
			}
			
			// Removes impossible vertical directions
			switch(movements[i].getDirectionV()) {
			case 1:
				possibleDirections[2] = null;
				break;
			case 0:
				possibleDirections[2] = null;
			case 2:
				possibleDirections[0] = null;
				break;
			default:
			}
		}
		
		
		return possibleDirections = this.removeNulls(possibleDirections);
	}

	@Override
	public void pickPowerUp(EnhancedThing powerup) {
		powerup.applyTo(this);
		this.pickThing();
	}

	@Override
	public void sendSignal() {
		for (int i = 0; i < this.runnerPriority.length; i++) {
			PlayerRecord target = this.runnerPriority[i];
			if (target.getStreet() == this.getStreet() && target.getAvenue() == this.getAvenue()) {
				Main.signal("heal", this.getPLAYER_ID(), target.getPLAYER_ID());
			}
		}
	}
}
