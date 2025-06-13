package powerUps;
import java.awt.Color;

import becker.robots.*;
import players.Player;
/**
 * Subclass of Enhanced Thing, a specific type of powerup
 * @author Eric Feng
 * @version Due Date: June 13 2025
 */
public class LuckPowerUp extends EnhancedThing{
	private final static int strengthBoost = 3, defenseBoost = 4;
	
	/**
	 * Constructor method for creating a luck powerup
	 * @param c is the city
	 * @param s is the street
	 * @param a is the avenue
	 */
	public LuckPowerUp(City c, int s, int a) {
		super(c,s,a,"Luck");
		this.setColor(Color.orange);
	}
	
	/**
	 * Overridding the applyTo method to apply a specific effect, tailored to the current type of powerup
	 */
	public void applyTo(Player p) {
		if (p.getTYPE() == "Attacker") {
			p.setStrength(p.getStrength() + strengthBoost);
		} else if (p.getTYPE() == "Runner") {
			p.setDefense(p.getDefense() + defenseBoost);
		} else { //haven't added healing attributes to Player
			
		}
	}
}
