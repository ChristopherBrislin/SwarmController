import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;

import com.fazecast.jSerialComm.SerialPort;
import io.dronefleet.mavlink.MavlinkConnection;

/**
 * 
 */

/**
 * Christopher Brislin
 * 18 Jan 2021
 * SwarmController
 */
public class Connection {
	
	InputStream in;
	OutputStream out;
	
	Socket inbound;
	
	boolean isOpen = false;
	
	SerialPort port;
	int baudRate;
	
	static MavlinkConnection mavlinkConnection;
	MessageHandler msgHandler = new MessageHandler();
	
	Timer time;
	
	public static void sendMessage(Object outMessage) {
		try {
			mavlinkConnection.send1(255, 0, outMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isOpen() {
		boolean result = false;
		
		if(inbound == null) {
			result=port.isOpen();
		}else if(port == null) {
			result= !inbound.isClosed();
		}
		
		return result;
	}
	
	
	
	
	

}
