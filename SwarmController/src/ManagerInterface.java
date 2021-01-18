import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * 
 */

/**
 * Christopher Brislin
 * 17 Jan 2021
 * SwarmController
 */
public class ManagerInterface extends DroneManager implements ActionListener{
	
	JButton armAll = new JButton("Arm All");
	JButton landAll = new JButton("Land All");
	JButton takeoffAll = new JButton("Takeoff All");
	JButton rtlAll = new JButton("RTL All");
	
	JPanel controlContainer = new JPanel();
	JPanel container = new JPanel();
	
	JTextArea messageArea = new JTextArea();
	
	
	public void buildInterface() {
		armAll.addActionListener(this);
		landAll.addActionListener(this);
		takeoffAll.addActionListener(this);
		rtlAll.addActionListener(this);
		
		controlContainer.add(armAll);
		controlContainer.add(landAll);
		controlContainer.add(takeoffAll);
		controlContainer.add(rtlAll);
		
		controlContainer.setLayout(new GridLayout(2,0));
		
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		container.add(controlContainer);
		container.add(textArea());
		
		Interface.addManager(container);
	}
	
	public Component textArea() {
		messageArea.setEditable(false);
		return messageArea;
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch(e.getActionCommand()) {
		case("Arm All"):
			armAllDrones();
			break;
		case("Land All"):
			landAllDrones();
			break;
		case("Takeoff All"):
			takeoffAllDrones();
			break;
		case("RTL All"):
			rtlAllDrones();
			break;
		default:
			break;
		}
		
	}

}
