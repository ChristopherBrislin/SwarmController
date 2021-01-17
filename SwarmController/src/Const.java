import java.awt.Color;
import java.awt.Dimension;

/**
 * 
 */

/**
 * Christopher Brislin
 * 1 Nov 2020
 * SwarmController
 */
public class Const {
	//
	static Dimension screenSize;
	
	//Launch Windo Size
	static Dimension DEFAULT_MIN_SIZE = new Dimension(1007,500);
	
	static int card_width =  (int) ((DEFAULT_MIN_SIZE.width - 7)*0.2);
	static int card_height = 75;
	
	static Dimension CARD_SIZE = new Dimension(card_width, card_height);
	
	//Interface Colour Scheme
	static final Color WARNING = new Color(245, 66, 66);
	static final Color CAUTION = new Color(252, 177, 3);
	static final Color OPTIMAL = new Color(34, 204, 0);
	
	public static void setScreenSize(Dimension size) {
		Const.screenSize = size;
		
		System.out.println(screenSize);
		
	}

}
