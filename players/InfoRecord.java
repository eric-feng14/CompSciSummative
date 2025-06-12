package players;

public class InfoRecord extends PlayerRecord {
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
}
