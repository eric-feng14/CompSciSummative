package players;
import java.util.*;
import becker.robots.*;

public class Attacker extends Player{
	
	private int numOfEnemiesDefeated = 0;
	private static ArrayList<PlayerRecord> targetList = new ArrayList<PlayerRecord>();
	private PlayerRecord currentTarget;

	public Attacker(City city, int s, int a, Direction d) {
		super(city, s, a, d, 10, 5, "Attacker");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void performAction() {
		// TODO Auto-generated method stub
		sortPriority();
		
		this.moveTo(this.priorityList[0].getStreet(), this.priorityList[0].getAvenue());
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
	
	@Override
	protected void sortPriority() {
		//Bubble sort
		int len = this.priorityList.length;
		for (int i = 0; i < len; i++) {
			boolean swapped = false;
			for (int j = 0; j < len; j++) {
				int dist1, dist2;
				dist1 = Math.abs(this.priorityList[0].getAvenue() - this.getAvenue()) + Math.abs(this.priorityList[0].getStreet() - this.getStreet());
				dist2 = Math.abs(this.priorityList[1].getAvenue() - this.getAvenue()) + Math.abs(this.priorityList[1].getStreet() - this.getStreet());
				if (dist1 > dist2) {
					PlayerRecord temp = this.priorityList[0];
					this.priorityList[0] = this.priorityList[1];
					this.priorityList[1] = temp;
					swapped = true;
				}
			}
		}
	}
	
}