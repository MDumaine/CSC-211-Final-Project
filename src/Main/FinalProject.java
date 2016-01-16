package Main;

/**
 * The main driver for the program. It creates a new game object and then calls
 * its gameLoop() method to begin the game.
 * 
 * @author Mason Dumaine
 *
 */
public class FinalProject
{
	public static void main(String[] args)
	{
		SpaceInvaders game = new SpaceInvaders();
		game.gameLoop();
	}
}
