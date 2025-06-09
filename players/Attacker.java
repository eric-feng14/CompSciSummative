package players;
import java.util.*;
import becker.robots.*;
import java.awt.*;

/**
 * Template robot class for the final summative in ICS4U
 * TODO:
 * - implement the other states (e.g. fighting, resting, etc)
 * - work on powerups later
 * - figure out how to make the robots learn (enhance the communicate() method). how to store in memory?
 * @author Eric Feng
 * @version Due date: June 13 2025
 */
public class Attacker extends Player{

	private PlayerRecord[] priorityList;
	private ArrayList<PlayerRecord> targets = new ArrayList<PlayerRecord>();
	private int roundsSpentChasing = 0, currentState = STATE_CHASE;
	private final static int MAX_CHASE_TIME = 5;
	private final static int STATE_CHASE = 1, STATE_FIGHT = 2, STATE_REST = 3;
	

	public Attacker(City city, int s, int a, Direction d) {
		super(city, s, a, d, 3, "Attacker", true);
		this.setColor(Color.RED); //attackers are red
	}

	@SuppressWarnings("unused")
	private void printPriorityList() {
		System.out.println("\nPriority list of attacker: " + this.getPLAYER_ID());
		for (PlayerRecord rec : this.priorityList) {
			System.out.format("type: %s, street: %d, avenue: %d\n", rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}
	
	private void printTargets() {
		System.out.println("\nTargets for attacker " + this.getPLAYER_ID());
		for (PlayerRecord rec : targets) {
			System.out.format("type: %s, street: %d, avenue %d\n", rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}
	
	private void printCurrentTarget() {
		PlayerRecord ct = this.getCurrentTarget();
		System.out.println("\nCurrent target of player" + this.getPLAYER_ID());
		System.out.format("type: %s, street: %d, avenue: %d\n", ct.getTYPE(), ct.getStreet(), ct.getAvenue());
	}

	@Override 
	public void performAction(PlayerRecord[] players) { 
		this.updateInfo(players);
		if (this.getCurrentTarget() == null) {
			this.setCurrentTarget(newTarget(players));
		}
		printPriorityList();
		printTargets();
		printCurrentTarget();
		switch(this.currentState) { 
			case STATE_CHASE: //chasing state -> could have multiple strategies in this case: maybe another switch
				this.chase(players);
				break;
			case STATE_FIGHT: //fighting state
				this.fight();
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
	
	/**
	 * Find out who the other attackers are going for and add it to the current robot's "targets" list
	 * @param players
	 */
	private void communicate(PlayerRecord[] players) {
		this.targets.clear(); //clear it first because other attacker's targets can change
		//Find out the targets
		for (PlayerRecord rec : players) {
			//If rec happens to be the playerRecord of itself, we want to add it anyways. since the array contains all targets
			if (rec.getTYPE().equals("Attacker") && rec.getCurrentTarget() != null) { 
				this.targets.add(rec.getCurrentTarget());
			}
		}
		//Potentially in the future, the other targets can give information about the players they have already fought -> e.g. the robots learn over time
	}
	
	/**
	 * Returns a PlayerRecord representing the current players target. Note that a target will always be returned.
	 * @return returns a PlayerRecord representing the target of the current attacker
	 */
	private PlayerRecord newTarget(PlayerRecord[] players) {
		this.communicate(players);
		//Edge case: first robot gets a target
		if (this.targets.size() == 0) {
			return this.priorityList[0];
		}
		for (PlayerRecord record : this.priorityList) {
			boolean found = false;
			for (PlayerRecord target : this.targets) {
				if (target.getPLAYER_ID() == record.getPLAYER_ID()) { 
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
	}
	
	/**
	 * Main update method for gathering the latest information from the application class
	 * @param players players is the PlayerRecord array with the newest information
	 */
	private void updateInfo(PlayerRecord[] players) {
		this.updatePriorityListAndTarget(players);
		this.sortByDistance();
	}
	
	/**
	 * Updates the current robot's priority list with the latest information from "players". also updates the current target information
	 * @param players players is the PlayerRecord array with the newest and latest information from the game
	 */
	private void updatePriorityListAndTarget(PlayerRecord[] players) {
		int idx = 0;
		for (PlayerRecord record : players) {
			if (! record.getTYPE().equals("Attacker")) {
				this.priorityList[idx] = record;
				idx++;
				//Update current target along when new information is passed
				if (this.getCurrentTarget() != null && record.getPLAYER_ID() == this.getCurrentTarget().getPLAYER_ID()) {
					this.setCurrentTarget(record);
				}
			}
		}
	}
	
	/**
	 * Selection sort algorithm that sorts the players in the current robot's priority list by distance
	 */
	private void sortByDistance() {
		int len = this.priorityList.length;
		for (int i = 0; i < this.priorityList.length - 1; i++) {
			for (int j = i + 1; j < this.priorityList.length; j++) {
				int dist1 = calcDistance(this.priorityList[j]), dist2 = calcDistance(this.priorityList[i]);
				if (dist1 < dist2) {
					swapPlayerRecord(i, j);
				}
			}
		}
	}
	
	/**
	 * Helper function for sorting the priority list
	 * @param idx1 idx1 is the index of the first PlayerRecord
	 * @param idx2 idx2 is the index of the second PlayerRecord
	 */
	private void swapPlayerRecord(int idx1, int idx2) {
		PlayerRecord temp = this.priorityList[idx1];
		this.priorityList[idx1] = this.priorityList[idx2];
		this.priorityList[idx2] = temp;
	}
	
	/**
	 * Calculates the distance between the current robot and the "rec" robot
	 * @param rec rec is the PlayerRecord of a robot in the current robot's priority list
	 * @return returns an integer representing their difference in distance
	 */
	private int calcDistance(PlayerRecord rec) {
		return Math.abs(rec.getAvenue() - this.getAvenue()) + Math.abs(rec.getStreet() - this.getStreet());
	}
}