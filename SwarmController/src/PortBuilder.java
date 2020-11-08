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
public class PortBuilder{

	SerialPort port;
	Thread t;
	boolean portFlag = true;
	HashMap<Integer, Drone> droneMap = new HashMap<Integer, Drone>();
	static MavlinkConnection connection;
	MessageHandler msgHandler = new MessageHandler();

	public SerialPort[] getAvailablePorts() {
		return SerialPort.getCommPorts();
	}
	
	public void configInboundMessages(int target) {
		CommandLong intervalMessage = CommandLong.builder()
				.command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL)
				.confirmation(0)
				.param1(24)
				.param2(1000000)
				.param7(0)
				.targetSystem(target)
				.targetComponent(0)
				.build();
		
		try {
			connection.send1(target, 0, intervalMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CommandLong intervalMessage2 = CommandLong.builder()
				.command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL)
				.confirmation(0)
				.param1(26)
				.param2(1000000)
				.param7(0)
				.targetSystem(target)
				.targetComponent(0)
				.build();
		
		try {
			connection.send1(target, 0, intervalMessage2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CommandLong intervalMessage3 = CommandLong.builder()
				.command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL)
				.confirmation(0)
				.param1(27)
				.param2(-1)
				.param7(0)
				.targetSystem(target)
				.targetComponent(0)
				.build();
		
		try {
			connection.send1(target, 0, intervalMessage3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void buildPort (SerialPort port) {
		portFlag = true;
		this.port = port;
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0,0); // default parameters used for non-blocking
		// serial poling
		this.port.setBaudRate(57600);// default parameters used for data bits, parity and stop bits. To be added.
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
			    	  	
						connection = MavlinkConnection.builder(port.getInputStream(), port.getOutputStream())
								.dialect(MavAutopilot.MAV_AUTOPILOT_ARDUPILOTMEGA, new ArdupilotmegaDialect())
								.build();
						
						MavlinkMessage message;
						Timer time = new Timer();
						HeartBeatScheduler gcsHeartbeat = new HeartBeatScheduler();
						time.schedule(gcsHeartbeat, 0, 1000);
						
						
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
								droneMap.put(id, drone);
								configInboundMessages(id);
								
							}
							
							msgHandler.inboundMessage(message);
							
								
								
							}
							last = message.getSequence();
							if(last == 255) last = -1; //Sequence wraparround 
							
						
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
