import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
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
	Thread thread;
	ServerSocket socket;
	
	Socket inbound;
	
	boolean isOpen = false;
	
	SerialPort port;
	int baudRate;
	
	MavlinkConnection mavlinkConnection;
	MessageHandler messageHandler;
	
	Timer time;
	
	public void sendMessage(Object outMessage) {
		try {
			mavlinkConnection.send1(255, 0, outMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public boolean isOpen() {
		
		//Notify interface on change
		boolean result = false;
		
		
		while(socket == null && port == null) {
			System.out.println("Im stuck agian");
		}
		
		if(socket== null) {
			result=port.isOpen();
		}else if(port == null) {
			result= !socket.isClosed();
		}
		
		return result;
	}
	
	
	
	public void close() {
		
			try {
				if(inbound != null)inbound.close();
				if(socket != null)socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Main.DEBUG)System.out.println("Thread terminated: " + thread.getName());
			thread = null;
			
	}
	
	
	

}
