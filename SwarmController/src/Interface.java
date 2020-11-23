import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.fazecast.jSerialComm.SerialPort;

/**
 * 
 */

/**
 * Christopher Brislin
 * 1 Nov 2020
 * SwarmController
 */
public class Interface implements ActionListener{
	
	JComboBox<SerialPort> cb_ports = new JComboBox<SerialPort>();
	PortBuilder portBuilder;
	static JFrame frame;
	JPanel container;
	static JPanel droneContainer;
	static boolean closePort = false;
	Border border = BorderFactory.createLoweredBevelBorder();
	
	public void interfaceStart() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(Const.DEFAULT_MIN_SIZE);
		frame.add(entryPoint());
		
		frame.setVisible(true);
		
	}
	
	
	
	public Component entryPoint() {
		container = new JPanel(new BorderLayout());
		droneContainer = new JPanel(new GridLayout(3,0));
		droneContainer.setBorder(border);
		
		JPanel controlPanel = new JPanel(new FlowLayout());
		updatePorts();
		JButton b_openPort = new JButton("Open Port");
		b_openPort.addActionListener(this);
		JButton b_closePort = new JButton("Close Port");
		b_closePort.addActionListener(this);
		
		controlPanel.add(cb_ports);
		controlPanel.add(b_openPort);
		controlPanel.add(b_closePort);
		container.add(controlPanel, BorderLayout.SOUTH);
		container.add(droneContainer, BorderLayout.CENTER);
		return container;
	}
	
	static void addDrone(Component drone) {
		droneContainer.add(drone, BorderLayout.CENTER);
		droneContainer.revalidate();
		
	}
	
	public void updatePorts() {
		cb_ports.setModel(new DefaultComboBoxModel<SerialPort>(SerialPort.getCommPorts()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case("Open Port"):
			portBuilder = new PortBuilder((SerialPort)cb_ports.getSelectedItem());
			portBuilder.start();
			break;
		case("Close Port"):
			closePort = true;
			
			break;
		
		default:
			break;
		
		}
		
	}
	
	
	

}
