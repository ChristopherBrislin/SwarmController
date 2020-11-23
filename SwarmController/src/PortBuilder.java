import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.ardupilotmega.ArdupilotmegaDialect;
import io.dronefleet.mavlink.common.CommonDialect;
import io.dronefleet.mavlink.common.MavAutopilot;

/**
 * 
 */

/**
 * Christopher Brislin 1 Nov 2020 SwarmController
 */
public class PortBuilder {

	SerialPort port;
	Thread t;
	boolean portFlag = true;
	static HashMap<Integer, Drone> droneMap = new HashMap<Integer, Drone>();
	static MavlinkConnection connection;
	MessageHandler msgHandler = new MessageHandler();

	public SerialPort[] getAvailablePorts() {
		return SerialPort.getCommPorts();
	}

	public static void sendMessage(Object outMessage, int target) {
		try {
			connection.send1(target, 0, outMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void buildPort(SerialPort port) {
		
		this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); // default parameters used for
																					// non-blocking
		// serial poling
		this.port.setBaudRate(57600);// default parameters used for data bits, parity and stop bits. To be added.
		this.port.openPort();
		
		
		
		connection = MavlinkConnection.builder(port.getInputStream(), port.getOutputStream())
				.dialect(MavAutopilot.MAV_AUTOPILOT_ARDUPILOTMEGA, new ArdupilotmegaDialect())
				.dialect(MavAutopilot.MAV_AUTOPILOT_GENERIC, new CommonDialect()).build();
		
		Timer time = new Timer();
		HeartBeatScheduler gcsHeartbeat = new HeartBeatScheduler();
		time.schedule(gcsHeartbeat, 0, 1000);

		port.addDataListener(new SerialPortDataListener() {

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}

			@Override

			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
					return;

				try {
					MavlinkMessage<?> message;

					while ((message = connection.next()) != null) {
						msgHandler.inboundMessage(message);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		

	}

	public void closePort() {
		
		this.port.closePort();
		System.out.println("close port called");
	}

}
