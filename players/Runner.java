package players;
import java.awt.Color;
import becker.robots.*;

public class Runner extends Player{
	private PlayerRecord[] priorityList;
	private int stamina = 10;
	private int steps;

	public Runner(City c, int s, int a, Direction d) {
		super(c, s, a, d, /*Player.generator.nextInt(3) + 2*/ 2, "Runner", false);
		this.setColor(Color.BLUE);
	}

	@Override
	public void performAction(PlayerRecord[] players) {
		this.stamina ++;
		steps = this.obtainSpeed();
		if (steps > this.stamina) {
			this.steps = stamina;
		}
		if (this.priorityList == null) {
			this.priorityList = new PlayerRecord[players.length];
		}
		this.learnDifferences(players);
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

	private void learnDifferences(PlayerRecord[] players) {
		if (this.priorityList[0] == null) {
			for(int i = 0; i < this.priorityList.length; i ++) {
				this.priorityList[i] = players[i];
				this.priorityList[i].setSpeed(1);
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
		PlayerRecord[] dangerList = this.findAttackers();
		
		if (this.inDanger(this.priorityList[0])) {
			this.runAway(dangerList);
		}
		else {
			if (this.getHp() < 50) {
				this.seekMedic();
			}
			else {
				this.rest(dangerList);
			}
		}
	}

	private void seekMedic() {
		PlayerRecord[] medicList = this.findMedics();
		
	}

	private void rest(PlayerRecord[] dangerList) {
		this.steps = 1;
		int[] bestLocation = findSafestLocation(dangerList);
		this.moveTo(bestLocation[0], bestLocation[1], true);
		this.stamina ++; 
	}

	private boolean inDanger(PlayerRecord record) {
		if((this.calcDistance(record) <= record.getSpeed()) || (this.calcDistance(record) <= 6)) {
			return true;
		}
		else {
			return false;
		}
	}
	private PlayerRecord[] findAttackers() {
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
	
	private  PlayerRecord[] findMedics() {
		int numMedic = 0;
		int firstIndex = 0;
		
		for (int i = 0; i < priorityList.length; i ++) {
			if(this.priorityList[i].getTYPE().equals("Medic")) {
				if (numMedic == 0) {
					firstIndex = i;
				}
				numMedic ++;
			}
		}
		PlayerRecord[] medicList = new PlayerRecord[numMedic];
		for (int i = 0; i < medicList.length; i ++) {
			medicList[i] = this.priorityList[firstIndex++];
		}
		return medicList;
	}

	private void runAway(PlayerRecord[] dangerList) {		

		int[] bestLocation = findSafestLocation(dangerList);
		this.moveTo(bestLocation[0], bestLocation[1], true);
		
		this.stamina -= this.steps;
	}

	private int[] findSafestLocation(PlayerRecord[] dangers) {

		int [] safestLocation = {this.getStreet(), this.getAvenue()};
		int lowestScore = Integer.MAX_VALUE;

		int [][] options = this.calculateMoveOptions();
		for (int i = 0; i < options.length; i ++) {
			System.out.println(options[i][0] + " " + options[i][1]);
		}

		for (int i = 0; i < options.length; i ++) {
			int newStr = options[i][0];
			int newAve = options[i][1];

			if (newStr < 0 || newStr > 12) {
				newStr = 0;
			}

			if (newAve < 0 || newAve > 23) {
				newAve = 0;
			}

			// Calculate danger at new position
			int dangerScore = calculateDangerAt(newStr, newAve, dangers);
			
			int distance = Math.abs(dangers[0].getAvenue() - newAve) + 
					Math.abs(dangers[0].getStreet() - newStr);
			
			int distance1 = Math.abs(dangers[0].getAvenue() - safestLocation[1]) + 
					Math.abs(dangers[0].getStreet() - safestLocation[0]);
			
			if (dangerScore < lowestScore || (dangerScore == lowestScore && distance > distance1)) {
				lowestScore = dangerScore;
				safestLocation[0] = newStr;
				safestLocation[1] = newAve;
			}
		}

		return safestLocation;
	}

	public int[][] calculateMoveOptions() {
		int speed = this.steps;

		int [][] options = new int[speed*4][2];

		int currentStr = this.getStreet();
		int currentAve = this.getAvenue();

		int optionIndex = 0;
		int i = 0;
		for (int s = speed; s > 0; s--) {
			// North
			options[optionIndex++] = new int[]{currentStr + i, currentAve - s};
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

	private int calculateDangerAt(int street, int avenue, PlayerRecord[] dangers) {
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
		}
		if (avenue == 0 || avenue == 23) {
			totalDanger += 300;
		}
		if (street == 0 || street == 12) {
			totalDanger += 300;
		}
		if (avenue == 1 || avenue == 22) {
			totalDanger += 150;
		}
		if (street == 1 || street == 11) {
			totalDanger += 150;
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