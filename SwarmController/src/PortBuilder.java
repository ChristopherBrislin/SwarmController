import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
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
import io.dronefleet.mavlink.annotations.MavlinkFieldInfo;
import io.dronefleet.mavlink.annotations.MavlinkMessageInfo;
import io.dronefleet.mavlink.ardupilotmega.ArdupilotmegaDialect;
import io.dronefleet.mavlink.common.BatteryStatus;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
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
			float last = -1;
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
								.dialect(MavAutopilot.MAV_AUTOPILOT_ARDUPILOTMEGA, new ArdupilotmegaDialect())
								.build();
						
						MavlinkMessage message;
						MavlinkPacket packet; 
						
						
						while ((message = connection.next()) != null) {
							if(message.getSequence()!= (last+1)) {
								//dropCount += message.getSequence() - last;
								dropCount++;
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
							
							
							
							
							
							
							//int msgID = ((MavlinkMessageInfo)message.getPayload().getClass().getAnnotation(MavlinkMessageInfo.class)).id();
							//System.out.println(dropCount + "% \t" + last + "\t" + msgID +"\t" + message.getPayload());
							if(message.getPayload() instanceof Statustext) {
								MavlinkMessage<Statustext> status = (MavlinkMessage<Statustext>) message;
								final Object message2 = status.getPayload();
								
								Arrays.stream(message2.getClass().getDeclaredMethods())
								.filter(f -> f.isAnnotationPresent(MavlinkFieldInfo.class))
								.forEach(f -> {
									try {
										System.out.printf("%s = %s\n", f.getName(), f.invoke(message2).toString());
									} catch(InvocationTargetException | IllegalAccessException e) {
										e.printStackTrace();
									}
									
								});
							
							}
							last = message.getSequence();
							if(last == 255) last = -1; //Sequence wraparround 
							
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
