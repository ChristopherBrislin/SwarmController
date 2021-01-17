import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

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
	
	JPanel container = new JPanel();
	
	public void buildInterface() {
		armAll.addActionListener(this);
		landAll.addActionListener(this);
		takeoffAll.addActionListener(this);
		rtlAll.addActionListener(this);
		
		container.add(armAll);
		container.add(landAll);
		container.add(takeoffAll);
		container.add(rtlAll);
		
		Interface.addManager(container);
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
