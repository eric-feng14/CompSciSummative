package players;
import becker.robots.*;

public class Medic extends Player{
	public Medic (City c, int s, int a, Direction d) {
		super(c, s, a, d, 3, 4, "Medic");
	}

	@Override
	protected void sortPriority() {
		// TODO Auto-generated method stub
		
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
