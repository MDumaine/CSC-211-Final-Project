package Main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This class gets gets key inputs from the user, and modifies its data members
 * accordingly.
 * 
 * @author Mason Dumaine
 *
 */
public class KeyBoard extends KeyAdapter
{
	// Boolean values for whether or not the key is pressed.
	public boolean left = false, right = false, fire = false, go = false;

	/**
	 * This checks the key event for valid keypresses and sets the corresponding
	 * data member.
	 */
	public void keyPressed(KeyEvent event)
	{
		if (event.getKeyCode() == 65)
		{
			left = true;
		}

		else if (event.getKeyCode() == 68)
		{
			right = true;
		}

		else if (event.getKeyCode() == 82)
		{
			go = true;
		}

		else if (event.getKeyCode() == 32)
		{
			fire = true;
		}
	}

	/**
	 * This ensures that when the key is released, data members reflect that
	 * change.
	 */
	public void keyReleased(KeyEvent event)
	{
		if (event.getKeyCode() == 65)
		{
			left = false;
		}

		else if (event.getKeyCode() == 68)
		{
			right = false;
		}

		else if (event.getKeyCode() == 82)
		{
			go = false;
		}

		else if (event.getKeyCode() == 32)
		{
			fire = false;
		}
	}
}
