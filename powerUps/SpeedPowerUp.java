package powerUps;
import java.awt.Color;

import becker.robots.*;
import players.Player;
/**
 * Subclass of Enhanced Thing, a specific type of powerup
 * @author Eric Feng
 * @version Due Date: June 13 2025
 */
public class SpeedPowerUp extends EnhancedThing{
	private final static int speedBoost = 2;
	
	/**
	 * Constructor method for creating a speed powerup
	 * @param c is the city
	 * @param s is the street
	 * @param a is the avenue
	 */
	public SpeedPowerUp(City c, int s, int a) {
		super(c,s,a,"Speed");
		this.setColor(Color.MAGENTA);
	}
	
	/**
	 * Overridding the applyTo method to apply a specific effect, tailored to the current type of powerup
	 */
	public void applyTo(Player p) {
		p.setStamina(p.obtainSpeed() + speedBoost);
	}
}
