import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavType;
import io.dronefleet.mavlink.common.MessageInterval;

/**
 * 
 */

/**
 * Christopher Brislin 5 Nov 2020 SwarmController
 */
public class MessageHandler {
	int id;
	static int i = 0;
	MavlinkMessage<?> message;
	static int[] mavlinkMessages = new int[] {1, 24, 165, 193, 245}; //SYS_STATUS, HWSTATUS, EKF_STATUS_REPORT, EXTENDED_SYS_STATE
	
	DroneManager droneManager = new DroneManager();
	
	long pastTime;
	long TIMEOUT = 12000;
	static boolean ConfigComplete = true;

	public void inboundMessage(MavlinkMessage<?> message) {
		this.message = message;
		this.id = message.getOriginSystemId();
		handleMessage();

	}
	
	public void onPortClose() {
		droneManager.clearManager();
	}

	@SuppressWarnings("unchecked")
	public void handleMessage() {
		if(Main.MAVLINK_DEBUG)System.out.println(message.getPayload());
		
		if((System.currentTimeMillis() - pastTime) > TIMEOUT && !ConfigComplete) {
			configInboundMessages(id);
		}
		
		if(message.getPayload() instanceof MessageInterval) {
			MavlinkMessage<MessageInterval> mi = (MavlinkMessage<MessageInterval>) message;
			configInboundMessages(id, mi);
			
		}
		
		if (droneManager.droneExists(id)) {
			//If the drone exists in the hashmap, just send the message through to the existing drone. 
			droneManager.getDrone(id).newMessage(message);
			
		} else if (message.getPayload() instanceof Heartbeat && !droneManager.droneExists(id)) {

			// If message is a heartbeat cast it 
			MavlinkMessage<Heartbeat> hb = (MavlinkMessage<Heartbeat>) message;
			// Check that the heartbeat originates from a Drone before creating new drone
			if (hb.getPayload().type().entry().equals(MavType.MAV_TYPE_QUADROTOR)) {
				Drone drone = new Drone();
				drone.buildDrone(id);
				droneManager.addDrone(drone, id);
				
				ConfigComplete = false;
				configInboundMessages(id);
				
				
			}
		} else {
			
			return;
		}

	}

	public void configInboundMessages(int target, MavlinkMessage<MessageInterval> currentIntervalMessage){
		
			currentIntervalMessage.getPayload().messageId();
		
			if(currentIntervalMessage.getPayload().intervalUs() != 1000000) {
			CommandLong longMessage = CommandLong.builder()
					.command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL)
					.confirmation(0)
					.param1(currentIntervalMessage.getPayload().messageId())
					.param2(1000000)
					.param7(0)
					.targetSystem(target)
					.targetComponent(0)
					.build();
			
			PortBuilder.sendMessage(longMessage);
			if(Main.DEBUG) System.out.println("Config Sent");
			}
			if(i<4) {
				configInboundMessages(target);
				i++;
			
			}else if(i>3) {
				//!! THIS ISN'T GOING TO WORK WITH MULTIPLE SYSTEMS - FIX IT. Suggest moving it to the drone. 
				//Update: 17/01/21 there seems to be an issue with a partially configured message interval
				//blocking this... 
				ConfigComplete = true;
			}
		
		
		

	}
	
	public void configInboundMessages(int target) {
		
		pastTime = System.currentTimeMillis();
		
		CommandLong longMessage = CommandLong.builder()
				.command(MavCmd.MAV_CMD_GET_MESSAGE_INTERVAL)
				.confirmation(0)
				.param1(mavlinkMessages[i])
				.targetSystem(target)
				.targetComponent(0)
				.build();
		
		PortBuilder.sendMessage(longMessage);
		if(Main.DEBUG)System.out.println("Config Requested");
	
		
	}

}
