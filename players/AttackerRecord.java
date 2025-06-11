package players;

public class AttackerRecord extends PlayerRecord{
	
	/*
	 * Attackers will learn about what? 
	 * Will each attacker record have an array about the other players? 
	 * What the robots will learn is the same thing, so why should we have multiple AttackerRecords when they hold the same info?
	 * If we just kept a nonAttacker class for every player that isn't an attacker, we would save memory no?
	 */
	
	public AttackerRecord(String TYPE, int PLAYER_ID, int street, int avenue, int speed, boolean isDefeated, int hp, PlayerRecord currentTarget) {
		super(TYPE, PLAYER_ID, street, avenue, speed, isDefeated, hp, currentTarget);
	}
	
	
}
