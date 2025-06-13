package powerUps;
import java.awt.Color;
import players.*;
import app.Main;
import becker.robots.*;
/**
 * We should probably add this to the abstract player class
 * this power up would affect how far a player can move without needing to stop and rest
 * @author 14eri
 *
 */
public class StaminaPowerUp extends EnhancedThing{
	private final static int staminaBoost = 5; //randomized later?
	
	public StaminaPowerUp(City c, int s, int a) {
		super(c,s,a,"Stamina");
		this.setColor(Color.cyan);
	}
	
	public void applyTo(Player p) {
		p.setStamina(p.getStamina() + staminaBoost);
	}
}
