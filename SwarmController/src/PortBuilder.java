import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.Statustext;
import io.dronefleet.mavlink.protocol.MavlinkPacket;
import io.dronefleet.mavlink.protocol.MavlinkPacketReader;

/**
 * 
 */

/**
 * Christopher Brislin 1 Nov 2020 SwarmController
 */
public class PortBuilder implements Runnable{

	SerialPort port;
	Thread t;
	boolean portFlag = true;
	HashMap<Integer, Drone> droneMap = new HashMap<Integer, Drone>();
	//ArrayList<Drone> droneList = new ArrayList<Drone>();

	public SerialPort[] getAvailablePorts() {
		return SerialPort.getCommPorts();
	}

	public void buildPort(SerialPort port) {
		portFlag = true;
		this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0,0); // default parameters used for non-blocking
		// serial poling
		this.port.setBaudRate(57600);// default parameters used for data bits, parity and stop bits. To be added.
		this.port.openPort();
		
		port.addDataListener(new SerialPortDataListener() {
			   @Override
			   public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
			   @Override
			   public void serialEvent(SerialPortEvent event)
			   {
			      if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
			         return;
			      
			      try {
						MavlinkConnection connection = MavlinkConnection.create(port.getInputStream(), port.getOutputStream());
						
						MavlinkMessage message;
					
						
						
						//MavlinkPacket packet;
						while ((message = connection.next()) != null && portFlag) {
							
							int id = message.getOriginSystemId();
							if(!droneMap.containsKey(id)) {
								System.out.println("building new drone");
								Drone drone = new Drone();
								drone.buildDrone(id);
								//droneList.add(drone);
								droneMap.put(id, drone);
							}
							
							System.out.println(message.getOriginSystemId());
							
							
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
