import java.util.EnumSet;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.ardupilotmega.EkfStatusReport;
import io.dronefleet.mavlink.ardupilotmega.Hwstatus;
import io.dronefleet.mavlink.common.CommandAck;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.ExtendedSysState;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavLandedState;
import io.dronefleet.mavlink.common.MavModeFlag;
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
	boolean inFlight = false;
	

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
		
		EnumSet.allOf(MavModeFlag.class).forEach(mode -> {
			
			
			if(hbmsg.baseMode().flagsEnabled(mode)) {
				//System.out.print(mode);
				//System.out.print(" Active ");
			}
			
			
			
			//System.out.print("\n");
			
		});
		
		
		
		
	}
	
	
	
	public void sysStatusFlags(SysStatus sysmsg) {
		
		//Compares the bitmask with all enumerated sensors. 
		EnumSet.allOf(MavSysStatusSensor.class).forEach(sensor -> {
			//System.out.print(sensor);
			
			if(sysmsg.onboardControlSensorsPresent().flagsEnabled(sensor)) {
				//System.out.print(" present ");
			}
			
			if(sysmsg.onboardControlSensorsEnabled().flagsEnabled(sensor)) {
				//System.out.print(" enabled");
			}
			
			if(sysmsg.onboardControlSensorsHealth().flagsEnabled(sensor)) {
				//System.out.print(" healthy ");
			}
			
			//System.out.print("\n");
			
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
		
		if(message.getPayload() instanceof ExtendedSysState) {
			ExtendedSysState ess = (ExtendedSysState) message.getPayload();
			if(ess.landedState().entry().equals(MavLandedState.MAV_LANDED_STATE_IN_AIR)) {
				//May need to elaborate here depending on system logic.
				inFlight = true;
			}else {
				inFlight = false;
			}
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
	
	public void disarmDrone(int target) {
		
		CommandLong longMessage = CommandLong.builder()
				.command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
				.confirmation(0)
				.param1(0)
				.targetSystem(target)
				.targetComponent(0)
				.build();
		
		PortBuilder.sendMessage(longMessage);
		
	}
	
	public void takeoffDrone(int target) {
		
	}
	
	public void landDrone(int target) {
		
	}
	
	public void rtlDrone(int target) {
		
	}
	
	

	

}
