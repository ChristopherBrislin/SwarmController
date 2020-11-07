import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import io.dronefleet.mavlink.AbstractMavlinkDialect;
import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkDialect;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.annotations.MavlinkMessageInfo;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.Statustext;
import io.dronefleet.mavlink.common.StatustextLong;
import io.dronefleet.mavlink.common.SysStatus;
import io.dronefleet.mavlink.protocol.MavlinkPacket;
import io.dronefleet.mavlink.protocol.MavlinkPacketReader;
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
	HashMap<Integer, Drone> droneMap = new HashMap<Integer, Drone>();
	//ArrayList<Drone> droneList = new ArrayList<Drone>();

	public SerialPort[] getAvailablePorts() {
		return SerialPort.getCommPorts();
	}

	public void buildPort (SerialPort port) {
		portFlag = true;
		this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0,0); // default parameters used for non-blocking
		// serial poling
		this.port.setBaudRate(115200);// default parameters used for data bits, parity and stop bits. To be added.
		this.port.openPort();
		
		port.addDataListener(new SerialPortDataListener() {
			float last = 0;
			float dropCount = 0;
			float totalCount = 0;
			   @Override
			   public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
			   @Override
			   public void serialEvent(SerialPortEvent event)
			   {
			      if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
			         return;
			      
			      try {
			    	  	
						MavlinkConnection connection = MavlinkConnection.builder(port.getInputStream(), port.getOutputStream())
								.dialect(MavAutopilot.MAV_AUTOPILOT_GENERIC, new StandardDialect())
								.build();
						
						MavlinkMessage message;
						StandardDialect dialect = new StandardDialect();
						
						while ((message = connection.next()) != null) {
							if(message.getSequence()!= (last+1)) {
								dropCount += message.getSequence() - last;
							}
							totalCount ++;
							
							int id = message.getOriginSystemId();
							if(!droneMap.containsKey(id)) {
								System.out.println("building new drone");
								Drone drone = new Drone();
								drone.buildDrone(id);
								//droneList.add(drone);
								droneMap.put(id, drone);
							}
							System.out.println((dropCount/totalCount*100) + "% \t" + dropCount + "\t" + message.getPayload());
							
							
							if(message.getPayload() instanceof Heartbeat) {
								MavlinkMessage<Heartbeat> heartbeat = (MavlinkMessage<Heartbeat>) message;
								heartbeat.getOriginSystemId();
								
								//int test = status.hashCode();
								//System.out.println(dropCount+ "\t" + last + "\t" +status.getPayload());
								//System.out.println(status.getPayload());
							}
							last = message.getSequence();
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
