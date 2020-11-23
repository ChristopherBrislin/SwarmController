import java.awt.CardLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.annotations.MavlinkEnum;
import io.dronefleet.mavlink.annotations.MavlinkFieldInfo;
import io.dronefleet.mavlink.annotations.MavlinkMessageInfo;
import io.dronefleet.mavlink.ardupilotmega.EkfStatusReport;
import io.dronefleet.mavlink.ardupilotmega.Hwstatus;
import io.dronefleet.mavlink.common.CommandAck;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavSysStatusSensor;
import io.dronefleet.mavlink.common.SysStatus;
import io.dronefleet.mavlink.serialization.payload.reflection.ReflectionPayloadDeserializer;
import io.dronefleet.mavlink.util.EnumValue;
import io.dronefleet.mavlink.util.reflection.MavlinkReflection;

/**
 * 
 */

/**
 * Christopher Brislin 1 Nov 2020 SwarmController
 */
public class Drone implements ActionListener {

	// MavSystemID
	int droneID;

	// Packet drop calculations
	int dropCount;
	int lastCount = -1;
	JButton armDisarmButton;
	
	MavlinkMessage droneMessage;
	HashMap<String, String> messageMap = new HashMap<String, String>();
	String[] messageItems = new String[] {};
	
	GridLayout buttonLayout = new GridLayout(0,2);
	
	JPanel container = new JPanel(buttonLayout);
	JPanel cards = new JPanel(new CardLayout());
	JPanel status = new JPanel();
	
	JLabel statusLabel;

	public void buildDrone(int id) {
		this.droneID = id;
		buildInterface();

	}

	public int getDroneID() {
		return droneID;
	}

	public void buildInterface() {
		
		buttonLayout.setHgap(0);
		buttonLayout.setVgap(0);
		
		armDisarmButton = new JButton("Arm");
		JButton RTLButton = new JButton("RTL");
		JButton landButton = new JButton("Land");
		JButton takeoffButton = new JButton("Takeoff");
		JButton droneData = new JButton("Data");
		JButton controls = new JButton("Controls");
		
		statusLabel = new JLabel("");

		armDisarmButton.addActionListener(this);
		RTLButton.addActionListener(this);
		droneData.addActionListener(this);
		controls.addActionListener(this);

		container.add(armDisarmButton);
		container.add(RTLButton);
		container.add(landButton);
		container.add(takeoffButton);
		container.add(droneData);
		
		status.add(statusLabel);
		status.add(controls);
		
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
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		for(String name : messageMap.keySet()) {
			System.out.println(name.toString());
			JLabel param = new JLabel(name.toString());
			container.add(param);
			
		}

		addItem(container);
	}

	public void calculatePacketDrop(int sequence) {
		if (sequence != (lastCount + 1)) {
			dropCount++;
		}
		lastCount = sequence;
		if (lastCount == 255)
			lastCount = -1; // Sequence wraparound
	}

	public void newMessage(MavlinkMessage<?> message) {
		this.droneMessage = message;
		calculatePacketDrop(message.getSequence());
		
		
		if(message.getPayload() instanceof Heartbeat) {
			Heartbeat hb = (Heartbeat)message.getPayload();
			statusLabel.setText(hb.systemStatus().entry().toString());
			
			
		}
		
		if(message.getPayload() instanceof SysStatus) {
			SysStatus ss = (SysStatus) message.getPayload();
			
		}
		
		if(message.getPayload() instanceof Hwstatus) {
			Hwstatus hs = (Hwstatus) message.getPayload();
			
		}
		
		if(message.getPayload() instanceof EkfStatusReport) {
			EkfStatusReport sr = (EkfStatusReport) message.getPayload();
			
		}
		if(message.getPayload() instanceof CommandAck) {
			
		}
		
		
		else {
			
		}
		
		
		/*
		Arrays.stream(message.getPayload().getClass().getDeclaredMethods())
				.filter(f -> f.isAnnotationPresent(MavlinkFieldInfo.class)).forEach(f -> {

					try {

						if (!messageMap.containsKey(f.getName())) {
							messageMap.put(f.getName(), f.invoke(message.getPayload()).toString());
						} else if(messageMap.containsKey(f.getName())) {
							messageMap.replace(f.getName(), f.invoke(message.getPayload()).toString());
						}
						

						//System.out.println(f.getName() + ": " + messageMap.get(f.getName()));

					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				});
				*/
		
		
	}
	
	public void armDrone(int target) {
		CommandLong longMessage = CommandLong.builder()
				.command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
				.confirmation(0)
				.param1(1)
				.param2(21196)
				.targetSystem(target)
				.targetComponent(0)
				.build();
		
		PortBuilder.sendMessage(longMessage, target);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CardLayout c1 = (CardLayout)(cards.getLayout());
		
		switch (e.getActionCommand()) {
		case ("Arm"):
			armDrone(droneID);
			armDisarmButton.setText("Disarm");
			break;
		case ("Disarm"):
			armDisarmButton.setText("Arm");
			break;
		case("Data"):
			c1.show(cards, "Status");
			break;
		case("Controls"):
			c1.show(cards, "Controls");
			break;
		}

	}

}
