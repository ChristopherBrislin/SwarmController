import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.Heartbeat;

/**
 * 
 */

/**
 * Christopher Brislin 1 Nov 2020 SwarmController
 */
public class PortBuilder {

	SerialPort port;

	public SerialPort[] getAvailablePorts() {
		return SerialPort.getCommPorts();
	}

	public void buildPort(SerialPort port) {
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
						if ((message = connection.next()) != null) {
							if (message instanceof Mavlink2Message) {
								Mavlink2Message message2 = (Mavlink2Message) message;
								System.out.println("Mavlink 2 message");
								if (message2.isSigned()) {
									System.out.println("Signed Mavlink 2 message");
								} else {
									// Message unsigned
									System.out.println("Unsigned Message");
								}
							} else {
								// Mavlink 1 message
							}
							
							System.out.println(message.toString());
						

						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
			      
			   }
			});

		

	}

	public void closePort() {
		
		this.port.closePort();
		System.out.println("close port called");
	}

}
