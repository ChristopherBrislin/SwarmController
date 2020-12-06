import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * 
 */

/**
 * Christopher Brislin 28 Nov 2020 SwarmController
 */
public class DroneInterface extends Drone implements ActionListener {
	
	JButton armDisarmButton;
	JButton takeoffLandButton;

	Border border = BorderFactory.createLineBorder(Color.black, 1, false);
	
	HashMap<String, String> messageMap = new HashMap<String, String>();
	String[] messageItems = new String[] {};
	
	GridLayout buttonLayout = new GridLayout(0,2);
	
	JPanel container = new JPanel(buttonLayout);
	JPanel cards = new JPanel(new CardLayout());
	JPanel status = new JPanel();
	
	JLabel statusLabel;
	JLabel packetDrop;
	
	int droneID;
	
	/**
	 * @param droneID2
	 */
	public DroneInterface(int droneID2) {
		droneID = droneID2;
	}

	

	public void buildInterface() {

		buttonLayout.setHgap(0);
		buttonLayout.setVgap(0);

		armDisarmButton = new JButton("Arm");
		JButton RTLButton = new JButton("RTL");
		JButton landButton = new JButton("Land");
		takeoffLandButton = new JButton("Takeoff");
		JButton droneData = new JButton("Data");
		JButton controls = new JButton("Controls");

		statusLabel = new JLabel("");
		packetDrop = new JLabel("");

		armDisarmButton.addActionListener(this);
		takeoffLandButton.addActionListener(this);
		RTLButton.addActionListener(this);
		droneData.addActionListener(this);
		controls.addActionListener(this);

		container.setBorder(border);

		container.add(armDisarmButton);
		container.add(RTLButton);
		container.add(landButton);
		container.add(takeoffLandButton);
		container.add(droneData);

		status.add(statusLabel);
		status.add(packetDrop);
		status.add(controls);
		status.setBorder(border);

		cards.add(container, "Controls");
		cards.add(status, "Status");

		Interface.addDrone(cards);
		System.out.println("Drone " + droneID + " built");

	}

	public void addItem(JPanel item) {
		container.add(item);
		container.revalidate();
		Interface.addDrone(container);

	}
	
	public void messageInterface() {
		
	}
	public void setStatusLabel(String labelMsg) {
		statusLabel.setText(labelMsg);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		CardLayout c1 = (CardLayout)(cards.getLayout());
		
		switch (e.getActionCommand()) {
		case ("Arm"):
			if(!isArmed) {
			armDrone(droneID);
			armDisarmButton.setText("Disarm");
			}
			
			break;
		case ("Disarm"):
			if(isArmed) {
			disarmDrone(droneID);
			armDisarmButton.setText("Arm");
			}
			break;
		case("Data"):
			c1.show(cards, "Status");
			break;
		case("Controls"):
			c1.show(cards, "Controls");
			break;
			
		case("RTL"):
			if(isArmed) {
				if(inFlight) {
					rtlDrone(droneID);
				}//On the ground
				
			}//Not armed 
			
			break;
		
		case("Takeoff"):
			if(isArmed) {
				if(!inFlight) {
			takeoffDrone(droneID);
			takeoffLandButton.setText("Land");
				} //Already flying
			} //Not armed
			
			break;
		
		case("Land"):
			
			if(inFlight) {
				landDrone(droneID);
				takeoffLandButton.setText("Takeoff");
			}
			
			break;
		}
		
		

	}

}
