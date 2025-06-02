package players;
import java.awt.Color;
import becker.robots.*;

public class Medic extends Player{
	PlayerRecord[] previousPriority;
	
	/**
	 * PlayerRecord constructor
	 * @param c - city
	 * @param s - street
	 * @param a - avenue
	 * @param d - direction
	 */
	public Medic (City c, int s, int a, Direction d) {
		super(c, s, a, d, 3, 4, "Medic");
		this.setColor(new Color(133, 248, 108));
		this.previousPriority = this.priorityList;
	}

	@Override
	protected void sortPriority() {
		
		for (int i = 0; i < priorityList.length; i++) {
			int smallestIndex = i;
			for (int j = i; i < priorityList.length; i++) {
				int street = this.priorityList[j].getStreet();
				int avenue = this.priorityList[j].getAvenue();
				if (street + avenue )
			}
		}
	}

	@Override
	protected void performAction() {
		// TODO Auto-generated method stub
		
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
