package org.example.schema;

// LiftRide class represents a single lift ride with its liftID and time
public class LiftRide {
	private int liftID; // ID of the lift
	private int time;   // Time of the lift ride

	// Default constructor
	public LiftRide() {

	}

	// Parameterized constructor to initialize liftID and time
	public LiftRide(int liftID, int time) {
		super(); // Call to superclass constructor (not necessary here)
		this.liftID = liftID; // Assigning liftID
		this.time = time;     // Assigning time
	}

	// Override toString method to print LiftRide object as a string
	@Override
	public String toString() {
		return "LiftRide [time=" + time + ", liftID=" + liftID + "]";
	}

	// Getter method for time
	public int getTime() {
		return time;
	}

	// Setter method for time
	public void setTime(int time) {
		this.time = time;
	}

	// Getter method for liftID
	public int getLiftID() {
		return liftID;
	}

	// Setter method for liftID
	public void setLiftID(int liftID) {
		this.liftID = liftID;
	}
}