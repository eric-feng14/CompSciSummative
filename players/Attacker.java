package players;
import java.util.*;

import app.Main;
import powerUps.*;
import unit_3_recurSort.sorting.Account;
import becker.robots.*;
import java.awt.*;

/**
 * Template robot class for the final summative in ICS4U
 * TODO:
 * - implement the other states (e.g. fighting, resting, etc)
 * - add feature, after engaging in battle with a target, you cannot fight them again
 * - work on strategies
 * - randomize the speed for each player
 * - add stamina to the base class
 * @author Eric Feng
 * @version Due date: June 13 2025
 */
public class Attacker extends Player{
	
	private PlayerRecord[] attackers, priorityList, previousPriorityList;
	private ArrayList<EnhancedThing> powerUps;
	private int roundsSpentChasing = 0, currentState = STATE_CHASE, currentStrat = STRAT_DEFAULT;
	private static boolean noPowerUps = false;
	private final static int MAX_CHASE_TIME = 10;
	private final static int STATE_CHASE = 1, STATE_FIGHT = 2, STATE_REST = 3;
	//no need for cornering since support state logic overlaps with it
	//default strategy is chase based on distance and speed, alternate strategy is based on learned defense
	private final static int STRAT_DEFAULT = 4, STRAT_ALTERNATE = 5, STRAT_FOCUS_POWERUP = 6, STRAT_SUPPORT = 7;

	public Attacker(City city, int s, int a, Direction d) {
		super(city, s, a, d, 3, "Attacker", false);
		this.setColor(Color.RED); //attackers are red
	}

	private void printPriorityList() {
		System.out.println("\nPriority list of attacker: " + this.getPLAYER_ID());
		for (PlayerRecord rec : this.priorityList) {
			System.out.format("type: %s, street: %d, avenue: %d\n", rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}
	
	private void printAttackers() {
		System.out.println("\nAttacker information for attacker" + this.getPLAYER_ID());
		for (PlayerRecord rec : attackers) {
			System.out.format("type: %s, street: %d, avenue %d\n", rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}
	
	private void printCurrentTarget() {
		PlayerRecord ct = this.getCurrentTarget();
		if (ct != null) {
			System.out.println("\nCurrent target of player" + this.getPLAYER_ID());
			System.out.format("type: %s, street: %d, avenue: %d, speed: %d\n", ct.getTYPE(), ct.getStreet(), ct.getAvenue(), ct.getSpeed());
		} else {
			System.out.println("No current target");
		}
	}

	@Override 
	public void performAction(PlayerRecord[] players, ArrayList<EnhancedThing> powerUps) { 
		this.updateInfo(players, powerUps);
		
		//Debugging statements
//		printPriorityList();
//		printAttackers();
//		printCurrentTarget();
		switch(this.currentState) { //fighting state is controlled between the application class
			case STATE_CHASE: 
				switch(this.currentStrat) {
					case STRAT_DEFAULT:
						this.chaseTarget(players);
					case STRAT_ALTERNATE: 
						
					case STRAT_SUPPORT: //activate this case if there is at least 2 attackers
						
					case STRAT_FOCUS_POWERUP: //activate this case if there are powerups on the field (Condition)
						this.chasePowerUp();
				}
				break;
			case STATE_REST: //resting state
				this.rest();
				break;
		}
	}
	
	public void chasePowerUp() {
		EnhancedThing targetPowerUp = this.powerUps.get(0);
		//Safety check
		if (targetPowerUp == null) {
			System.out.println("No power ups available");
			return;
		}
		
		int verticalDiff = this.getStreet() - targetPowerUp.getStreet();
		int horizontalDiff = this.getAvenue() - targetPowerUp.getAvenue();
		this.chase(verticalDiff, horizontalDiff);
		if (this.canPickThing()) {
			this.pickPowerUp(targetPowerUp);
		}
	}
	
	public void chaseTarget(PlayerRecord[] players) {
		//If there is currently no target, find a new target
		if (this.roundsSpentChasing == Attacker.MAX_CHASE_TIME) {
			this.setCurrentTarget(newTarget(players));
			this.roundsSpentChasing = 0;
		}
		
		//Safety check
		if (this.getCurrentTarget() == null) {
			System.out.println("No current target! Null target!");
			return;
		}
		
		int verticalDiff = this.getStreet() - this.getCurrentTarget().getStreet();
		int horizontalDiff = this.getAvenue() - this.getCurrentTarget().getAvenue();
		this.chase(verticalDiff, horizontalDiff);
		this.roundsSpentChasing++;
		if (this.targetReached()) {
			this.currentState = STATE_FIGHT; //change to fighting state
		}
	}
	
	private boolean targetReached() {
		if (this.getStreet() == this.getCurrentTarget().getStreet() && this.getAvenue() == this.getCurrentTarget().getAvenue()) {
			System.out.println("Target Reached!");
			return true;
		}
		return false;
	}
	
	public void sendSignal() {
		if (this.currentState == STATE_FIGHT) {
			Main.signal("attack", this.getPLAYER_ID(), this.getCurrentTarget().getPLAYER_ID());
		}
	}
	
	public void sendInfo(int damageDealt, int victimID) {
		for (PlayerRecord rec : this.priorityList) {
			if (rec.getPLAYER_ID() == victimID) {
				//simple multiplication formula to estimate the defense of the player
				//it assumes that more damage means less defense, and vice versa
				rec.setDefense(this.getStrength() - damageDealt);
			}
		}
	}
	
	public void pickPowerUp(EnhancedThing powerup) {
		powerup.applyTo(this);
		this.pickThing();
	}
	
	public void rest() {
		int dist;
		if (this.currentStrat == STRAT_DEFAULT || this.currentStrat == STRAT_ALTERNATE) {
			dist = calcDistance(this.getCurrentTarget());
		} else if (this.currentStrat == STRAT_FOCUS_POWERUP){
			dist = calcDistance(this.powerUps.get(0)); 
		} else {
			dist = 0;
		}
		//rest for a specific number of rounds (necessary to reach the target)
		this.setStamina(this.getStamina() + this.obtainSpeed());
		if (this.getStamina() >= dist) {
			this.currentState = STATE_CHASE;
		}

	}
	
	/**
	 * chases the target with a set amount of steps
	 */
	private void chase(int verticalDiff, int horizontalDiff) {
		int speed = this.obtainSpeed();

		if (verticalDiff != 0 && this.getStamina() > 0) {
			int verticalSteps = Math.min(Math.abs(verticalDiff), speed);
			//use of ternary operator to make code more readable -> (condition) ? (true assignment) : (false assignment)
			Direction dir = (verticalDiff > 0) ? Direction.NORTH : Direction.SOUTH;
			this.directedMove(dir, verticalSteps);
			speed -= verticalSteps; //solves a lot of problems. e.g. if verticalSteps was the full speed, there would be no more horizontal movements
		}
		
		if (horizontalDiff != 0 && this.getStamina() > 0) {
			int horizontalSteps = Math.min(Math.abs(horizontalDiff), speed);
			Direction dir  = (horizontalDiff > 0) ? Direction.WEST : Direction.EAST;
			this.directedMove(dir, horizontalSteps);
		}
		
		if (this.getStamina() <= 0) {
			this.currentState = STATE_REST;
		}
	}
	
	/**
	 * Helper function for chasing another robot
	 * @param dir direction is the direction to turn to
	 * @param steps steps is how far the robot should go
	 */
	private void directedMove(Direction dir, int steps) {
		this.turnTo(dir);
		//Safe moving -> prevent walking into a wall
		for (int i = 0; i < steps; i++) {
			if (this.frontIsClear() && this.getStamina() > 0) {
				this.move();
				this.setStamina(this.getStamina()-1);
			} else if (this.getStamina() <= 0) {
				System.out.println("Attacker " + this.getPLAYER_ID() + " must rest!");
				this.currentState = STATE_REST;
				return;
			} else {
				System.out.println("Attacker " + this.getPLAYER_ID() + "cannot move!");
				return;
			}
		}
	}
	
	/**
	 * Returns a PlayerRecord representing the current players target. Note that a target will always be returned.
	 * @return returns a PlayerRecord representing the target of the current attacker
	 */
	private PlayerRecord newTarget(PlayerRecord[] players) {
		//priority list and attackers are already updated
		for (PlayerRecord record : this.priorityList) {
			//Check whether the other attackers are already searching for "record". If not, it's a valid target. 
			//Note that "attackers" contains the current target as well. This is intended.
			boolean found = false;
			for (PlayerRecord attacker : this.attackers) {
				PlayerRecord target = attacker.getCurrentTarget();
				if (target != null && target.getPLAYER_ID() == record.getPLAYER_ID()) { 
					found = true;
					break;
				}
			}
			
			if (!found) {
				return record;
			}
		}
		
		int idx;
		//everyone is already being chased -> return a random target
		do {
			idx = generator.nextInt(this.priorityList.length);
		} while (idx == this.getCurrentTarget().getPLAYER_ID() && this.priorityList.length > 1);
		return this.priorityList[idx];
	}
	
	/**
	 * Initial instruction when the game commences -> sets up all of the attackers with their required information
	 */
	@Override
	public void initialize(PlayerRecord[] players) {
		int size = 0;
		for (PlayerRecord rec : players) {
			if (!rec.getTYPE().equals("Attacker")) {
				size++;
			}
		}
//		//Edge case -> no other players other than attackers on the field
		if (size == 0) {
			System.out.println("No other players!");
			System.exit(0);
		}
		
		this.priorityList = new PlayerRecord[size]; //Assign the priorityList a specific size
		this.previousPriorityList = new PlayerRecord[size];
		this.attackers = new PlayerRecord[players.length - size];
		
		//Populate previousPriorityList with default player records
		int idx = 0;
		for (PlayerRecord rec : players) {
			if (! rec.getTYPE().equals("Attacker")) {
				this.previousPriorityList[idx] = rec;
				idx++;
			}
		}
		
		//Extra note: priorityList and attackers will be updated upon the first call of performAction()
		//Learned attributes will be filled up as interactions between various robots start to happen
	}
	
	/**
	 * Main update method for gathering the latest information from the application class
	 * @param players players is the PlayerRecord array with the newest information
	 */
	private void updateInfo(PlayerRecord[] players, ArrayList<EnhancedThing> powerUps) {
		this.updateListsAndTarget(players);
		this.sortPriorityList();
		this.powerUps = powerUps;
		this.sortPowerUps(powerUps);
		//Update the previous priority list
		this.previousPriorityList = this.priorityList;
		
		//Safety checks
		if (this.getCurrentTarget() == null) { //no target, e.g. first round of play
			this.setCurrentTarget(newTarget(players));
		}
		
		if (powerUps.size() == 0) {
			noPowerUps = true;
		}
	}
	
	private void sortPowerUps(ArrayList<EnhancedThing> powerUps) {
		//Insertion sort
		for (int i = 1; i < powerUps.size(); i++) {
			EnhancedThing currentPowerUp = powerUps.get(i);
			int j = i;
			
			//Continue shifting elements until the desired position is found 
			while (j > 0 && calcDistance(currentPowerUp) < calcDistance(powerUps.get(j-1))) {
				powerUps.set(j, powerUps.get(j-1));
				j--;
			}
			powerUps.set(j, currentPowerUp);
		}
	}
	
	private void updatePreviousPriority() {
		for (int i = 0; i < this.priorityList.length; i++) {
			this.previousPriorityList[i] = this.priorityList[i];
		}
	}
	
	private void updateCurrentTarget(PlayerRecord rec) {
		if (this.getCurrentTarget() != null && rec.getPLAYER_ID() == this.getCurrentTarget().getPLAYER_ID()) {
			this.setCurrentTarget(rec);
		}
	}
	
	/**
	 * Updates the current robot's priority list with the latest information from "players". also updates the current target information
	 * @param players players is the PlayerRecord array with the newest and latest information from the game
	 */
	private void updateListsAndTarget(PlayerRecord[] players) {
		int idx1 = 0, idx2 = 0;
		for (PlayerRecord record : players) {
			if (! record.getTYPE().equals("Attacker")) {
				this.priorityList[idx1] = record;
				this.learnSpeed(idx1);
				idx1++;
				//Update current target as well when new information is passed
				this.updateCurrentTarget(record);
			} else { //then it is an attacker
				this.attackers[idx2] = record;
				idx2++;
			}
		}
	}
	
	private PlayerRecord findRecord(int idx) {
		for (PlayerRecord record : this.previousPriorityList) {
			if (record.getPLAYER_ID() == this.priorityList[idx].getPLAYER_ID()) {
				return record;
			}
		}
		return null;
	}
	
	private void learnSpeed(int idx) {
		//We need to find the previous record because the order of "previousPriorityList" constantly changes,
		//while the players array from the application class never changes order
		PlayerRecord prevRecord = findRecord(idx), currentRecord = this.priorityList[idx];
		if (prevRecord == null) {
			System.out.println("Didn't find the previous record!");
			System.exit(0);
		}
		int speed = calcDistance(prevRecord, currentRecord);
		//We're looking at the maximum possible speed of other players, not the current speed, so we can get a better understanding of their abilities
		int newSpeed = Math.max(speed, this.previousPriorityList[idx].getSpeed());
		this.priorityList[idx].setSpeed(newSpeed);
	}
	
	/**
	 * Selection sort algorithm that sorts the players in the current robot's priority list by distance and maximum speed
	 */
	private void sortPriorityList() {
		int len = this.priorityList.length;
		for (int i = 0; i < this.priorityList.length - 1; i++) {
			for (int j = i + 1; j < this.priorityList.length; j++) {
				int dist1 = calcDistance(this.priorityList[j]), dist2 = calcDistance(this.priorityList[i]);
				int speed1 = this.priorityList[j].getSpeed(), speed2 = this.priorityList[i].getSpeed();
				int priority1 = dist1 * speed1, priority2 = dist2 * speed2;
				if (priority1 < priority2) {
					swapPlayerRecord(i, j, this.priorityList);
				}
			}
		}
	}
	
	/**
	 * Helper function for sorting the priority list
	 * @param idx1 idx1 is the index of the first PlayerRecord
	 * @param idx2 idx2 is the index of the second PlayerRecord
	 * @param lst lst is the player record array containing the items to be swapped
	 */
	private void swapPlayerRecord(int idx1, int idx2, PlayerRecord[] lst) {
		PlayerRecord temp = lst[idx1];
		lst[idx1] = lst[idx2];
		lst[idx2] = temp;
	}
	
	/**
	 * Calculates the distance between the current robot and the "rec" robot
	 * @param rec rec is the PlayerRecord of a robot in the current robot's priority list
	 * @return returns an integer representing their difference in distance
	 */
	private int calcDistance(PlayerRecord rec) {
		return Math.abs(rec.getAvenue() - this.getAvenue()) + Math.abs(rec.getStreet() - this.getStreet());
	}
	
	/**
	 * Overloaded calcDistance method that calculates the distance between two playerRecords rather than a single record vs the current robot
	 * @param rec1 rec1 is the first playerRecord
	 * @param rec2 rec2 is the second playerRecord
	 * @return
	 */
	private int calcDistance(PlayerRecord rec1, PlayerRecord rec2) {
		return Math.abs(rec1.getAvenue() - rec2.getAvenue()) + Math.abs(rec1.getStreet() - rec2.getStreet());
	}
	
	/**
	 * Overloaded calcDistance method that calculates the distance between the current robot and a "powerUp"
	 * @param powerUp powerUp is an enhancedThing representing a specific type of power up
	 * @return returns an integer representing distance
	 */
	private int calcDistance(EnhancedThing powerUp) {
		return Math.abs(this.getAvenue() - powerUp.getAvenue()) + Math.abs(this.getStreet() - powerUp.getStreet());
	}
}