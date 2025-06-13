package powerUps;
import becker.robots.*;
import players.*;
public abstract class EnhancedThing extends Thing{
	final private String type;
	final private int street, avenue;
	
	public EnhancedThing(City c, int street, int avenue, String type) {
		super(c,street,avenue);
		this.street = street;
		this.avenue = avenue;
		this.type = type;
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
