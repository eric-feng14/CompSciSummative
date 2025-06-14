package players;
import java.util.*;
import app.Main;
import powerUps.*;
import becker.robots.*;
import java.awt.*;

/**
 * Template robot class for the final summative in ICS4U
 * TODO:
 * - add feature, after engaging in battle with a target, you cannot fight them again
 * @author Eric Feng
 * @version Due date: June 13 2025
 */
public class Attacker extends Player{
	
	private PlayerRecord[] attackers, priorityList, previousPriorityList;
	private ArrayList<EnhancedThing> powerUps;
	private int roundsSpentChasing = 0, currentState = STATE_CHASE, currentStrat = STRAT_DEFAULT;
	private boolean pickedPowerUp = false;
	private final static int MAX_CHASE_TIME = 10;
	private final static int STATE_CHASE = 1, STATE_FIGHT = 2, STATE_REST = 3;
	//no need for cornering since support state logic overlaps with it
	//default strategy is chase based on distance and speed, alternate strategy is based on learned defense
	private final static int STRAT_DEFAULT = 4, STRAT_ALTERNATE = 5, STRAT_FOCUS_POWERUP = 6, STRAT_SUPPORT = 7;

	/**
	 * Constructor method for creating an attacker
	 * @param city is the city of the robot's home
	 * @param s s is the street
	 * @param a a is the avenue
	 * @param d d is the initial direction it faces
	 */
	public Attacker(City city, int s, int a, Direction d) {
		super(city, s, a, d, 4, "Attacker", false);
		this.setColor(Color.RED); //attackers are red
	}

	/**
	 * prints out the information about the priority list
	 */
	private void printPriorityList() {
		System.out.println("\nPriority list of attacker: " + this.getPLAYER_ID());
		for (PlayerRecord rec : this.priorityList) {
			System.out.format("type: %s, street: %d, avenue: %d\n", rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}
	
	/**
	 * debug method that prints out info about the attackers
	 */
	private void printAttackers() {
		System.out.println("\nAttacker information for attacker" + this.getPLAYER_ID());
		for (PlayerRecord rec : attackers) {
			System.out.format("type: %s, street: %d, avenue %d\n", rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}
	
	/**
	 * debug method that prints info about current Target
	 */
	private void printCurrentTarget() {
		PlayerRecord ct = this.getCurrentTarget();
		if (ct != null) {
			System.out.println("\nCurrent target of player" + this.getPLAYER_ID());
			System.out.format("type: %s, street: %d, avenue: %d, speed: %d\n", ct.getTYPE(), ct.getStreet(), ct.getAvenue(), ct.getSpeed());
		} else {
			System.out.println("No current target");
		}
	}

	/**
	 * Main method containing logic with actions that the robot needs to perform
	 */
	@Override
	public void performAction(PlayerRecord[] players, ArrayList<EnhancedThing> powerUps) { 
		//update instance variables with latest info
		this.updateListsAndTarget(players);
		this.powerUps = powerUps;
		
		//if there's no target, find a new one
		if (this.getCurrentTarget() == null) { //no target, e.g. first round of play
			this.setCurrentTarget(newTarget(players));
		}
		
		//Switch strategies
		this.switchStrategies();
		
		//Debugging statements
//		printPriorityList();
//		printAttackers();
//		printCurrentTarget();
		
		//check which state the robot is in
		switch(this.currentState) { //fighting state is controlled between the application class
			case STATE_CHASE: 
				//check which strategy the robot is taking
				switch(this.currentStrat) {
					case STRAT_DEFAULT: //default strategy -> target robots based on distance and speed (learned over time)
						this.defaultSortPriorityList();
						this.chaseTarget(players, false);
						break;
					case STRAT_ALTERNATE: //alternate strategy -> targets robots based on fights (e.g. estimated defense)
						this.alternativeSortPriorityList();
						this.chaseTarget(players, false);
						break;
					case STRAT_SUPPORT: //activate this case if there is at least 2 attackers -> supports another attacker
						this.sortAttackersByDistance();
						this.setCurrentTarget(attackers[0].getCurrentTarget());
						this.chaseTarget(players, true);
						break;
					case STRAT_FOCUS_POWERUP: //activate this case if there are powerups on the field -> focuses on getting a powerup
						this.sortPowerUps(powerUps);
						this.chasePowerUp();
						break;
				}
				this.roundsSpentChasing++;
				break;
			case STATE_REST: //resting state
				this.rest();
				break;
		}
		
		this.previousPriorityList = this.priorityList;
	}
	
	/**
	 * switch strategies depending on how long the robot has been doing one strategy
	 */
	private void switchStrategies() {
		if (this.roundsSpentChasing == Attacker.MAX_CHASE_TIME) {
			ArrayList<Integer> choices = new ArrayList<Integer>();
			choices.add(STRAT_DEFAULT);
			choices.add(STRAT_ALTERNATE);
			//Add the suport strategy only if there are more than one attacker
			if (this.attackers.length >= 2) {
				choices.add(STRAT_SUPPORT);
			}
			//If there are powerups, add the focus power up strategy
			if (powerUps.size() > 0 ) {
				choices.add(STRAT_FOCUS_POWERUP);
			}
			//make a random selection
			int choice = generator.nextInt(choices.size());
			this.currentStrat = choices.get(choice);
			this.roundsSpentChasing = 0;
		}
	}
	
	/**
	 * chase method to go after a powerup
	 */
	public void chasePowerUp() {
		if (this.powerUps.size() == 0) {
			this.currentStrat = STRAT_DEFAULT;
		}
		EnhancedThing targetPowerUp = this.powerUps.get(0);
		//Safety check
		if (targetPowerUp == null) {
			System.out.println("No power ups available");
			return;
		}
		
		int verticalDiff = this.getStreet() - targetPowerUp.getStreet();
		int horizontalDiff = this.getAvenue() - targetPowerUp.getAvenue();
		this.chase(verticalDiff, horizontalDiff, false); //perform the actual movements
		//pick up the powerup -> specially handled
		if (this.canPickThing()) {
			this.pickPowerUp(targetPowerUp);
		}
	}
	
	/**
	 * chase the current Target
	 * @param players players is the players array
	 * @param reverse
	 */
	public void chaseTarget(PlayerRecord[] players, boolean reverse) {	
		//Safety check
		if (this.getCurrentTarget() == null) {
			System.out.println("No current target! Null target!");
			return;
		}
		
		int verticalDiff = this.getStreet() - this.getCurrentTarget().getStreet();
		int horizontalDiff = this.getAvenue() - this.getCurrentTarget().getAvenue();
		this.chase(verticalDiff, horizontalDiff, reverse);
		//if attacker is at the target
		if (this.targetReached()) {
			this.currentState = STATE_FIGHT; //change to fighting state
		}
	}
	
	/**
	 * Checks if the current attacker is at their target
	 * @return returns a boolean representing the function
	 */
	private boolean targetReached() {
		if (this.getStreet() == this.getCurrentTarget().getStreet() && this.getAvenue() == this.getCurrentTarget().getAvenue()) {
			System.out.println("Target Reached!");
			return true;
		}
		return false;
	}
	
	/**
	 * called from the application class to determine if the robot should fight or do anything else 
	 * can also return info back to application class
	 */
	@Override
	public void sendSignal() {
		if (this.currentState == STATE_FIGHT) {
			Main.signal("attack", this.getPLAYER_ID(), this.getCurrentTarget().getPLAYER_ID());
			this.currentState = STATE_CHASE;
		}
		if (this.pickedPowerUp) {
			Main.signal("remove", this.getPLAYER_ID(), this.powerUps.get(0).getID());
			this.pickedPowerUp = false;
		}
	}
	
	/**
	 * receives information from the application class about the fight
	 */
	public void sendInfo(int damageDealt, int victimID) {
		for (PlayerRecord rec : this.priorityList) {
			if (rec.getPLAYER_ID() == victimID) {
				//simple subtraction formula to estimate the defense of the player
				//it assumes that more damage means less defense, and vice versa
				rec.setDefense(this.getStrength() - damageDealt);
			}
		}
	}
	
	/**
	 * specialized method to pick the powerup with effect added
	 */
	@Override
	public void pickPowerUp(EnhancedThing powerup) {
		if (this.canPickThing()) {
			powerup.applyTo(this);
			this.pickThing();
			this.pickedPowerUp = true;
		}
	}
	
	/**
	 * robots method to rest and recover stamina
	 */
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
	 * universal chase method to move somewhere using maximum possible steps
	 * @param verticalDiff int representing the vertical difference
	 * @param horizontalDiff int representing the horizontal difference
	 * @param reversedOrder reversedOrder is a boolean determining whether to go horizontally first or not
	 */
	private void chase(int verticalDiff, int horizontalDiff, boolean reversedOrder) {
	    int speed = this.obtainSpeed();

	    // Movement directions and differences
	    int[] diffs = reversedOrder ? new int[]{horizontalDiff, verticalDiff} : new int[]{verticalDiff, horizontalDiff};
	    //Ternary operators are used here for conciseness: (type) (identifier) = (condition) ? (assignment true) : (assignment false)
	    Direction[] dirs = reversedOrder
	        ? new Direction[]{(horizontalDiff > 0) ? Direction.WEST : Direction.EAST,
	                          (verticalDiff > 0) ? Direction.NORTH : Direction.SOUTH}
	        : new Direction[]{(verticalDiff > 0) ? Direction.NORTH : Direction.SOUTH,
	                          (horizontalDiff > 0) ? Direction.WEST : Direction.EAST};

	    for (int i = 0; i < 2 && speed > 0 && this.getStamina() > 0; i++) {
	        int diff = Math.abs(diffs[i]);
	        if (diff != 0) {
	            int steps = Math.min(diff, speed);
	            this.directedMove(dirs[i], steps);
	            speed -= steps;
	        }
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
			
			//if no attacker is chasing "record"
			if (!found && !record.isDefeated()) {
				return record;
			}
		}
		
		//everyone is already being chased -> return a random target
		int idx = generator.nextInt(this.priorityList.length);
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
		//Edge case -> no other players other than attackers on the field
		if (size == 0) {
			System.out.println("No other players!");
			System.exit(0);
		}
		
		this.priorityList = new PlayerRecord[size]; //Assign the priorityList a specific size
		this.previousPriorityList = new PlayerRecord[size];
		this.attackers = new PlayerRecord[players.length - size];
		
		//Populate previousPriorityList with default player records to prevent exceptions in future calculations
		int idx = 0;
		for (PlayerRecord rec : players) {
			if (! rec.getTYPE().equals("Attacker")) {
				this.previousPriorityList[idx] = rec;
				idx++;
			}
		}
	}
	
	/**
	 * Sort the attackers array by their distance to the current robot
	 */
	private void sortAttackersByDistance() {
		int len = this.attackers.length;
		//Selection sort;
		for (int i = 0; i < len - 1; i++) {
			for (int j = i + 1; j < len; j++) {
				//calculate distances
				int dist1 = calcDistance(this.attackers[j]), dist2 = calcDistance(this.attackers[i]);
				if (dist1 < dist2) { //find the smallest distance in the rest of array
					swapPlayerRecord(i, j, this.attackers);
				}
			}
		}
	}
	
	/**
	 * sorts the powerups arraylist in terms of their distance to the current attacker
	 * @param powerUps powerUps is the arraylist containing all existing powerups
	 */
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
	
	/**
	 * update current target information for the current attacker
	 * @param rec rec is the new record with new information
	 */
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
	
	/**
	 * performs a linear search over previous priority list to find the record mathching the current record in priorityList
	 * @param idx idx is the index of the plyaer we want to learn about
	 * @return returns a playerrecord of representing the previous player record
	 */
	private PlayerRecord findRecord(int idx) {
		for (PlayerRecord record : this.previousPriorityList) {
			if (record.getPLAYER_ID() == this.priorityList[idx].getPLAYER_ID()) {
				return record;
			}
		}
		return null;
	}
	
	/**
	 * compare info from the previous priority list to the current and learn the max speed of that player
	 * @param idx idx is the index of the player who we're learning about (their speed)
	 */
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
	private void defaultSortPriorityList() {
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
	 * sorts based on defense attributes learned over time by fighting using a selection sort
	 */
	private void alternativeSortPriorityList() {
		int len = this.priorityList.length;
		for (int i = 0; i < this.priorityList.length - 1; i++) {
			for (int j = i + 1; j < this.priorityList.length; j++) {
				int defense1 = this.priorityList[j].getDefense(), defense2 = this.priorityList[i].getDefense();
				if (defense1 < defense2) { //find min defense
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
	 * @return returns an integer representing their difference in distance
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