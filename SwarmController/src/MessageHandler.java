import java.util.List;

import io.dronefleet.mavlink.AbstractMavlinkDialect;
import io.dronefleet.mavlink.MavlinkDialect;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.ardupilotmega.Ahrs;
import io.dronefleet.mavlink.protocol.MavlinkPacket;

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

