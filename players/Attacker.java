package players;
import java.util.*;
import becker.robots.*;

public class Attacker extends Player{
	
	private static Random generator = new Random();
	private static ArrayList<PlayerRecord> commonTargets = new ArrayList<PlayerRecord>();
	private PlayerRecord currentTarget;
	private int roundsSpentChasing = 0;
	private final static int maxChaseTime = 5;
	

	public Attacker(City city, int s, int a, Direction d) {
		super(city, s, a, d, 10, "Attacker", true);
	}

	/**
	 * Assuming sortPriority() has already been called by doThing().
	 * Extra notes:
	 * if an attacker has been chasing an innocent for a specified amount of time, and they have not caught them,
	 * they will switch to another innocent. Implementation will involve:
	 * - switching currentTarget, and removing the previous target from commonTargets
	 */
	@Override 
	protected void performAction() { 
		//If there is currently no target, find a new target
		if (this.roundsSpentChasing == Attacker.maxChaseTime) { //switch to another innocent
			switchTargets();
		}
		
		chaseTarget(currentTarget);
		this.roundsSpentChasing++;
	}
	
	private void chaseTarget(PlayerRecord target) {
		int speed = this.obtainSpeed();
	}
	
	private void switchTargets() {
		PlayerRecord oldTarget = this.currentTarget; //save the old target
		PlayerRecord newTarget = this.newTarget(); //find a new target
		//if they point to the same thing, don't need to remove it because theres a max of 1 copy of each robot in commonTargets
		if (oldTarget != newTarget) { 
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
		//all targets are being chased -> pick a random player that's not an attacker to chase
		int idx = generator.nextInt(this.priorityList.size());
		PlayerRecord targetRecord = this.priorityList.get(idx);
		return targetRecord;
	}
	
	@Override
	protected void sortPriority(PlayerRecord[] players) {
		//Filter out all the other types
		for (int i = 0; i < players.length; i++) {
			if (!players[i].getTYPE().equals("Attacker")) { //Filter out all the other attackers
				this.priorityList.add(players[i]);
			}
		}
		
		//Bubble sort
		int len = this.priorityList.size();
		for (int i = 0; i < len; i++) {
			boolean swapped = false;
			for (int j = 0; j < len-1; j++) {
				//calculate the two distances
				int dist1 = calcDistance(this.priorityList.get(j)), dist2 = calcDistance(this.priorityList.get(j+1));
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
		PlayerRecord temp = this.priorityList.get(idx1);
		this.priorityList.set(idx1, this.priorityList.get(idx2));
		this.priorityList.set(idx2, temp);
	}
	
	private int calcDistance(PlayerRecord rec) {
		return Math.abs(rec.getAvenue() - this.getAvenue()) + Math.abs(rec.getStreet() - this.getStreet());
	}
}