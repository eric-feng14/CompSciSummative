package players;
import java.awt.Color;
import becker.robots.*;

public class Runner extends Player{
	private PlayerRecord[] priorityList;
	private int stamina = 10;
	
	public Runner(City c, int s, int a, Direction d) {
		super(c, s, a, d, 3, "Runner", false);
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
	}

	private boolean inDanger(PlayerRecord record) {
		if((this.calcDistance(record) <= record.getSpeed()) || (this.calcDistance(record) <= 5)) {
			return true;
		}
		else {
			return false;
		}
	}
	private PlayerRecord[] findDangers() {
		int numAttacker = 0;
		for (int i = 0; i < priorityList.length; i ++) {
			if(this.priorityList[i].getTYPE().equals("Attacker")) {
				numAttacker ++;
			}
		}
		PlayerRecord[] dangerList = new PlayerRecord[numAttacker];
		for (int i = 0; i < dangerList.length; i ++) {
			dangerList[i] = this.priorityList[i];
		}
		return dangerList;
	}
	
	private void runAway() {		
		PlayerRecord[] dangerList = this.findDangers();
//		for (PlayerRecord i : dangerList) {
//			System.out.println("ME " + i);
//		}
		
		Direction bestDirection = findSafestDirection(dangerList);
		this.turnTo(bestDirection);

		// Move with available speed
		int stepsTaken = 0;
		while (stepsTaken < this.obtainSpeed() && this.frontIsClear()) {
			this.move();
			stepsTaken++;
		}
	}

	private Direction findSafestDirection(PlayerRecord[] dangers) {
		Direction[] possibleDirections = {
				Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
		};

		Direction safestDirection = Direction.NORTH;
		int lowestDangerScore = Integer.MAX_VALUE;
		
		for (Direction dir : possibleDirections) {
			int newAve = this.getAvenue();
			int newStr = this.getStreet();

			switch(dir) {
			case NORTH: newStr-=this.obtainSpeed(); break;
			case EAST: newAve+=this.obtainSpeed(); break;
			case SOUTH: newStr+=this.obtainSpeed(); break;
			case WEST: newAve-=this.obtainSpeed(); break;
			}
			if (newAve < 0) {
				newAve = 0;
			}
			if (newStr < 0) {
				newStr = 0;
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
			case 1: totalDanger += 800; break;
			case 2: totalDanger += 600; break;
			case 3: totalDanger += 400; break;
			case 4: totalDanger += 200; break;
			}
			if (avenue == 0 || avenue == 23) {
				totalDanger += 100;
			}
			if (street == 0 || street == 12) {
				totalDanger += 100;
			}
			if (avenue == 1 || avenue == 22) {
				totalDanger += 50;
			}
			if (street == 1 || street == 11) {
				totalDanger += 50;
			}
		}
		return totalDanger;
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