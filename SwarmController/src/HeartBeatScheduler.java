import java.io.IOException;
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
		
		try {
			PortBuilder.connection.send1(255, 0, heartbeat);
			System.out.println("GCS says: \t" + heartbeat.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
