package players;
import java.awt.Color;
import becker.robots.*;

public class Runner extends Player{
	private int agility;
	private PlayerRecord[] priorityList;
	
	public Runner(City c, int s, int a, Direction d) {
		super(c, s, a, d, 5, "Runner", false);
		this.setColor(Color.BLUE);
		this.agility = 5;
	}

	@Override
	public void performAction(PlayerRecord[] players) {
		this.updateList(players);
		this.sortPriority(players);
		this.runAway();
	}
	
	private void sortPriority(PlayerRecord[] players) {
		for (int i = 1; i < priorityList.length; i ++) {
			for (int j = i; j > 0; j--) {
				if ((priorityList[i].getTYPE().compareTo(priorityList[j - 1].getTYPE()) < 0) 
						&& this.calcDistance(priorityList[i]) < this.calcDistance(priorityList[i-1])) {
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
		this.priorityList = new PlayerRecord[players.length];
		
		for(int i = 0; i < this.priorityList.length; i ++) {
			this.priorityList[i] = players[i];
		}
	}
	
	private int calcDistance(PlayerRecord r) { 
		return Math.abs(r.getAvenue() - this.getAvenue()) + Math.abs(r.getStreet() - this.getStreet());
	}
	
	private void runAway() {
		
	}
}
