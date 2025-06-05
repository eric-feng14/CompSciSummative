package players;
import java.util.*;
import becker.robots.*;
import java.awt.*;
public class Attacker extends Player{
	
	private static ArrayList<PlayerRecord> commonTargets = new ArrayList<PlayerRecord>();
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
		System.out.println("\nPriority List of Attacker " + this.getPLAYER_ID() + ":");
		for (PlayerRecord rec : this.priorityList) {
			System.out.format("type: %s, street: %d, avenue: %d\n", rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}
	
	private void printCommonTargets() {
		System.out.println("\nCommon Targets with length " + Attacker.commonTargets.size() + ":");
		for (PlayerRecord rec : Attacker.commonTargets) {
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
		this.sortPriority(players);
		//Any other random cases, e.g. A runner sacrifices themself
		switch(this.currentState) { 
			case 1: //chasing state
				this.chase(players);
				break;
			case 2: //fighting state
				this.fight(players);
				break;
			case 3: //resting state
				this.rest();
		}
	}
	
	public void chase(PlayerRecord[] players) {
		//Debugging
//		this.printPriorityList();
//		this.printTargetInfo();
		
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
		int absVerticalDiff = Math.abs(verticalDiff), absHorizontalDiff = Math.abs(horizontalDiff);
		int speed = this.obtainSpeed();
		//Try moving vertically towards the target
		if (verticalDiff != 0) {
			Direction dir = (verticalDiff > 0) ? Direction.NORTH : Direction.SOUTH;
			this.turnTo(dir);
			if (absVerticalDiff >= speed) {
				System.out.println("Vertical Difference >= speed");
				this.move(speed);
			} else {
				this.move(absVerticalDiff);
				Direction newDir = (horizontalDiff > 0) ? Direction.WEST : Direction.EAST;
				this.turnTo(newDir);
				this.move(Math.min(absHorizontalDiff, speed - absVerticalDiff));
			}
			return;
		}
		if (horizontalDiff != 0) {
			Direction dir = (horizontalDiff > 0) ? Direction.WEST : Direction.EAST;
			this.turnTo(dir);
			if (absHorizontalDiff >= speed) {
				this.move(speed);
			} else {
				this.move(absHorizontalDiff);
			}
			return;
		}
	}
	
	private void switchTargets() {
		PlayerRecord oldTarget = this.currentTarget; //save the old target
		this.currentTarget = this.newTarget(); //find a new target
		//if they point to the same thing, don't need to remove it because theres a max of 1 copy of each robot in commonTargets
		if (oldTarget != this.currentTarget) { 
			Attacker.commonTargets.remove(oldTarget); //remove the old target
		}
		
		this.roundsSpentChasing = 0; //reset the chase time
	}
	
	/**
	 * Returns a PlayerRecord representing the current players target. Note that a target will always be returned.
	 * @return returns a PlayerRecord representing the target of the current attacker
	 */
	private PlayerRecord newTarget() {
		for (PlayerRecord rec : this.priorityList) { //assuming priorityList is already sorted
			if (! Attacker.commonTargets.contains(rec)) { //target was not found in the common target list
				Attacker.commonTargets.add(rec);
				return rec;
			}
		}
		//all targets are being chased -> pick a random player that's not an attacker to chase. note that we don't add to commonTargets
//		int idx = Player.generator.nextInt(this.priorityList.length);
//		PlayerRecord targetRecord = this.priorityList[idx];
//		return targetRecord; <- test later
		return null;
	}
	
	@Override
	public void initialize(PlayerRecord[] players) {
		int size = 0;
		for (PlayerRecord rec : players) {
			if (!rec.getTYPE().equals("Attacker")) {
				size++;
			}
		}
		this.priorityList = new PlayerRecord[size]; //Assign the priorityList a specific size
		
		this.sortPriority(players);
		//Assign the player a target.
		this.currentTarget = newTarget();
	}
	
	private void sortPriority(PlayerRecord[] players) {
		this.filterAttackers(players);
		
		//Bubble sort
		int len = this.priorityList.length;
		for (int i = 0; i < len; i++) {
			boolean swapped = false;
			for (int j = 0; j < len-1; j++) {
				//calculate the two distances
				int dist1 = calcDistance(this.priorityList[j]), dist2 = calcDistance(this.priorityList[j+1]);
				if (dist1 > dist2) {
					swapPlayerRecord(j, j+1);
					swapped = true;
				}
			}
			if (!swapped) { //efficiency optimization
				break;
			}
		}
		
		for (PlayerRecord rec : this.priorityList) {
			System.out.println(rec);
		}
	}
	
	private void filterAttackers(PlayerRecord[] players) {
		int idx = 0;
		for (PlayerRecord record : players) {
			if (! record.getTYPE().equals("Attacker")) {
				this.priorityList[idx] = record;
				idx++;
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