import java.util.EnumSet;
import java.util.stream.Stream;

import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.ardupilotmega.EkfStatusReport;
import io.dronefleet.mavlink.ardupilotmega.Hwstatus;
import io.dronefleet.mavlink.common.CommandAck;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavSysStatusSensor;
import io.dronefleet.mavlink.common.SysStatus;

/**
 * 
 */

/**
 * Christopher Brislin 1 Nov 2020 SwarmController
 */
public class Drone {

	// MavSystemID
	int droneID;

	// Packet drop calculations
	int dropCount;
	int lastCount = -1;
	
	
	MavlinkMessage<?> droneMessage;
	DroneInterface droneInterface;
	
	boolean isArmed = false;
	

	public void buildDrone(int id) {
		this.droneID = id;
		
		droneInterface = new DroneInterface(droneID);
		droneInterface.buildInterface();

	}

	public int getDroneID() {
		return droneID;
	}

	public void calculatePacketDrop(int sequence) {
		if (sequence != (lastCount + 1)) {
			dropCount++;
		}
		lastCount = sequence;
		if (lastCount == 255)
			lastCount = -1; // Sequence wraparound
	}
	
	public void baseModeFlags(Heartbeat hbmsg){
		int input = hbmsg.baseMode().value();
		
		for(int k = 1; k <= 8; k++ ) {
			switch(input & (1 << k)) {
			case(1):
				//Custom mode enabled
				break;
			case(2):
				//Test mode enabled
				break;
			case(4):
				//Autonomous mode enabled
				break;
			case(8):
				//Guided enabled - this includes auto mission waypoints
				break;
			case(16):
				//Stabilized enabled
				break;
			case(32):
				//HIL enabled - simulation
				break;
			case(64):
				//Manual input enabled
				break;
			case(128):
				//System.out.println("ARMED!!");
				if(!isArmed)
					isArmed = true;
				
				//Armed state
				break;
			}
		}
		
		for(int k = 1; k <= 8; k++ ) {
			switch(~input & (1 << k)) {
			case(1):
				//Custom mode enabled
				break;
			case(2):
				//Test mode enabled
				break;
			case(4):
				//Autonomous mode enabled
				break;
			case(8):
				//Guided enabled - this includes auto mission waypoints
				break;
			case(16):
				//Stabilized enabled
				break;
			case(32):
				//HIL enabled - simulation
				break;
			case(64):
				//Manual input enabled
				break;
			case(128):
				//System.out.println("ARMED!!");
				if(isArmed)
					isArmed = false;
				
				//Armed state
				break;
			}
		}
		
		
	}
	
	public void sysStatusFlags(SysStatus sysmsg) {
		
		//Compares the bitmask with all enumerated sensors. 
		EnumSet.allOf(MavSysStatusSensor.class).forEach(sensor -> {
			System.out.print(sensor);
			
			if(sysmsg.onboardControlSensorsPresent().flagsEnabled(sensor)) {
				System.out.print(" present ");
			}
			
			if(sysmsg.onboardControlSensorsEnabled().flagsEnabled(sensor)) {
				System.out.print(" enabled");
			}
			
			if(sysmsg.onboardControlSensorsHealth().flagsEnabled(sensor)) {
				System.out.print(" healthy ");
			}
			
			System.out.print("\n");
			
		});
	}
	
	public void newMessage(MavlinkMessage<?> message) {
		this.droneMessage = message;
		calculatePacketDrop(message.getSequence());
		
		
		
		if(message.getPayload() instanceof Heartbeat) {
			Heartbeat hb = (Heartbeat)message.getPayload();
			
			droneInterface.setStatusLabel(hb.systemStatus().entry().toString());
			baseModeFlags(hb);
			
			
		}
		
		if(message.getPayload() instanceof SysStatus) {
			SysStatus ss = (SysStatus) message.getPayload();
			ss.batteryRemaining();
			sysStatusFlags(ss);
			
			
		}
		
		if(message.getPayload() instanceof Hwstatus) {
			Hwstatus hs = (Hwstatus) message.getPayload();
			hs.i2cerr();
			
		}
		
		if(message.getPayload() instanceof EkfStatusReport) {
			EkfStatusReport sr = (EkfStatusReport) message.getPayload();
			sr.compassVariance();
			
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
		
		PortBuilder.sendMessage(longMessage);
	}
	
	public void disarmDrone() {
		
	}
	
	

	

}
