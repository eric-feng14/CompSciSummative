package app;
import players.*;
import becker.robots.*;

/**
 * A friendly game of tag
 * @author Eric, Felix, and Richard
 * @version 5/26/2025
 */
public class Main {
	public static void main(String[] args) {
		City city = new City(13, 24);
		

		
		Attacker attacker = new Attacker(city, 0, 0, Direction.EAST);
		String nameOfClass = Player.getPlayerRecord(0).getPlayer().getClass().getSimpleName();
		System.out.println(nameOfClass);
	}
}
