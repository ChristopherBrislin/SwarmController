import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * 
 */

/**
 * Christopher Brislin
 * 31 Oct 2020
 * SwarmController
 */
public class Main {
	
	public static boolean DEBUG = false;
	public static boolean MAVLINK_DEBUG = false;
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Const.setScreenSize(screenSize);
		
		Interface gui = new Interface();
		gui.interfaceStart();
		
		

	}

}
