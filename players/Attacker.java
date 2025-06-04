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
		this.setColor(new Color(0,0,0)); //attackers are black
		this.setSpeed(Player.generator.nextInt(4) + 1); //different attackers have different speeds
	}
	
	private void printPriorityList() {
		System.out.println("\nPriority List:");
		System.out.println(this.getStreet() + " " + this.getAvenue());
		for (PlayerRecord rec : this.priorityList) {
			System.out.format("id: %d, type: %s, street: %s, avenue: %s\n", this.getPLAYER_ID(), rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}
	
	private void printCommonTargets() {
		System.out.println("\nCommon Targets:\n");
		System.out.println(Attacker.commonTargets.size());
		for (PlayerRecord rec : Attacker.commonTargets) {
			System.out.format("id: %d, type: %s, street: %s, avenue: %s\n", this.getPLAYER_ID(), rec.getTYPE(), rec.getStreet(), rec.getAvenue());
		}
	}

	@Override 
	public void performAction(PlayerRecord[] players) { 
		switch(this.currentState) { 
			case 1: //chasing state
				this.chase(players);
				break;
			case 2: //fighting state
				this.fight();
				break;
			case 3: //resting state
				this.rest();
		}
	}
	
	public void chase(PlayerRecord[] players) {
		this.sortPriority(players);
		
		//Debugging
		this.printPriorityList();
		System.out.format("type: %s, id: %d", this.currentTarget.getTYPE(), this.currentTarget.getPLAYER_ID());
		
		//If there is currently no target, find a new target
		if (this.roundsSpentChasing == Attacker.MAX_CHASE_TIME) { //switch to another innocent
			switchTargets();
		}
		
		if (chaseTarget(currentTarget)) {
			//defeat the target(s) at the current position
		}
		this.roundsSpentChasing++;
	}
	
	public void fight() {}
	
	public void rest() {}
	
	/**
	 * chases the target with a set amount of steps
	 * @param target target is the PlayerRecord info about the target
	 * @return returns a boolean representing whether or not the target has been reached
	 */
	private boolean chaseTarget(PlayerRecord target) {
		int speed = this.obtainSpeed();
		
		
		if (target.getStreet() == this.getStreet() && target.getAvenue() == this.getAvenue()) {
			return true;
		}
		return false;
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
		int idx = Player.generator.nextInt(this.priorityList.length);
		PlayerRecord targetRecord = this.priorityList[idx];
		return targetRecord;
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
		int idx = 0;
		for (PlayerRecord rec : players) {
			if (!rec.getTYPE().equals("Attacker")) { //filter out all the attackers every time info from the application class comes here
				this.priorityList[idx] = rec;
				idx++;
			}
		}
		
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