import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
	PortBuilder portBuilder = new PortBuilder();
	static JFrame frame;
	static JPanel container;
	
	public void interfaceStart() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(Const.DEFAULT_MIN_SIZE);
		frame.add(entryPoint());
		
		frame.setVisible(true);
		
	}
	
	
	
	public Component entryPoint() {
		container = new JPanel(new BorderLayout());
		JPanel controlPanel = new JPanel(new FlowLayout());
		updatePorts();
		JButton b_openPort = new JButton("Open Port");
		b_openPort.addActionListener(this);
		JButton b_closePort = new JButton("Close Port");
		b_closePort.addActionListener(this);
		
		controlPanel.add(cb_ports);
		controlPanel.add(b_openPort);
		controlPanel.add(b_closePort);
		container.add(controlPanel, BorderLayout.EAST);
		return container;
	}
	
	static void addDrone(Component drone) {
		container.add(drone, BorderLayout.CENTER);
		container.revalidate();
		
	}
	
	public void updatePorts() {
		cb_ports.setModel(new DefaultComboBoxModel<SerialPort>(portBuilder.getAvailablePorts()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case("Open Port"):
			portBuilder.buildPort((SerialPort)cb_ports.getSelectedItem());
			break;
		case("Close Port"):
			System.out.println("trying to close port");
			portBuilder.closePort();
			break;
		
		default:
			break;
		
		}
		
	}
	
	
	

}
