package playerMods;
import players.*;

/**
 * Record for all players that are threats
 * @author Richard
 * @version 6/7/2025
 */
public class ThreatRecord extends PlayerRecord{
	private int predictedProximity;
	
	/**
	 * Initializes records for threats
	 * @param TYPE - type of player
	 * @param PLAYER_ID - player id
	 * @param street - street of city
	 * @param avenue - avenue of city
	 * @param speed - determined speed of player
	 * @param isDefeated - defeated
	 * @param predicted - predicted range after next move
	 */
	public ThreatRecord(String TYPE, int PLAYER_ID, int street, int avenue, int speed, boolean isDefeated, int predicted) {
		super(TYPE, PLAYER_ID, street, avenue, speed, isDefeated);
		this.predictedProximity = predicted;
		
	}
	
	/**
	 * Initializes variables according to player
	 * @param player - player
	 */
	public ThreatRecord(Player player) {
		super(player);
		this.predictedProximity = 1;
	}
	
	/**
	 * Turns PlayerRecord into ThreatRecord
	 * @param player - player record
	 */
	public ThreatRecord(PlayerRecord player) {
		super(player.getTYPE(), player.getPLAYER_ID(), player.getStreet(), player.getAvenue(), player.getSpeed(), player.isDefeated());
		this.predictedProximity = 1;
	}

	/**
	 * Gets the predicted proximity of threat
	 * @return - predictedProximity --> threat
	 */
	public int getPredictedProximity() {
		return predictedProximity;
	}

	/** Sets predicted range of threat in next move
	 * @param predictedProximity - determined proximity
	 */
	public void setPredictedProximity(int predictedProximity) {
		this.predictedProximity = predictedProximity;
	}
	
}
