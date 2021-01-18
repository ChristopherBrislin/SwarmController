import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.fazecast.jSerialComm.SerialPort;

/**
 * 
 */

/**
 * Christopher Brislin 1 Nov 2020 SwarmController
 */
public class Interface implements ActionListener, PopupMenuListener {

	CardLayout card;
	Container c;

	String tcpConnectionString;
	String udpConnectionString;

	SerialPort selectedPort;
	int selectedBaud;

	static JFrame frame;
	static JPanel container;
	static JPanel droneContainer;
	static boolean closePort = false;
	Border border = BorderFactory.createLoweredBevelBorder();

	JCheckBox debug = new JCheckBox("Debug");
	JCheckBox mavlinkDebug = new JCheckBox("Mavlink Debug");

	Connection connection;
	JButton b_openPort;
	JButton updatePorts;
	ManagerInterface managerInterface = new ManagerInterface();

	JMenu menu, help;
	JMenuItem settings;

	JPanel settingContainer;

	// Connection Settings:
	JButton conButton;
	JComboBox<String> conType;
	String type;
	Boolean connectionOpen = false;

	// TCP Settings:

	JTextField tcpAddress;
	JTextField tcpPort;

	// UDP Settings:

	JTextField udpAddress;
	JTextField udpPort;

	// Serial Settings:
	JComboBox<SerialPort> cb_ports = new JComboBox<SerialPort>();
	JComboBox<Integer> baud_rate;

	public void interfaceStart() {

		card = new CardLayout();

		c = new Container();
		c.setLayout(card);

		JMenuBar menuBar = new JMenuBar();
		menu = new JMenu("Settings");
		settings = new JMenuItem("Debug Settings");
		help = new JMenu("Help");

		settings.addActionListener(this);
		help.addActionListener(this);

		menu.add(settings);

		menuBar.add(menu);
		menuBar.add(help);

		c.add(entryPoint(), "default");

		frame = new JFrame();
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(Const.DEFAULT_MIN_SIZE);
		frame.add(entryPoint());

		managerInterface.buildInterface();

		frame.setVisible(true);

	}

	public Component serialSettings() {
		Integer[] baudRates = new Integer[] { 1200, 2400, 4800, 9600, 18200, 36400, 57600, 115200, 230400, 460800,
				921600, 1382400 };

		baud_rate = new JComboBox<Integer>();
		baud_rate.setModel(new DefaultComboBoxModel<Integer>(baudRates));

		JPanel container = new JPanel();

		JLabel serial_port = new JLabel("Serial Port: ");
		JLabel baud_label = new JLabel("Baud Rate: ");

		baud_rate.setSelectedIndex(6);

		updatePorts();
		container.add(serial_port);
		container.add(cb_ports);
		container.add(baud_label);
		container.add(baud_rate);

		return container;
	}

	public Component tcpSettings() {
		JPanel container = new JPanel();

		JLabel tcp = new JLabel("TCP ");
		JLabel ip_label = new JLabel("Address: ");

		tcpAddress = new JTextField("127.0.0.1", 6);

		JLabel port_label = new JLabel("Port: ");
		tcpPort = new JTextField("3333", 3);

		container.add(tcp);
		container.add(ip_label);
		container.add(tcpAddress);
		container.add(port_label);
		container.add(tcpPort);

		return container;
	}

	public Component udpSettings() {
		JPanel container = new JPanel();

		JLabel udp = new JLabel("UDP ");
		JLabel ip_label = new JLabel("Address: ");
		udpAddress = new JTextField("127.0.0.1", 6);

		JLabel port_label = new JLabel("Port: ");
		udpPort = new JTextField("3333", 3);

		container.add(udp);
		container.add(ip_label);
		container.add(udpAddress);
		container.add(port_label);
		container.add(udpPort);

		return container;
	}

	public Component entryPoint() {

		cb_ports.addPopupMenuListener(this);
		container = new JPanel(new BorderLayout());

		droneContainer = new JPanel(new GridLayout(0, 5));
		droneContainer.setBorder(border);

		JPanel controlPanel = new JPanel(new FlowLayout());
		updatePorts();

		debug.addActionListener(this);
		mavlinkDebug.addActionListener(this);

		controlPanel.add(connectionSettings());

		container.add(controlPanel, BorderLayout.SOUTH);
		container.add(droneContainer, BorderLayout.CENTER);
		return container;
	}

	public Component connectionSettings() {

		JPanel typeSettings = new JPanel();
		CardLayout settingCard = new CardLayout();
		conButton = new JButton("Connect");

		conButton.addActionListener(this);

		typeSettings.setLayout(settingCard);

		typeSettings.add(udpSettings(), "udpSettings");
		typeSettings.add(tcpSettings(), "tcpSettings");
		typeSettings.add(serialSettings(), "serial");

		String[] conTypeList = new String[] { "Serial", "TCP", "UDP" };
		conType = new JComboBox<String>();
		conType.setModel(new DefaultComboBoxModel<String>(conTypeList));

		JPanel connectionContainer = new JPanel();
		JLabel conTypeLabel = new JLabel("Connection Type: ");

		connectionContainer.add(conTypeLabel);
		connectionContainer.add(conType);
		connectionContainer.add(typeSettings);

		conType.addActionListener(e -> {
			String selection = (String) conType.getSelectedItem();
			switch (selection) {
			case ("UDP"):

				settingCard.show(typeSettings, "udpSettings");
				connectionContainer.add(conButton);
				connectionContainer.revalidate();

				break;

			case ("TCP"):

				settingCard.show(typeSettings, "tcpSettings");
				connectionContainer.add(conButton);
				connectionContainer.revalidate();
				break;

			case ("Serial"):

				settingCard.show(typeSettings, "serial");
				connectionContainer.add(conButton);
				connectionContainer.revalidate();

				break;

			default:
				settingCard.show(typeSettings, "serial");
				connectionContainer.add(conButton);
				connectionContainer.revalidate();
				break;
			}
		});
		conType.setSelectedIndex(0);

		return connectionContainer;
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

	public void openConnection(String type) {
		connectionOpen = true;
		closePort = false;
		switch (type) {
		case ("Serial"):

			connection = new SerialConnection((SerialPort) cb_ports.getSelectedItem(),
					(int) baud_rate.getSelectedItem());
			
			break;
		case ("UDP"):
			// TODO
			break;

		case ("TCP"):

			connection = new SocketConnection(Integer.parseInt(tcpPort.getText()));

			break;

		default:
			break;
		}
		if (connection.isOpen()) {
			
			conButton.setText("Disconnect");
		}else {
			//Connection not opened..
		}

	}

	public void closeConnection() {
		connectionOpen = false;
		closePort = true;
		while(connection.isOpen()) {
			
		}
		conButton.setText("Connect");
	}

	public void updatePorts() {
		cb_ports.setModel(new DefaultComboBoxModel<SerialPort>(SerialPort.getCommPorts()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {

		case ("Connect"):
			type = (String) conType.getSelectedItem();
			if (!connectionOpen) {
				openConnection(type);
			} else {
				// Connection open
			}

			break;
		case ("Disconnect"):
			if (connection.isOpen()) {
				closeConnection();
			}else {
				//Connection already closed
			}
			break;

		case ("Debug"):
			
			Main.DEBUG = debug.isSelected();
		System.out.println(Main.DEBUG);
			break;

		case ("Mavlink Debug"):
			Main.MAVLINK_DEBUG = mavlinkDebug.isSelected();
		System.out.println(Main.MAVLINK_DEBUG);
			break;

		case ("Refresh Ports"):
			updatePorts();
			break;
		case ("Debug Settings"):

			// JOptionPane with debug settings

			Object[] o = { debug, mavlinkDebug };
			JOptionPane.showMessageDialog(frame, o, "Debug Settings", JOptionPane.DEFAULT_OPTION);
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
