import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Timer;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortIOException;

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
public class PortBuilder implements Runnable{
	
	Thread t;

	SerialPort port;
	boolean portFlag = true;
	static HashMap<Integer, Drone> droneMap = new HashMap<Integer, Drone>();
	static MavlinkConnection connection;
	MessageHandler msgHandler = new MessageHandler();
	
	InputStream in;
	OutputStream out;
	
	Timer time;
	
	public void setPort(SerialPort port) {
		this.port = port;
	}

	
	
	public SerialPort[] getAvailablePorts() {
		return SerialPort.getCommPorts();
	}

	public static void sendMessage(Object outMessage) {
		try {
			connection.send1(255, 0, outMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void closePort() {
		
		time.cancel();
		time.purge();
		//ßtime.wait();
		System.out.println("Close requested: " + port.closePort());
		t=null;
		
		
		
	
		
	}
	
	public void start() {
		if(t==null) {
			t = new Thread(this, "Serial Port Thread");
			t.start();
		}else {
			t.start();
		}
	}

	@Override
	public void run() {

		//this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);// default parameters used for
																				// non-blocking
																					// serial poling
		this.port.setBaudRate(57600);// default parameters used for data bits, parity and stop bits. To be added.
		this.port.openPort();
		in = port.getInputStream();
		out = port.getOutputStream();
		
		
		connection = MavlinkConnection
				.builder(port.getInputStream(), port.getOutputStream())
				.dialect(MavAutopilot.MAV_AUTOPILOT_ARDUPILOTMEGA, new ArdupilotmegaDialect())
				.dialect(MavAutopilot.MAV_AUTOPILOT_GENERIC, new CommonDialect())
				.build();
		
		time = new Timer();
		HeartBeatScheduler gcsHeartbeat = new HeartBeatScheduler();
		time.schedule(gcsHeartbeat, 0, 1000);
		
		port.addDataListener(new SerialPortDataListener() {

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}

			@Override

			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
					return;
				}
					
				
				try {
					MavlinkMessage<?> message;
					
					
					while ((message = connection.next()) != null && !Interface.closePort) {
						msgHandler.inboundMessage(message);
					}
					closePort();
					

				} catch (Exception ex) {
					time.cancel();
					ex.printStackTrace();
					
				} 
			}
			
		});

		
		
	}

}
