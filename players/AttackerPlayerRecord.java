package players;

public class AttackerPlayerRecord extends PlayerRecord{
	
	//Instance variables for what they have learned over time
	
	/**
	 * Constructor of AttackerPlayerRecord
	 * @param TYPE TYPE = type of record e.g "Attacker
	 * @param PLAYER_ID
	 * @param street
	 * @param avenue
	 * @param speed
	 * @param isDefeated
	 * @param hp
	 * @param currentTarget
	 */
	public AttackerPlayerRecord(String TYPE, int PLAYER_ID, int street, int avenue, int speed, boolean isDefeated, int hp, PlayerRecord currentTarget) {
		super("Attacker", PLAYER_ID, street, avenue, speed, isDefeated, hp, currentTarget);
		
	}
	
	/**
	 * Player constructor
	 * @param player - Player
	 */
	public AttackerPlayerRecord(PlayerRecord player) {
		this("Attacker", player.getPLAYER_ID(), player.getStreet(), player.getAvenue(), 0, player.isDefeated(), player.getHP(), player.getCurrentTarget());
	}
}
