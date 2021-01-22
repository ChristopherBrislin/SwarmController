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
public class SerialConnection extends Connection{
	
	
	
	
	
	
	

	
	//Move these to somewhere more appropriate
	
	public SerialConnection(SerialPort port, int baud, MessageHandler handler) {
		this.port = port;
		this.baudRate = baud;
		this.messageHandler = handler;
		buildConnection();
	}
	
	public void buildConnection() {

		if(Main.DEBUG)System.out.println("Setting up Serial Connection with " + port + " @ " + baudRate);
		//this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);// default parameters used for
																				// non-blocking
																					// serial poling
		this.port.setBaudRate(baudRate);// default parameters used for data bits, parity and stop bits. To be added.
		this.port.openPort();
		in = port.getInputStream();
		out = port.getOutputStream();
		
		
		mavlinkConnection = MavlinkConnection
				.builder(port.getInputStream(), port.getOutputStream())
				.dialect(MavAutopilot.MAV_AUTOPILOT_ARDUPILOTMEGA, new ArdupilotmegaDialect())
				.dialect(MavAutopilot.MAV_AUTOPILOT_GENERIC, new CommonDialect())
				.build();
		
		messageHandler.setConnection(this);
		
		time = new Timer();
		HeartBeatScheduler gcsHeartbeat = new HeartBeatScheduler(messageHandler);
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
					
					
					while ((message = mavlinkConnection.next()) != null && !Interface.closePort) {
						messageHandler.inboundMessage(message);
					}
					if(Main.DEBUG)System.out.println("Connection closing due no mavlink.next");
					closeSerialConnection();
					

				} catch (Exception ex) {
					time.cancel();
					
					ex.printStackTrace();
					if(Main.DEBUG)System.out.println("Connection closing due IOException");
					closeSerialConnection();
				} 
			}
			
		});

		
		
	}
	
public void closeSerialConnection() {
		
		time.cancel();
		time.purge();
		port.removeDataListener();
		port.closePort();
		
		//Notify Interface
		//messageHandler.getInterface().
		
		if(Main.DEBUG)System.out.println("Close requested: " + port.closePort());
		
		messageHandler.onPortClose();
		
		
	
		
	}
	
	
	public SerialPort[] getAvailablePorts() {
		
		
		return SerialPort.getCommPorts();
	}

	


	
	

	

}
