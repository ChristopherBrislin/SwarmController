import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.annotations.MavlinkFieldInfo;

/**
 * 
 */

/**
 * Christopher Brislin
 * 1 Nov 2020
 * SwarmController
 */
public class Drone implements ActionListener{
	
	//MavSystemID
	int droneID;
	
	//Packet drop calculations
	int dropCount;
	int lastCount = -1;
	MavlinkMessage droneMessage;
	HashMap<String, String> messageMap = new HashMap<String, String>();
	
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
		container.add(messageInterface());
		
		Interface.addDrone(container);
		System.out.println("Drone " + droneID + " built");
	}
	
	public Container messageInterface() {
		JPanel container = new JPanel();
		
		
		return container;
	}
	
	public void calculatePacketDrop(int sequence) {
		if(sequence != (lastCount+1)) {
			dropCount++;
		}
		lastCount = sequence;
		if(lastCount == 255)lastCount = -1; //Sequence wraparound
	}
	
	public void newMessage(MavlinkMessage message) {
		this.droneMessage = message;
		calculatePacketDrop(message.getSequence());
		//System.out.println(droneID + "\t" + dropCount+"\t"+ lastCount + "\t"+ message.getPayload());
		//System.out.print("\n"+message.getPayload().getClass().toString());
		Arrays.stream(message.getPayload().getClass().getDeclaredMethods())
		.filter(f -> f.isAnnotationPresent(MavlinkFieldInfo.class))
		.forEach(f -> {
			
				
				try {
					
					
					
					if(!messageMap.containsKey(f.getName())) {
						messageMap.put(f.getName(), f.invoke(message.getPayload()).toString());
					}else {
						messageMap.replace(f.getName(), f.invoke(message.getPayload()).toString());
					}
					
					System.out.println(f.getName() + ": " + messageMap.get(f.getName()));
					
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
