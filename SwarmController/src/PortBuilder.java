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
		this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000,1000); // default parameters used for non-blocking
		// serial poling
		this.port.setBaudRate(57600);// default parameters used for data bits, parity and stop bits. To be added.
		this.port.openPort();
		if (this.port.isOpen()) {

			System.out.println("Port Open");
		}

		try {
			MavlinkConnection connection = MavlinkConnection.create(port.getInputStream(), port.getOutputStream());
			MavlinkMessage message;
			while ((message = connection.next()) != null) {
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
				if (message.getPayload() instanceof Heartbeat) {
					MavlinkMessage<Heartbeat> heartbeatMessage = (MavlinkMessage<Heartbeat>) message;
					System.out.println(heartbeatMessage.toString());
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void closePort() {
		this.port.closePort();
	}

}
