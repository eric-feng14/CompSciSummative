package players;
import java.awt.Color;
import java.util.ArrayList;

import app.*;
import becker.robots.*;
import powerUps.EnhancedThing;

/**
 * A runner focuses on avoiding attackers, collecting power-ups, and maintaining stamina
 * @author Felix Wang
 * @version Jun. 13th, 2025
 */
public class Runner extends Player{
	private PlayerRecord[] priorityList; // Sorted list of players by priority
	private EnhancedThing[] powerUps; // Array of available power-ups
	private int steps; // Number of steps to take in current turn
	private final int MAX_STAM; // Max stamina of player
	private boolean pickedPowerUp = false;

	/**
	 * Constructor for the Runner player
	 * @param c - The city where the player exists
	 * @param s - The initial street location
	 * @param a - The initial avenue location
	 * @param d - The initial direction
	 */
	public Runner(City c, int s, int a, Direction d) {
		super(c, s, a, d, /*Player.generator.nextInt(3) + 2*/ 3, "Runner", false);
		this.setStamina(10);
		this.MAX_STAM = 10;
		this.setColor(Color.BLUE);
	}

	/**
	 * Performs the player's action for the current turn
	 * @param players - Array of updated player records from Main
	 * @param powerUps - List of available power-ups from Main
	 */
	@Override
	public void performAction(PlayerRecord[] players, ArrayList<EnhancedThing> powerUps) {
		// Recover 1 stamina point each turn
		this.addStamina(1);

		// Determine how many steps can be taken based on speed and stamina
		this.steps = this.obtainSpeed();
		if (this.steps > this.getStamina()) {
			this.steps = this.getStamina();
		}
		//System.out.println("Stam: " + this.getStamina());

		this.learnDifferences(players);
		this.sortPriority();
		this.sortPowerUps(powerUps); 
		this.doStrategy();
	}

	/**
	 * Sorts available power-ups by distance and safety
	 * @param powerUps - List of available power-ups
	 */
	private void sortPowerUps(ArrayList<EnhancedThing> powerUps) {
		PlayerRecord[] dangerList = this.findAttackers();

		// Resize array if needed
		if ((this.powerUps == null) || (this.powerUps.length != powerUps.size())) {
			this.powerUps = new EnhancedThing[powerUps.size()];
		}

		// Copy power-ups to array
		for (int i = 0; i < powerUps.size(); i ++) {
			this.powerUps[i] = powerUps.get(i);
		}

		// Selection sort power-ups by distance and danger
		for (int i = 0; i < this.powerUps.length - 1; i ++) {
			for (int j = i + 1; j < this.powerUps.length; j ++) {
				// Compare distance and danger level
				if (this.calcDistance(this.powerUps[i]) > this.calcDistance(this.powerUps[j])
						|| (this.calcDistance(this.powerUps[i]) == this.calcDistance(this.powerUps[j]) 
						&& this.calculateDangerAt(this.powerUps[i].getStreet(), this.powerUps[i].getAvenue(), dangerList) > 
						this.calculateDangerAt(this.powerUps[j].getStreet(), this.powerUps[j].getAvenue(), dangerList))) {
					// Swap positions if current is worse than next
					EnhancedThing thing = this.powerUps[i];
					this.powerUps[i] = this.powerUps[j];
					this.powerUps[j] = thing;
				}
			}
		}
	}

	/**
	 * Attempts to pick up a power-up
	 * @param thing - The power-up to pick up
	 */
	@Override
	public void pickPowerUp(EnhancedThing thing) {
		if(this.canPickThing()) {
			thing.applyTo(this);
			this.pickThing();
			this.pickedPowerUp = true;
		}
	}

	/**
	 * Sends signal to Main to remove the picked power-up
	 */
	@Override
	public void sendSignal() {
		if (this.pickedPowerUp) {
			Main.signal("remove", this.getPLAYER_ID(), this.powerUps[0].getID());
			AttackerTester.signal("remove", this.getPLAYER_ID(), this.powerUps[0].getID());
			RunnerTester.signal("remove", this.getPLAYER_ID(), this.powerUps[0].getID());
			MedicTester.signal("remove", this.getPLAYER_ID(), this.powerUps[0].getID());
			this.pickedPowerUp = false;
		}
	}

	/**
	 * Increases stamina up to the maximum stamina
	 * @param num - Amount to increase stamina by
	 */
	private void addStamina(int num) {
		if (this.getStamina() + num <= this.MAX_STAM) {
			this.setStamina(this.getStamina() + num);
		}
		else {
			this.setStamina(this.MAX_STAM);
		}
	}

	/**
	 * Sorts players by priority (attackers first, then closest)
	 */
	private void sortPriority() {
		// Move self to end of priority list
		PlayerRecord thisRecord = this.priorityList[this.getPLAYER_ID()];
		this.priorityList[this.getPLAYER_ID()] = this.priorityList[this.priorityList.length - 1];
		this.priorityList[this.priorityList.length - 1] = thisRecord;
		
		// Insertion sort for priority list
		for (int i = 1; i < this.priorityList.length - 1; i ++) {
			for (int j = i; j > 0; j--) {
				// Compare by type (attackers first) and distance
				if (((this.priorityList[j].getTYPE().compareTo(this.priorityList[j - 1].getTYPE()) < 0) 
						|| ((this.priorityList[j].getTYPE().equals(this.priorityList[j - 1].getTYPE()) && 
								this.calcDistance(this.priorityList[j]) < this.calcDistance(this.priorityList[j-1]))))
						&& (!this.priorityList[j].isDefeated())) {
					// Swap if current should come before previous
					PlayerRecord record = this.priorityList[j];
					this.priorityList[j] = this.priorityList[j - 1];
					this.priorityList[j - 1] = record;
				}
				else {
					break;
				}
			}
		}
//		for(PlayerRecord i : this.priorityList) {
//			System.out.println(i);
//		}
	}

	/**
	 * Updates knowledge about other players' positions and speeds
	 * @param players - Array of updated player records from Main
	 */
	private void learnDifferences(PlayerRecord[] players) {
		// Initialize or resize priority list if needed
		if ((this.priorityList == null) || (this.priorityList.length != players.length)) {
			this.priorityList = new PlayerRecord[players.length];
		}

		if (this.priorityList[0] == null) {
			// First time learning about players
			for(int i = 0; i < this.priorityList.length; i ++) {
				this.priorityList[i] = players[i];
			}
		}
		else {
			// Reset priorityList sorted by playerID so it matches the one from Main by index
			this.resetPriority();
			// Compare the two lists and update differences
			for(int i = 0; i < this.priorityList.length; i ++) {
				PlayerRecord previous = priorityList[i];
				this.priorityList[i] = players[i];
				// Update speed estimate if player moved farther than expected
				if (this.calcDistance(this.priorityList[i], previous) > previous.getSpeed()) {
					this.priorityList[i].setSpeed(this.calcDistance(this.priorityList[i], previous));
				}
				else {
					this.priorityList[i].setSpeed(previous.getSpeed());
				}
			}
		}
	}

	/**
	 * Calculates distance to another player
	 * @param r - The player record to measure distance to
	 * @return distance to the player
	 */
	private int calcDistance(PlayerRecord r) { 
		return Math.abs(r.getAvenue() - this.getAvenue()) + Math.abs(r.getStreet() - this.getStreet());
	}

	/**
	 * Calculates distance to a power-up
	 * @param powerUp - The power-up to measure distance to
	 * @return distance to the power-up
	 */
	private int calcDistance(EnhancedThing powerUp) {
		return Math.abs(powerUp.getAvenue() - this.getAvenue()) + Math.abs(powerUp.getStreet() - this.getStreet());
	}

	/**
	 * Calculates distance between two player records
	 * @param r1 - First player record
	 * @param r2 - Second player record
	 * @return distance between the players
	 */
	private int calcDistance(PlayerRecord r1, PlayerRecord r2) { 
		return Math.abs(r1.getAvenue() - r2.getAvenue()) + Math.abs(r1.getStreet() - r2.getStreet());
	}

	/**
	 * Determines and executes the best strategy based on current situation
	 */
	private void doStrategy() {
		PlayerRecord[] dangerList = this.findAttackers();
		// Check if in immediate danger from closest attacker
		if (this.inDanger(this.priorityList[0])) {
			if (this.getStamina() == 1) {
				this.rest(dangerList);
			}
			else {
//				System.out.println("run");
				this.runAway(dangerList);
			}
		}
		else {
			// If low stamina, rest near power-up
			if (this.getStamina() <= 5) {
//				System.out.println("rest");
				this.rest(dangerList);
			}
			// If low health, seek medics
			else if (this.getHp() <= 50) {
//				System.out.println("medic");
				this.seekMedic(dangerList);
			}
			else if (this.powerUps.length != 0) {
				// Otherwise seek power-ups
//				System.out.println("power");
				this.seekPowerUps(dangerList);
			}
			else {
				this.rest(dangerList);
			}
		}
	}

	/**
	 * Seeks nearby power-ups 
	 */
	private void seekPowerUps(PlayerRecord[] dangerList) {
		int[] bestLocation = findBestLocation(dangerList);
		this.moveTo(bestLocation[0], bestLocation[1], true);
		// Safety check in case there are no more power-ups
		if (this.powerUps.length != 0) {
			this.pickPowerUp(this.powerUps[0]);
		}
		this.setStamina(this.getStamina() - this.steps);
	}


	/**
	 * Seeks nearby medics for healing 
	 */
	private void seekMedic(PlayerRecord[] dangerList) {
		PlayerRecord[] medicList = this.findMedics();
		// If it can reach the nearest medic within its steps
		if (this.calcDistance(medicList[0]) <= this.steps) {
			this.steps = this.calcDistance(medicList[0]);
			this.moveTo(medicList[0].getStreet(), medicList[0].getAvenue(), true);
			this.setStamina(this.getStamina() - this.steps);
		}
		// Otherwise just rest
		else {
			this.rest(dangerList);
		}
	}

	/**
	 * Rests to recover stamina near a safe power-up
	 * @param dangerList - Array of attackers
	 */
	private void rest(PlayerRecord[] dangerList) {
		this.steps = 1; // Only move 1 step when resting
		int[] bestLocation = findBestLocation(dangerList);
		this.moveTo(bestLocation[0], bestLocation[1], true);
		// Safety check in case there are no more power-ups
		if (this.powerUps.length != 0) {
			this.pickPowerUp(this.powerUps[0]);
		}
		this.addStamina(1); // Recover additional stamina when resting
	}

	/**
	 * Checks if a player is dangerously close
	 * @param record - The player record to check
	 * @return True if the player is within dangerous distance
	 */
	private boolean inDanger(PlayerRecord record) {
		if((this.calcDistance(record) <= record.getSpeed()) || (this.calcDistance(record) <= 6)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Finds all attackers
	 * @return Array of attacker player records
	 */
	private PlayerRecord[] findAttackers() {
		int numAttacker = 0;
		// Count number of attackers
		for (int i = 0; i < this.priorityList.length; i ++) {
			if(this.priorityList[i].getTYPE().equals("Attacker")) {
				numAttacker ++;
			}
		}
		// Create and populate attacker array
		PlayerRecord[] dangerList = new PlayerRecord[numAttacker];
		for (int i = 0; i < dangerList.length; i ++) {
			dangerList[i] = this.priorityList[i];
		}
		return dangerList;
	}

	/**
	 * Finds all medics
	 * @return Array of medic player records
	 */
	private PlayerRecord[] findMedics() {
		int numMedic = 0;
		int firstIndex = 0;

		// Count number of medics and find first index
		for (int i = 0; i < this.priorityList.length; i ++) {
			if(this.priorityList[i].getTYPE().equals("Medic")) {
				if (numMedic == 0) {
					firstIndex = i;
				}
				numMedic ++;
			}
		}
		// Create and populate medic array
		PlayerRecord[] medicList = new PlayerRecord[numMedic];
		for (int i = 0; i < medicList.length; i ++) {
			medicList[i] = this.priorityList[firstIndex++];
			// Same as:
			// medicList[i] = this.priorityList[firstIndex];
			// firstIndex++;
		}
		return medicList;
	}

	/**
	 * Moves away from dangerous players (attackers)
	 * @param dangerList - Array of attackers
	 */
	private void runAway(PlayerRecord[] dangerList) {        
		int[] bestLocation = this.findBestLocation(dangerList);
		this.moveTo(bestLocation[0], bestLocation[1], true);
		if (this.powerUps.length != 0) {
			this.pickPowerUp(this.powerUps[0]);
		}
		this.setStamina(this.getStamina() - this.steps);
	}

	/**
	 * Finds the safest location to move to
	 * @param dangers - Array of attackers
	 * @return Array containing [street, avenue] of best location
	 */
	private int[] findBestLocation(PlayerRecord[] dangers) {
		int [] safestLocation = {this.getStreet(), this.getAvenue()};
		int lowestScore = Integer.MAX_VALUE;

		int [][] options = this.calculateMoveOptions();
//		for (int i = 0; i < options.length; i ++) {
//			System.out.println(options[i][0] + " " + options[i][1]);
//			
//		}

		// Evaluate each possible move option
		for (int i = 0; i < options.length; i ++) {
			int newStr = options[i][0];
			int newAve = options[i][1];

			// Handle edge of city wrapping
			if (newStr < 0) {
				newStr = 0;
			}
			if (newStr > 12) {
				newStr = 12;
			}

			if (newAve < 0) {
				newAve = 0;
			}
			if(newAve > 23) {
				newAve = 23;
			}

			// Calculate danger at new position
			int dangerScore = this.calculateDangerAt(newStr, newAve, dangers);

			// If there are more power-ups, go closer to power-ups
			if (this.powerUps.length > 0) {
				int distanceP = Math.abs(this.powerUps[0].getAvenue() - newAve) + 
						Math.abs(this.powerUps[0].getStreet() - newStr);

				int distanceP1 = Math.abs(this.powerUps[0].getAvenue() - safestLocation[1]) + 
						Math.abs(this.powerUps[0].getStreet() - safestLocation[0]);
				// Choose location with least danger or closer to power-up if equal danger
				if (dangerScore < lowestScore || (dangerScore == lowestScore && distanceP < distanceP1)) {
					lowestScore = dangerScore;
					safestLocation[0] = newStr;
					safestLocation[1] = newAve;
				}
			}
			
			// Otherwise, stay as far away from attackers as possible
			else {
				int distance = Math.abs(dangers[0].getAvenue() - newAve) + 
						Math.abs(dangers[0].getStreet() - newStr);

				int distance1 = Math.abs(dangers[0].getAvenue() - safestLocation[1]) + 
						Math.abs(dangers[0].getStreet() - safestLocation[0]);
				// Choose location with least danger or farthest from the nearest attacker if equal danger
				if (dangerScore < lowestScore || (dangerScore == lowestScore && distance > distance1)) {
					lowestScore = dangerScore;
					safestLocation[0] = newStr;
					safestLocation[1] = newAve;
				}
			}
		}

		// Consider moving directly to power-up if it's safe and reachable
		if (this.powerUps.length > 0 && this.calcDistance(this.powerUps[0]) <= this.steps && 
				this.calculateDangerAt(this.powerUps[0].getStreet(), this.powerUps[0].getAvenue(), dangers) <= lowestScore + 300) {
			safestLocation[0] = this.powerUps[0].getStreet();
			safestLocation[1] = this.powerUps[0].getAvenue();
			this.steps = this.calcDistance(this.powerUps[0]);
		}

		return safestLocation;
	}

	/**
	 * Calculates all possible move options within current steps
	 * @return 2D array of possible [street, avenue] positions
	 */
	public int[][] calculateMoveOptions() {
		int speed = this.steps;
//		System.out.println("steps: " + this.steps);
		int [][] options = new int[speed*4][2];

		int currentStr = this.getStreet();
		int currentAve = this.getAvenue();

		int optionIndex = 0;
		int i = 0;
		// Generate move options in all directions
		for (int s = speed; s > 0; s--) {
			// North
			options[optionIndex++] = new int[]{currentStr + i, currentAve - s}; 
			// Same as:
			// options[optionIndex] = new int[]{currentStr + i, currentAve - s}; 
			// optionIndex++;
			
			// East
			options[optionIndex++] = new int[]{currentStr + s, currentAve + i};
			// South
			options[optionIndex++] = new int[]{currentStr - i, currentAve + s};
			// West
			options[optionIndex++] = new int[]{currentStr - s, currentAve - i};
			i ++;
		}

		return options;
	}

	/**
	 * Calculates danger score at a specific location.
	 * @param street - Street coordinate to evaluate
	 * @param avenue - Avenue coordinate to evaluate
	 * @param dangers - Array of attackers
	 * @return Danger score (higher is more dangerous)
	 */
	private int calculateDangerAt(int street, int avenue, PlayerRecord[] dangers) {
		int totalDanger = 0;

		// Add danger from each attacker based on distance
		for (PlayerRecord danger : dangers) {
			int distance = Math.abs(danger.getAvenue() - avenue) + Math.abs(danger.getStreet() - street);

			switch(distance) {
			case 0: totalDanger += 1000; break;
			case 1: totalDanger += 800; break;
			case 2: totalDanger += 600; break;
			case 3: totalDanger += 400; break;
			case 4: totalDanger += 300; break;
			case 5: totalDanger += 200; break;
			case 6: totalDanger += 100; break;
			}
		}

		// Add danger for being near edges of city
		if (avenue == 0 || avenue == 23) {
			totalDanger += 300;
		}
		if (street == 0 || street == 12) {
			totalDanger += 300;
		}
		
		// Add less danger for being near but not at edges
		if (avenue == 1 || avenue == 22) {
			totalDanger += 150;
		}
		if (street == 1 || street == 11) {
			totalDanger += 150;
		}
		return totalDanger;
	}

	/**
	 * Resets priority list by sorting players by ID
	 */
	private void resetPriority() {
		this.mergeSort(this.priorityList, 0, this.priorityList.length - 1);
	}

	/**
	 * Merge sort the priority list
	 * @param r - Array to sort
	 * @param start - Starting index
	 * @param end - Ending index
	 */
	private void mergeSort(PlayerRecord[] r, int start, int end) {
		if (start < end) {
			int mid = (start + end)/2;
			this.mergeSort(r, start, mid);
			this.mergeSort(r, mid + 1, end);
			this.merge(r, start, mid, end);
		}
	}

	/**
	 * Merge helper for merge sort
	 * @param r - Array to merge
	 * @param start - Starting index
	 * @param mid - Middle index
	 * @param end - Ending index
	 */
	private void merge(PlayerRecord[] r, int start, int mid, int end) {
		PlayerRecord[] temp = new PlayerRecord[r.length];
		int pos1 = start;
		int pos2 = mid + 1;
		int spot = start;

		// Merge two sorted halves
		while (!(pos1 > mid && pos2 > end)) {
			if ((pos1 > mid) || (pos2 <= end && r[pos2].getPLAYER_ID() < r[pos1].getPLAYER_ID())) {
				temp[spot] = r[pos2];
				pos2 ++;
			}
			else {
				temp[spot] = r[pos1];
				pos1 ++;
			}
			spot ++;
		}

		// Copy merged array back
		for (int i = start; i <= end; i ++) {
			r[i] = temp[i];
		}
	}

	/**
	 * Only moves if the path is clear to prevent crashing
	 */
	public void move() {
		// If the front is clear
		if(this.frontIsClear()) {
			super.move();
		}
	}
}