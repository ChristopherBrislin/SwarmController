import java.util.TimerTask;

import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;

/**
 * 
 */

/**
 * Christopher Brislin
 * 8 Nov 2020
 * SwarmController
 */
public class HeartBeatScheduler  extends TimerTask {
	
	public void run() {
		
		Heartbeat heartbeat = Heartbeat.builder()
				.type(MavType.MAV_TYPE_GCS)
				.autopilot(MavAutopilot.MAV_AUTOPILOT_INVALID)
				.systemStatus(MavState.MAV_STATE_UNINIT)
				.mavlinkVersion(3)
				.build();
		
		PortBuilder.sendMessage(heartbeat);
		if(Main.DEBUG)System.out.println("Heartbeat Sent");
		
	}

}
