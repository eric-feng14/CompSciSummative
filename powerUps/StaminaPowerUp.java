package powerUps;
import java.awt.Color;
import players.*;
import app.Main;
import becker.robots.*;
/**
 * Subclass of Enhanced Thing, a specific type of powerup
 * @author Eric Feng
 * @version Due Date: June 13 2025
 */
public class StaminaPowerUp extends EnhancedThing{
	private final static int staminaBoost = 5; //randomized later?
	
	/**
	 * Constructor method for creating a stamina powerup
	 * @param c is the city
	 * @param s is the street
	 * @param a is the avenue
	 */
	public StaminaPowerUp(City c, int s, int a) {
		super(c,s,a,"Stamina");
		this.setColor(Color.cyan);
	}
	
	/**
	 * Overridding the applyTo method to apply a specific effect, tailored to the current type of powerup
	 */
	public void applyTo(Player p) {
		p.setStamina(p.getStamina() + staminaBoost);
	}
}
