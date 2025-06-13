package powerUps;
import becker.robots.*;
import players.*;
public abstract class EnhancedThing extends Thing{
	private static int nextID = 0;
	final private String type;
	final private int street, avenue;
	final private int ID;
	
	public EnhancedThing(City c, int street, int avenue, String type) {
		super(c,street,avenue);
		this.street = street;
		this.avenue = avenue;
		this.type = type;
		this.ID = nextID;
		nextID++;
	}
	
	public abstract void applyTo(Player p);
	
	public int getStreet() {
		return this.street;
	}
	
	public int getAvenue() {
		return this.avenue;
	}
	
	public String getType() {
		return this.type;
	}
}
