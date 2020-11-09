import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import io.dronefleet.mavlink.AbstractMavlinkDialect;
import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkDialect;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.annotations.MavlinkFieldInfo;
import io.dronefleet.mavlink.annotations.MavlinkMessageInfo;
import io.dronefleet.mavlink.ardupilotmega.ArdupilotmegaDialect;
import io.dronefleet.mavlink.common.BatteryStatus;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;
import io.dronefleet.mavlink.common.Statustext;
import io.dronefleet.mavlink.common.StatustextLong;
import io.dronefleet.mavlink.common.SysStatus;
import io.dronefleet.mavlink.protocol.MavlinkPacket;
import io.dronefleet.mavlink.protocol.MavlinkPacketReader;
import io.dronefleet.mavlink.serialization.payload.MavlinkPayloadDeserializer;
import io.dronefleet.mavlink.standard.StandardDialect;

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
	
	int[] mavlinkMessages = new int[] {1,165, 147, 193 };
	CommandLong[] intervalMessage = new CommandLong[mavlinkMessages.length];

	public SerialPort[] getAvailablePorts() {
		return SerialPort.getCommPorts();
	}

	public void configInboundMessages(int target) {
		
		for(int i=0; i<mavlinkMessages.length; i++) {
			intervalMessage[i] = CommandLong.builder()
					.command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL)
					.confirmation(0)
					.param1(mavlinkMessages[i])
					.param2(1000000)
					.param7(0)
					.targetSystem(target)
					.targetComponent(0)
					.build();
			
			try {
				connection.send1(target, 0, intervalMessage[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		

	}

	public void buildPort(SerialPort port) {
		portFlag = true;
		this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); // default parameters used for
																					// non-blocking
		// serial poling
		this.port.setBaudRate(115200);// default parameters used for data bits, parity and stop bits. To be added.
		this.port.openPort();

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

					connection = MavlinkConnection.builder(port.getInputStream(), port.getOutputStream())
							.dialect(MavAutopilot.MAV_AUTOPILOT_ARDUPILOTMEGA, new ArdupilotmegaDialect()).build();

					MavlinkMessage message;
					Timer time = new Timer();
					HeartBeatScheduler gcsHeartbeat = new HeartBeatScheduler();
					time.schedule(gcsHeartbeat, 0, 1000);

					while ((message = connection.next()) != null) {

						int id = message.getOriginSystemId();
						if (!droneMap.containsKey(id)) {
							System.out.println("building new drone");
							Drone drone = new Drone();
							drone.buildDrone(id);
							droneMap.put(id, drone);
							configInboundMessages(id);

						}

						msgHandler.inboundMessage(message);

					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});

	}

	public void closePort() {
		portFlag = false;
		this.port.closePort();
		System.out.println("close port called");
	}

}
