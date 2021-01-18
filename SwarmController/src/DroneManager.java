import java.util.HashMap;

/**
 * 
 */

/**
 * Christopher Brislin
 * 17 Jan 2021
 * SwarmController
 */
public class DroneManager {
	
	
	private  static HashMap<Integer, Drone> droneMap = new HashMap<Integer, Drone>();
	
	
	
	public void addDrone(Drone drone, int id) {
		
		droneMap.put(id, drone);
	}
	
	public Drone getDrone(int id) {
		return droneMap.get(id);
	}
	
	public void rtlAllDrones() {
		droneMap.forEach((id,drone) -> {
			drone.rtlDrone(id);
		});
	}
	
	public void armAllDrones() {
		droneMap.forEach((id,drone) -> {
			drone.armDrone(id);
		});
	}
	
	public void takeoffAllDrones() {
		droneMap.forEach((id, drone) -> {
			drone.takeoffDrone(id);
		});
	}
	
	public void landAllDrones() {
		droneMap.forEach((id, drone) -> {
			drone.landDrone(id);
		});
	}
	
	public void disarmAllDrones() {
		droneMap.forEach((id, drone) -> {
			drone.disarmDrone(id);
		});
	}
	
	public boolean droneExists(int id) {
		return droneMap.containsKey(id);
	}
	
	public void removeDrone(int id) {
		Drone drone = droneMap.get(id);
		drone.removeInterface();
		droneMap.remove(id);
	}
	
	public void clearManager() {
		
		droneMap.forEach((id, drone)->{
			drone.removeInterface();
		});
		droneMap.clear();
	}
	

}
