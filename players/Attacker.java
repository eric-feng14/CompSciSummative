package players;
import java.util.*;
import becker.robots.*;
import java.awt.*;

/**
 * Template robot class for the final summative in ICS4U
 * TODO:
 * - implement the other states (e.g. fighting, resting, etc)
 * - work on powerups later
 * - add feature, after engaging in battle with a target, you cannot fight them again
 * - add strategies, e.g. cornering, supporting & coordinating with other attackers
 * - add randomness
 * - randomize the speed for each player
 * @author Eric Feng
 * @version Due date: June 13 2025
 */
public class Attacker extends Player{
	
	//learnedAttributes contains information gathered from watching others as well as fighting others
	private AttackerRecord[] learnedAttributes;
	private PlayerRecord[] attackers, priorityList, previousPriorityList;
	private int roundsSpentChasing = 0, currentState = STATE_CHASE;
	private final static int MAX_CHASE_TIME = 5;
	private final static int STATE_CHASE = 1, STATE_FIGHT = 2, STATE_REST = 3;
	private final static int MISS = 0, NORMAL_HIT = 20, CRITICAL_HIT = 40, KNOCKOUT = 100;
	

	public Attacker(City city, int s, int a, Direction d) {
		super(city, s, a, d, 3, "Attacker", true);
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
		System.out.println("\nCurrent target of player" + this.getPLAYER_ID());
		System.out.format("type: %s, street: %d, avenue: %d, speed: %d\n", ct.getTYPE(), ct.getStreet(), ct.getAvenue(), ct.getSpeed());
	}

	@Override 
	public void performAction(PlayerRecord[] players) { 
		this.updateInfo(players);
		if (this.getCurrentTarget() == null) { //no target, e.g. first round of play
			this.setCurrentTarget(newTarget(players));
		}
		printPriorityList();
		printAttackers();
		printCurrentTarget();
		switch(this.currentState) { //fighting state is controlled between the application class
			case STATE_CHASE: //chasing state -> could have multiple strategies in this case: maybe another switch
				this.chase(players);
				break;
			case STATE_REST: //resting state
				this.rest();
				break;
		}
	}
	
	public void chase(PlayerRecord[] players) {
		//If there is currently no target, find a new target
		if (this.roundsSpentChasing == Attacker.MAX_CHASE_TIME) {
			this.setCurrentTarget(newTarget(players));
			this.roundsSpentChasing = 0;
		}
		
		this.chaseTarget();
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
	
	public void fight() {//assuming we are at the same position as our target
		//would we have to send information back to the application class?
	}
	
	public PlayerRecord[] getInfo() {
		if (this.currentState == STATE_FIGHT) {
			return new PlayerRecord[] {new PlayerRecord(this), this.getCurrentTarget()};
		}
		return null;
	}
	
	public void rest() {}
	
	/**
	 * chases the target with a set amount of steps
	 */
	private void chaseTarget() {
		//Safety check
		if (this.getCurrentTarget() == null) {
			System.out.println("No current target! Null target!");
			return;
		}
		
		int verticalDiff = this.getStreet() - this.getCurrentTarget().getStreet();
		int horizontalDiff = this.getAvenue() - this.getCurrentTarget().getAvenue();
		int speed = this.obtainSpeed();

		if (verticalDiff != 0) {
			int verticalSteps = Math.min(Math.abs(verticalDiff), speed);
			//use of ternary operator to make code more readable -> (condition) ? (true assignment) : (false assignment)
			Direction dir = (verticalDiff > 0) ? Direction.NORTH : Direction.SOUTH;
			this.directedMove(dir, verticalSteps);
			speed -= verticalSteps; //solves a lot of problems. e.g. if verticalSteps was the full speed, there would be no more horizontal movements
		}
		
		if (horizontalDiff != 0) {
			int horizontalSteps = Math.min(Math.abs(horizontalDiff), speed);
			Direction dir  = (horizontalDiff > 0) ? Direction.WEST : Direction.EAST;
			this.directedMove(dir, horizontalSteps);
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
			if (this.frontIsClear()) {
				this.move();
			} else {
				System.out.println("Attacker " + this.getPLAYER_ID() + "cannot move!");
				return;
			}
		}
	}
	
	private boolean noTargets() {
		for (PlayerRecord attacker : this.attackers) {
			if (attacker != null) { //an attacker is already chasing someone, targets already existed
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns a PlayerRecord representing the current players target. Note that a target will always be returned.
	 * @return returns a PlayerRecord representing the target of the current attacker
	 */
	private PlayerRecord newTarget(PlayerRecord[] players) {
		//Edge case: first robot gets a target
		if (noTargets()) {
			return this.priorityList[0];
		}
		
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
		this.learnedAttributes = new AttackerRecord[attackers.length];
		
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
	private void updateInfo(PlayerRecord[] players) {
		this.updateListsAndTarget(players);
		this.sortPriorityList();
		this.updatePreviousPriority();
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
}