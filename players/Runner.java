package players;
import java.awt.Color;
import becker.robots.*;

public class Runner extends Player{

	private boolean isCaught = false;
	private int agility;

	public Runner(City c, int s, int a, Direction d) {
		super(c, s, a, d, 5, 5, "Runner");
		this.setColor(Color.BLUE);
		this.agility = 5;
	}

	@Override
	protected void sortPriority() {
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
	private int calcDistance(PlayerRecord r) { 
		return Math.abs(r.getAvenue() - this.getAvenue()) + Math.abs(r.getStreet() - this.getStreet());
	}

	@Override
	protected void performAction() {
		
	}

	@Override
	protected Direction getNextDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getNextMovement() {
		// TODO Auto-generated method stub
		return 0;
	}
}
