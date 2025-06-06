package players;
import java.awt.Color;
import becker.robots.*;

public class Runner extends Player{
	private PlayerRecord[] priorityList;
	private int stamina = 10;

	public Runner(City c, int s, int a, Direction d) {
		super(c, s, a, d, 4, "Runner", false);
		this.setColor(Color.BLUE);
	}

	@Override
	public void performAction(PlayerRecord[] players) {
		this.priorityList = new PlayerRecord[players.length];
		this.updateList(players);
		this.sortPriority(players);
		this.doStrategy();
	}


	private void sortPriority(PlayerRecord[] players) {
		PlayerRecord thisRecord = priorityList[this.getPLAYER_ID()];
		priorityList[this.getPLAYER_ID()] = priorityList[priorityList.length - 1];
		priorityList[priorityList.length - 1] = thisRecord;

		for (int i = 1; i < priorityList.length - 1; i ++) {
			for (int j = i; j > 0; j--) {
				if ((priorityList[j].getTYPE().compareTo(priorityList[j - 1].getTYPE()) < 0) 
						|| ((priorityList[j].getTYPE().equals(priorityList[j - 1].getTYPE()) && 
								this.calcDistance(priorityList[j]) < this.calcDistance(priorityList[j-1])))) {
					PlayerRecord record = priorityList[j];
					priorityList[j] = priorityList[j - 1];
					priorityList[j - 1] = record;
				}
				else {
					break;
				}
			}
		}
	}
	private void updateList(PlayerRecord[] players) {
		if (this.priorityList[0] == null) {
			for(int i = 0; i < this.priorityList.length; i ++) {
				this.priorityList[i] = players[i];
			}
		}
		else {
			this.resetPriority();
			for(int i = 0; i < this.priorityList.length; i ++) {
				PlayerRecord previous = priorityList[i];
				this.priorityList[i] = players[i];
				if (this.calcDistance(this.priorityList[i], previous) > previous.getSpeed()) {
					this.priorityList[i].setSpeed(this.calcDistance(this.priorityList[i], previous));
				}
			}
		}
	}

	private int calcDistance(PlayerRecord r) { 
		return Math.abs(r.getAvenue() - this.getAvenue()) + Math.abs(r.getStreet() - this.getStreet());
	}

	private int calcDistance(PlayerRecord r1, PlayerRecord r2) { 
		return Math.abs(r1.getAvenue() - r2.getAvenue()) + Math.abs(r1.getStreet() - r2.getStreet());
	}

	private void doStrategy() {
		//		for (PlayerRecord i : this.priorityList) {
		//			System.out.println("ME " + i);
		//		}
		if (this.inDanger(this.priorityList[0])) {
			this.runAway();
		}
		//		int stepUsed = 0;
		//		if (Math.abs(this.priorityList[0].getAvenue() - this.getAvenue()) < 
		//		Math.abs(this.priorityList[0].getStreet() - this.getStreet())) {
		//			if (this.priorityList[0].getStreet() > this.getStreet()) {
		//				this.turnTo(Direction.EAST);
		//			}
		//			else {
		//				this.turnTo(Direction.WEST);
		//			}
		//		}
		//		else {
		//			if (this.priorityList[0].getAvenue() > this.getAvenue()) {
		//				this.turnTo(Direction.NORTH);
		//			}
		//			else {
		//				this.turnTo(Direction.SOUTH);
		//			}
		//		}
		//		while (stepUsed < this.obtainSpeed()) {
		//			this.move();
		//			stepUsed ++;
		//		}
	}

	private boolean inDanger(PlayerRecord record) {
		if((this.calcDistance(record) <= record.getSpeed()) || (this.calcDistance(record) <= 5)) {
			return true;
		}
		else {
			return false;
		}
	}

	private void runAway() {		
		PlayerRecord[] dangerList = this.findDangers();
		for (PlayerRecord i : dangerList) {
			System.out.println("ME " + i);
		}


		// Move with available speed
		int stepsTaken = 0;
		while (stepsTaken < this.obtainSpeed() && this.frontIsClear()) {
			Direction bestDirection = findSafestDirection(dangerList);
			this.turnTo(bestDirection);
			this.move();
			stepsTaken++;
		}
	}

	private PlayerRecord[] findDangers() {
		int dangerAttacker = 0;
		for (int i = 0; i < priorityList.length; i ++) {
			if(this.priorityList[i].getTYPE().equals("Attacker") && this.inDanger(this.priorityList[i])) {
				dangerAttacker ++;
			}
		}
		PlayerRecord[] dangerList = new PlayerRecord[dangerAttacker];
		for (int i = 0; i < dangerList.length; i ++) {
			dangerList[i] = this.priorityList[i];
		}
		return dangerList;
	}

	private Direction findSafestDirection(PlayerRecord[] dangers) {
		Direction[] possibleDirections = {
				Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
		};

		Direction safestDirection = Direction.NORTH;
		int lowestDangerScore = Integer.MAX_VALUE;
		
		for (Direction dir : possibleDirections) {
			if (!canMoveInDirection(dir)) {
				continue; // Skip blocked directions
			}
			// Calculate hypothetical position after moving
			int newAve = this.getAvenue();
			int newStr = this.getStreet();

			switch(dir) {
			case NORTH: newStr--; break;
			case EAST: newAve++; break;
			case SOUTH: newStr++; break;
			case WEST: newAve--; break;
			}

			// Calculate danger at new position
			int dangerScore = calculateDangerAt(newAve, newStr, dangers);

			if (dangerScore < lowestDangerScore) {
				lowestDangerScore = dangerScore;
				safestDirection = dir;
			}
		}

		return safestDirection;
	}

	private int calculateDangerAt(int avenue, int street, PlayerRecord[] dangers) {
		int totalDanger = 0;

		for (PlayerRecord danger : dangers) {
			// Calculate Manhattan distance from hypothetical position to each danger
			int distance = Math.abs(danger.getAvenue() - avenue) + 
					Math.abs(danger.getStreet() - street);
			switch(distance) {
			case 0: totalDanger += 1000; break;
			case 1: totalDanger += 1000; break;
			case 2: totalDanger += 800; break;
			case 3: totalDanger += 800; break;
			case 4: totalDanger += 600; break;
			case 5: totalDanger += 400; break;
			}
		}

		return totalDanger;
	}

	private boolean canMoveInDirection(Direction dir) {
		Direction currentFacing = this.getDirection();
		this.turnTo(dir);
		boolean clear = this.frontIsClear();
		this.turnTo(currentFacing); // Return to original facing
		return clear;
	}

	private void resetPriority() {
		mergeSort(this.priorityList, 0, priorityList.length - 1);
	}

	private static void mergeSort(PlayerRecord[] r, int start, int end) {
		if (start < end) {
			int mid = (start + end)/2;
			mergeSort(r, start, mid);
			mergeSort(r, mid + 1, end);
			merge(r, start, mid, end);
		}
	}
	private static void merge(PlayerRecord[] r, int start, int mid, int end) {
		PlayerRecord[] temp = new PlayerRecord[r.length];
		int pos1 = start;
		int pos2 = mid + 1;
		int spot = start;

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

		for (int i = start; i <= end; i ++) {
			r[i] = temp[i];
		}
	}

	public void move() {
		if(this.frontIsClear()) {
			super.move();
		}
	}
}