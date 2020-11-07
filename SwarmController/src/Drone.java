import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 
 */

/**
 * Christopher Brislin
 * 1 Nov 2020
 * SwarmController
 */
public class Drone implements ActionListener{
	
	int droneID;
	
	public void buildDrone(int id) {
		this.droneID = id;
		buildInterface();
		
	}
	
	public int getDroneID() {
		return droneID;
	}
	
	public void buildInterface() {
		JPanel container = new JPanel();
		JButton armButton = new JButton("Arm");
		JButton disarmButton = new JButton("Disarm");
		
		armButton.addActionListener(this);
		disarmButton.addActionListener(this);
		
		container.add(armButton);
		container.add(disarmButton);
		
		Interface.addDrone(container);
		System.out.println("Drone " + droneID + " built");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case("Arm"):
			System.out.println(droneID + " Armed");
			break;
		case("Disarm"):
			System.out.println(droneID + " Disarmed");
			break;
		}
		
	}
	

}
