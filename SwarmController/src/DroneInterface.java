import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	

	String[] messageItems = new String[] {};
	
	
	//This needs to change to GridBag at some point
	GridLayout buttonLayout = new GridLayout(0,2);
	JPanel container = new JPanel(buttonLayout);
	
	
	JPanel cards = new JPanel(new CardLayout());
	JPanel status = new JPanel();
	JPanel parentContainer = new JPanel();
	
	//To be moved to Const and resized dynamically
	Dimension buttonSize = new Dimension(150,50);
	
	
	JLabel statusLabel;
	JLabel packetDrop;
	JLabel indicatorLabel;
	JPanel indicator = new JPanel();
	Color indColor = new Color(245, 66, 66);
	
	
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
		indicator.setBackground(indColor);

		armDisarmButton = new JButton("Arm");
		JButton RTLButton = new JButton("RTL");
		JButton landButton = new JButton("Land");
		takeoffLandButton = new JButton("Takeoff");
		JButton droneData = new JButton("Data");
		JButton controls = new JButton("Controls");
		indicatorLabel = new JLabel("");
		

		statusLabel = new JLabel("");
		packetDrop = new JLabel("");

		armDisarmButton.addActionListener(this);
		armDisarmButton.setPreferredSize(buttonSize);
		takeoffLandButton.addActionListener(this);
		takeoffLandButton.setPreferredSize(buttonSize);
		RTLButton.addActionListener(this);
		RTLButton.setPreferredSize(buttonSize);
		droneData.addActionListener(this);
		droneData.setPreferredSize(buttonSize);
		controls.addActionListener(this);
		controls.setPreferredSize(buttonSize);
		indicator.setPreferredSize(buttonSize);
		indicator.add(indicatorLabel);

		container.setBorder(border);

		container.add(armDisarmButton);
		container.add(RTLButton);
		container.add(landButton);
		container.add(takeoffLandButton);
		container.add(droneData);
		container.add(indicator);

		status.add(statusLabel);
		status.add(packetDrop);
		status.add(controls);
		status.setBorder(border);
		
		status.setSize(new Dimension(300,300));
		
		container.setSize(new Dimension(300,300));

		cards.add(container, "Controls");
		cards.add(status, "Status");
		
		
		
		cards.setPreferredSize(new Dimension(Const.CARD_SIZE));
		parentContainer.add(cards);
		
		//System.out.println(cards.getSize());

		Interface.addDrone(parentContainer);
		if(Main.DEBUG)System.out.println("Drone " + droneID + " built");

	}
	
	public void removeDrone() {
		Interface.removeDrone(parentContainer);
	}

	public void addItem(JPanel item) {
		container.add(item);
		container.revalidate();
		Interface.addDrone(container);

	}
	
	public void setIndicator(Color col) {
		indicator.setBackground(col);
	}
	
	public void setIndicatorLabel(String label) {
		indicatorLabel.setText(label);
	}
	
	public void setArmButtonText(boolean isArmed) {
		if(isArmed) {
			armDisarmButton.setText("Disarm");
		}
		else if(!isArmed){
			armDisarmButton.setText("Arm");
		}
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
			}
			
			break;
		case ("Disarm"):
			if(isArmed) {
			disarmDrone(droneID);
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
