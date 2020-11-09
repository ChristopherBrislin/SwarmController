import io.dronefleet.mavlink.MavlinkMessage;

/**
 * 
 */

/**
 * Christopher Brislin
 * 5 Nov 2020
 * SwarmController
 */
public class MessageHandler {
	int id;
	MavlinkMessage message;
	
	
	public void inboundMessage(MavlinkMessage message) {
		this.message = message;
		this.id = message.getOriginSystemId();
		handleMessage();
		
	}
	
	public void handleMessage() {
		PortBuilder.droneMap.get(id).newMessage(message);
		
	}
	

}

