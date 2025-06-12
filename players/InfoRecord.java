package players;

public class InfoRecord extends PlayerRecord {
	private final static int NORMAL_HIT = 20, CRITICAL_HIT = 40, KNOCKOUT = 100;

	int defense, strength;

	public InfoRecord(Player player, int d, int s) {
		super(player);
		this.defense = d;
		this.strength = s;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public static int getNormalHit() {
		return NORMAL_HIT;
	}

	public static int getCriticalHit() {
		return CRITICAL_HIT;
	}

	public static int getKnockout() {
		return KNOCKOUT;
	}
}
