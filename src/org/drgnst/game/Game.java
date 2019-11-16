package org.drgnst.game;

import static java.awt.event.KeyEvent.*;


import org.drgnst.game.Level.Level;
import org.drgnst.game.entities.Player;

/**
 * @author Sopiro
 *
 * 2015. 12. 14. ¿ÀÈÄ 5:31:36
 */
public class Game
{
	public Level level;
	public Player player;
	public int time;

	public Game()
	{
		level = Level.loadLevel("level0");
		player = new Player(this);
	}

	public void update(boolean[] keys)
	{
		time++;

		boolean up = keys[VK_W];
		boolean down = keys[VK_S];
		boolean left = keys[VK_A];
		boolean right = keys[VK_D];
		boolean turnLeft = keys[VK_Q];
		boolean turnRight = keys[VK_E];
		boolean space = keys[VK_SPACE];
		
		player.update(up, down, left, right, turnLeft, turnRight, space);
	}
}
