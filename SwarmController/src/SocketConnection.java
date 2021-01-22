import java.io.IOException;
import java.net.ServerSocket;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.ardupilotmega.ArdupilotmegaDialect;
import io.dronefleet.mavlink.common.CommonDialect;
import io.dronefleet.mavlink.common.MavAutopilot;

/**
 * 
 */

/**
 * Christopher Brislin
 * 17 Jan 2021
 * SwarmController
 */
public class SocketConnection extends Connection implements Runnable{
	
	
	
	
	
	
	int tcpPort;
	
	
	
	public SocketConnection(int port) {
		this.tcpPort = port;
		thread = new Thread(this);
		thread.setName("TCPPort");
		
		thread.start();
	}
	
	
	
	public void buildConnection() {
	}
	
	public void closeConnection() {
		try {
			//inbound.close();
			socket.close();
			if(Main.DEBUG)System.out.println("TCP Connection Closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



	@Override
	public void run() {
try {
			
			
	
			socket = new ServerSocket(tcpPort);
			System.out.println(thread.getName());
			inbound = socket.accept();
			
			
			in = inbound.getInputStream();
			out = inbound.getOutputStream();
			
			if(Main.DEBUG)System.out.println("Server listening");
			
			mavlinkConnection = MavlinkConnection
					.builder(in, out)
					.dialect(MavAutopilot.MAV_AUTOPILOT_ARDUPILOTMEGA, new ArdupilotmegaDialect())
					.dialect(MavAutopilot.MAV_AUTOPILOT_GENERIC, new CommonDialect())
					.build();
			
			MavlinkMessage<?> message;
			
			
			
			while ((message = mavlinkConnection.next()) != null && !Interface.closePort) {
				messageHandler.inboundMessage(message);
			}
			closeConnection();
			
			
		}catch(Exception ex) {
			System.out.println(ex);
		}
	
		
	}
	


}
