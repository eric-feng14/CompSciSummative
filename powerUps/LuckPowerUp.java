package powerUps;
import java.awt.Color;

import becker.robots.*;
import players.Player;
/**
 * e.g. if you're a runner, you're much more likely to take less dmg
 * e.g. if you're a medic, you're more like to heal more health
 * e.g. if you're an attacker, you're more likely to deal more dmg
 * @author 14eri
 *
 */
public class LuckPowerUp extends EnhancedThing{
	private final static int strengthBoost = 3, defenseBoost = 4;
	
	public LuckPowerUp(City c, int s, int a) {
		super(c,s,a,"Luck");
		this.setColor(Color.orange);
	}
	
	public void applyTo(Player p) {
		if (p.getTYPE() == "Attacker") {
			p.setStrength(p.getStrength() + strengthBoost);
		} else if (p.getTYPE() == "Runner") {
			p.setDefense(p.getDefense() + defenseBoost);
		} else { //haven't added healing attributes to Player
			
		}
	}
}
