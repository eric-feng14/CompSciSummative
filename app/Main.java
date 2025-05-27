
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
		City city = new City();
		
		Wall [][] walls = new Wall[24][13];

		// creates horizontal walls
		for (int i = 0; i < walls.length; i ++) {
			walls[i][0] = new Wall(city, 0, i, Direction.NORTH);
			walls[i][walls[0].length - 1] = new Wall(city, walls[0].length - 1, i, Direction.SOUTH);
		}

		// creates vertical walls
		for (int i = 0; i < walls[0].length; i ++) {
			walls[0][i] = new Wall(city, i, 0, Direction.WEST);
			walls[walls.length - 1][i] = new Wall(city, i, walls.length - 1, Direction.EAST);
		}
		
		Attacker attacker = new Attacker(city, 0, 0, Direction.EAST);
		String nameOfClass = Player.getPlayerRecord(0).getPlayer().getClass().getSimpleName();
		System.out.println(nameOfClass);
	}
}
