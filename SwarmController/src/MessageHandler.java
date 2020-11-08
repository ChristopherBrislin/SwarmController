import java.util.TimerTask;

import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;
import io.dronefleet.mavlink.common.SysStatus;

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
		handleMessage();
		
	}
	
	public void handleMessage() {
		System.out.println("MAV" +message.getOriginSystemId() + " says: \t" + message.getPayload());
	}
	


}

/*

//MavlinkMessageInfo messageInfo = message.getPayload().getClass().getAnnotation(MavlinkMessageInfo.class);
//System.out.println(messageInfo.crc());

//int msgID = ((MavlinkMessageInfo)message.getPayload().getClass().getAnnotation(MavlinkMessageInfo.class)).id();
System.out.println("MAV says: \t" + message.getPayload());
//System.out.println(message.getPayload().getClass().getDeclaredMethods());

if(message.getPayload() instanceof SysStatus) {
	MavlinkMessage<SysStatus> status = (MavlinkMessage<SysStatus>) message;
	final Object message2 = message.getPayload();
	
	
	//System.out.println(message2.);
	/*
	
	Arrays.stream(message2.getClass().getDeclaredMethods())
	.filter(f -> f.isAnnotationPresent(MavlinkFieldInfo.class))
	.forEach(f -> {
		System.out.println(f.getName() + " = " + f.toString());
			//f.getName();
			//f.toString();
		
			
			//System.out.printf("%s = %s\n", f.getName(), f.invoke(message2).toString());
		
		
	});
*/