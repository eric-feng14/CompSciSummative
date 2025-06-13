package powerUps;
import java.awt.Color;

import becker.robots.*;
import players.Player;
/**
 * as the name suggests, temporarily increases the speed by a specific amount
 * @author 14eri
 *
 */
public class SpeedPowerUp extends EnhancedThing{
	private final static int speedBoost = 2;
	
	public SpeedPowerUp(City c, int s, int a) {
		super(c,s,a,"Speed");
		this.setColor(Color.MAGENTA);
	}
	public void applyTo(Player p) {
		p.setStamina(p.obtainSpeed() + speedBoost);
	}
}
