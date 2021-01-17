import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.fazecast.jSerialComm.SerialPort;

/**
 * 
 */

/**
 * Christopher Brislin
 * 1 Nov 2020
 * SwarmController
 */
public class Interface implements ActionListener, PopupMenuListener{
	
	JComboBox<SerialPort> cb_ports = new JComboBox<SerialPort>();
	
	static JFrame frame;
	static JPanel container;
	static JPanel droneContainer;
	static boolean closePort = false;
	Border border = BorderFactory.createLoweredBevelBorder();
	JCheckBox debug = new JCheckBox("Debug", Main.DEBUG);
	JCheckBox mavlinkDebug = new JCheckBox("Mavlink Debug" , Main.MAVLINK_DEBUG);
	PortBuilder serialPort = new PortBuilder();
	JButton b_openPort;
	JButton updatePorts;
	ManagerInterface managerInterface = new ManagerInterface();
	
	
	public void interfaceStart() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(Const.DEFAULT_MIN_SIZE);
		frame.add(entryPoint());
		
		managerInterface.buildInterface();
		
		frame.setVisible(true);
		
	}
	
	
	
	public Component entryPoint() {
		
		cb_ports.addPopupMenuListener(this);
		container = new JPanel(new BorderLayout());
		
		droneContainer = new JPanel(new GridLayout(0,5));
		droneContainer.setBorder(border);
		
		JPanel controlPanel = new JPanel(new FlowLayout());
		updatePorts();
		b_openPort = new JButton("Open Port");
		b_openPort.addActionListener(this);
		updatePorts = new JButton("Refresh Ports");
		updatePorts.addActionListener(this);
		
		debug.addActionListener(this);
		mavlinkDebug.addActionListener(this);
		

		
		
		controlPanel.add(cb_ports);
		controlPanel.add(b_openPort);
		controlPanel.add(updatePorts);
		controlPanel.add(debug);
		controlPanel.add(mavlinkDebug);
		
		container.add(controlPanel, BorderLayout.SOUTH);
		container.add(droneContainer, BorderLayout.CENTER);
		return container;
	}
	
	static void addDrone(Component drone) {
		droneContainer.add(drone);
		droneContainer.revalidate();
		
	}
	
	static void removeDrone(Component drone) {
		droneContainer.remove(drone);
		droneContainer.revalidate();
		droneContainer.repaint();
	}
	
	static void addManager(Component panel) {
		container.add(panel, BorderLayout.EAST);
		container.revalidate();
	}
	
	public void updatePorts() {
		cb_ports.setModel(new DefaultComboBoxModel<SerialPort>(SerialPort.getCommPorts()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		
		switch(e.getActionCommand()) {
		
		
		case("Open Port"):
			
			//This needs to be changed - if the port is closed and re-opened, superflous threads are created. 
			serialPort.setPort((SerialPort)cb_ports.getSelectedItem());
			closePort = false;
			serialPort.openPort();
			if(serialPort.portOpen()) {
				b_openPort.setText("Close Port");
			}
			break;
		case("Close Port"):
			closePort = true;
			while(serialPort.portOpen()) {
				
			}
			if(!serialPort.portOpen()) {
				b_openPort.setText("Open Port");
			}
			//Remove all drones
			
			
			break;
			
		case("Debug"):
			Main.DEBUG = !Main.DEBUG;
			break;
			
		case("Mavlink Debug"):
			Main.MAVLINK_DEBUG = !Main.MAVLINK_DEBUG;
			break;
			
		case("Refresh Ports"):
			updatePorts();
			break;
		
		default:
			break;
		
		}
		
	}



	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		updatePorts();
		
	}



	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		
		
	}



	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
		
		
	}
	
	
	

}
