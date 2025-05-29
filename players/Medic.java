package players;
import java.awt.Color;
import becker.robots.*;

public class Medic extends Player{
	public Medic (City c, int s, int a, Direction d) {
		super(c, s, a, d, 3, 4, "Medic");
		this.setColor(new Color(133, 248, 108));
	}

	@Override
	protected void sortPriority() {
		PlayerRecord[] newList = this.priorityList;
		for (int i = 0; i < newList.length; i++) {
			for (int j = i; i < newList.length; i++) {
				if ()
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
