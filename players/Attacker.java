package players;
import java.util.*;
import becker.robots.*;
import java.awt.*;

/**
 * Template robot class for the final summative in ICS4U
 * @author Eric Feng
 * @version Due date: June 13 2025
 */
public class Attacker extends Player{

	private PlayerRecord[] priorityList;
	private PlayerRecord currentTarget;
	private int roundsSpentChasing = 0, currentState = 1;
	private final static int MAX_CHASE_TIME = 5;
	private final static int STATE_CHASE = 1, STATE_FIGHT = 2, STATE_REST = 3;
	

	public Attacker(City city, int s, int a, Direction d) {
		super(city, s, a, d, 10, "Attacker", true);
		this.setColor(Color.RED); //attackers are black
		this.setSpeed(3);
//		this.setSpeed(Player.generator.nextInt(4) + 1); //different attackers have different speeds -> implement random later
	}
	
	private void printPriorityList() {
		System.out.println("\nPriority list of attacker: " + this.getPLAYER_ID());
		for (PlayerRecord rec : this.priorityList) {
			System.out.format("type: %s, street: %d, avenue: %d\n", rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}
	
	private void printTargetInfo() {
		System.out.println("\nTarget information:");
		System.out.format("type: %s, id: %d, street: %d, avenue: %d\n\n", this.currentTarget.getTYPE(), this.currentTarget.getPLAYER_ID(),
				this.currentTarget.getStreet(), this.currentTarget.getAvenue());
	}

	@Override 
	public void performAction(PlayerRecord[] players) { 
		this.updateInfo(players);
		switch(this.currentState) { 
			case 1: //chasing state -> could have multiple strategies in this case: maybe another switch
				this.chase();
				break;
			case 2: //fighting state
				this.fight(players);
				break;
			case 3: //resting state
				this.rest();
		}
	}
	
	public void chase() {
		//If there is currently no target, find a new target
//		if (this.roundsSpentChasing == Attacker.MAX_CHASE_TIME) { //switch to another innocent
//			return; //isolate the following code for now
//			this.sortPriority(players);
//			switchTargets();
//		}
		
		this.chaseTarget();
		this.roundsSpentChasing++;
		if (this.targetReached()) {
			this.currentState = 2; //change to fighting state
		}
	}
	
	private boolean targetReached() {
		if (this.getStreet() == this.currentTarget.getStreet() && this.getAvenue() == this.currentTarget.getAvenue()) {
			System.out.println("Target Reached!");
			return true;
		}
		return false;
	}
	
	public void fight(PlayerRecord[] players) {}
	
	public void rest() {}
	
	/**
	 * chases the target with a set amount of steps
	 * @param target target is the PlayerRecord info about the target
	 * @return returns a boolean representing whether or not the target has been reached
	 */
	private void chaseTarget() {
		int verticalDiff = this.getStreet() - this.currentTarget.getStreet();
		int horizontalDiff = this.getAvenue() - this.currentTarget.getAvenue();
		int speed = this.obtainSpeed();
		printPriorityList();
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
	
	private void directedMove(Direction dir, int steps) {
		this.turnTo(dir);
		this.move(steps);
	}
	
	private void switchTargets() {
		PlayerRecord oldTarget = this.currentTarget; //save the old target
		this.currentTarget = this.newTarget(); //find a new target
		
		this.roundsSpentChasing = 0; //reset the chase time
	}
	
	/**
	 * Returns a PlayerRecord representing the current players target. Note that a target will always be returned.
	 * @return returns a PlayerRecord representing the target of the current attacker
	 */
	private PlayerRecord newTarget() {
		
		//all targets are being chased -> pick a random player that's not an attacker to chase. note that we don't add to commonTargets
//		int idx = Player.generator.nextInt(this.priorityList.length);
//		PlayerRecord targetRecord = this.priorityList[idx];
//		return targetRecord; <- test later
		return this.priorityList[0];
	}
	
	/**
	 * Initial instruction when the game commences
	 */
	@Override
	public void initialize(PlayerRecord[] players) {
		int size = 0;
		for (PlayerRecord rec : players) {
			if (!rec.getTYPE().equals("Attacker")) {
				size++;
			}
		}
		this.priorityList = new PlayerRecord[size]; //Assign the priorityList a specific size
		
		this.updateInfo(players);
		this.currentTarget = newTarget();
	}
	
	private void updateInfo(PlayerRecord[] players) {
		this.updatePriorityListAndTarget(players);
		this.sortByDistance();
	}
	
	private void sortByDistance() {
		//Selection sort -> sort the other players by their distance to the current attacker
		int len = this.priorityList.length;
		for (int i = 0; i < this.priorityList.length - 1; i++) {
			for (int j = i + 1; j < this.priorityList.length; j++) {
				int dist1 = calcDistance(this.priorityList[j]), dist2 = calcDistance(this.priorityList[i]);
				if (dist1 > dist2) {
					swapPlayerRecord(i, j);
				}
			}
		}
	}
	
	private void updatePriorityListAndTarget(PlayerRecord[] players) {
		int idx = 0;
		for (PlayerRecord record : players) {
			if (! record.getTYPE().equals("Attacker")) {
				this.priorityList[idx] = record;
				idx++;
				//Update current target along when new information is passed
				if (this.currentTarget != null && record.getPLAYER_ID() == this.currentTarget.getPLAYER_ID()) {
					this.currentTarget = record;
				}
			}
		}
	}
	
	private void swapPlayerRecord(int idx1, int idx2) {
		PlayerRecord temp = this.priorityList[idx1];
		this.priorityList[idx1] = this.priorityList[idx2];
		this.priorityList[idx2] = temp;
	}
	
	private int calcDistance(PlayerRecord rec) {
		return Math.abs(rec.getAvenue() - this.getAvenue()) + Math.abs(rec.getStreet() - this.getStreet());
	}
}