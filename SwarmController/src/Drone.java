import java.util.EnumSet;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.ardupilotmega.CopterMode;
import io.dronefleet.mavlink.ardupilotmega.EkfStatusFlags;
import io.dronefleet.mavlink.ardupilotmega.EkfStatusReport;
import io.dronefleet.mavlink.ardupilotmega.Hwstatus;
import io.dronefleet.mavlink.common.CommandAck;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.ExtendedSysState;
import io.dronefleet.mavlink.common.GpsFixType;
import io.dronefleet.mavlink.common.GpsRawInt;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavLandedState;
import io.dronefleet.mavlink.common.MavModeFlag;
import io.dronefleet.mavlink.common.MavSysStatusSensor;
import io.dronefleet.mavlink.common.Statustext;
import io.dronefleet.mavlink.common.SysStatus;

/**
 * 
 */

/**
 * Christopher Brislin 1 Nov 2020 SwarmController
 */
@SuppressWarnings("unused")
public class Drone {

	// MavSystemID
	int droneID;

	// Packet drop calculations
	int dropCount;
	int lastCount = -1;

	MavlinkMessage<?> droneMessage;
	DroneInterface droneInterface;
	MessageHandler handler;
	Interface userInterface;

	boolean isArmed = false;
	boolean inFlight = false;

	public Drone(int id, MessageHandler handler) {

		this.droneID = id;
		this.handler = handler;

		droneInterface = new DroneInterface(this);
		droneInterface.buildInterface();
		
		userInterface = handler.getInterface();

	}

	public void buildDrone(int id) {

	}

	public int getDroneID() {
		return droneID;
	}

	public void calculatePacketDrop(int sequence) {
		if (sequence != (lastCount + 1)) {
			dropCount++;
		}
		lastCount = sequence;
		if (lastCount == 255)
			lastCount = -1; // Sequence wraparound
	}

	public void removeInterface() {
		droneInterface.removeDrone();
	}

	public void baseModeFlags(Heartbeat hbmsg) {
		// custom mode is the actual Ardupilot mode that is relevant.
		//long customMode = hbmsg.customMode();

		if (isArmed != hbmsg.baseMode().flagsEnabled(MavModeFlag.MAV_MODE_FLAG_SAFETY_ARMED)) {
			isArmed = hbmsg.baseMode().flagsEnabled(MavModeFlag.MAV_MODE_FLAG_SAFETY_ARMED);
			droneInterface.setArmButtonText(isArmed);
		}

		EnumSet.allOf(MavModeFlag.class).forEach(mode -> {

			if (hbmsg.baseMode().flagsEnabled(mode)) {
				if (Main.DEBUG) {
					// System.out.print(mode);

				}

			}

			if (Main.DEBUG) {
				// System.out.print("\n");
			}

		});

	}

	public void sysStatusFlags(SysStatus sysmsg) {

		// Compares the bitmask with all enumerated sensors.
		EnumSet.allOf(MavSysStatusSensor.class).forEach(sensor -> {
			// System.out.print(sensor);

			if (sysmsg.onboardControlSensorsPresent().flagsEnabled(sensor)) {
				// System.out.print(" present ");
			}

			if (sysmsg.onboardControlSensorsEnabled().flagsEnabled(sensor)) {
				// System.out.print(" enabled");
			}

			if (sysmsg.onboardControlSensorsHealth().flagsEnabled(sensor)) {
				// System.out.print(" healthy ");
			}

			// System.out.print("\n");

		});
	}

	public void ekfStatusFlags(EkfStatusReport ekfmsg) {

		EnumSet.allOf(EkfStatusFlags.class).forEach(status -> {

			if (ekfmsg.flags().flagsEnabled(status)) {
				// if(Main.DEBUG)System.out.println(status.toString());
			}

		});

	}

	@SuppressWarnings("unlikely-arg-type")
	public void gpsStatus(GpsRawInt gpsmsg) {
		if (!gpsmsg.fixType().equals(GpsFixType.GPS_FIX_TYPE_RTK_FLOAT)) {
			droneInterface.setIndicator(Const.CAUTION);
		}
	}

	public void newMessage(MavlinkMessage<?> message) {
		this.droneMessage = message;
		calculatePacketDrop(message.getSequence());

		if (message.getPayload() instanceof Heartbeat) {
			Heartbeat hb = (Heartbeat) message.getPayload();

			droneInterface.setStatusLabel(hb.systemStatus().entry().toString());
			baseModeFlags(hb);

		}

		if (message.getPayload() instanceof SysStatus) {
			SysStatus ss = (SysStatus) message.getPayload();
			ss.batteryRemaining();
			sysStatusFlags(ss);

		}

		if (message.getPayload() instanceof Hwstatus) {
			Hwstatus hs = (Hwstatus) message.getPayload();
			hs.i2cerr();

		}

		if (message.getPayload() instanceof EkfStatusReport) {
			EkfStatusReport sr = (EkfStatusReport) message.getPayload();
			ekfStatusFlags(sr);

		}
		if (message.getPayload() instanceof CommandAck) {

		}
		if (message.getPayload() instanceof GpsRawInt) {
			GpsRawInt gri = (GpsRawInt) message.getPayload();
			gpsStatus(gri);
		}

		if (message.getPayload() instanceof Statustext) {
			Statustext ststxt = (Statustext) message.getPayload();
			// Yo this is messed - fix this. DOnt do this.
			
			userInterface.managerInterface.addMessage("Drone " + droneID + " says:" + ststxt.text() + "\n");

		}

		if (message.getPayload() instanceof ExtendedSysState) {
			ExtendedSysState ess = (ExtendedSysState) message.getPayload();
			if (ess.landedState().entry().equals(MavLandedState.MAV_LANDED_STATE_IN_AIR)) {
				// May need to elaborate here depending on system logic.
				inFlight = true;
				droneInterface.setIndicatorLabel("Airborne");
			} else {
				inFlight = false;
				droneInterface.setIndicatorLabel("On Ground");
			}
		}

		else {

		}

	}

	public void armDrone(int target) {
		CommandLong longMessage = CommandLong.builder().command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM).confirmation(0)
				.param1(1).param2(21196).targetSystem(target).targetComponent(0).build();

		handler.sendMessage(longMessage);
	}

	public void disarmDrone(int target) {

		CommandLong longMessage = CommandLong.builder().command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM).confirmation(0)
				.param1(0).targetSystem(target).targetComponent(0).build();

		handler.sendMessage(longMessage);

	}

	public void takeoffDrone(int target) {

	}

	public void landDrone(int target) {

	}

	public void rtlDrone(int target) {

	}

}
