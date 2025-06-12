package players;

public class InfoRecords extends PlayerRecord {
	int defense, strength;

	public InfoRecords(Player player, int d, int s) {
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
