package players;
import java.awt.Color;
import becker.robots.*;

public class Runner extends Player{
	private int agility;
	private PlayerRecord[] priorityList;

	public Runner(City c, int s, int a, Direction d) {
		super(c, s, a, d, 2, "Runner", false);
		this.setColor(Color.BLUE);
		this.agility = 5;
	}

	@Override
	public void performAction(PlayerRecord[] players) {
		this.priorityList = new PlayerRecord[players.length];
		this.updateList(players);
		this.sortPriority(players);
		this.doThing();
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

	private void doThing() {
		for (PlayerRecord i : this.priorityList) {
			System.out.println("ME " + i);
		}
		int stepTook = 0;
		if (Math.abs(this.priorityList[0].getAvenue() - this.getAvenue()) > 
		Math.abs(this.priorityList[0].getStreet() - this.getStreet())) {
			if (this.priorityList[0].getStreet() > this.getStreet()) {
				this.turnTo(Direction.EAST);
			}
			else {
				this.turnTo(Direction.WEST);
			}
		}
		else {
			if (this.priorityList[0].getAvenue() > this.getAvenue()) {
				this.turnTo(Direction.NORTH);
			}
			else {
				this.turnTo(Direction.SOUTH);
			}
		}
		while (stepTook < this.obtainSpeed()) {
			this.move();
			stepTook ++;
		}
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