package org.example.schema;

// SkierLiftRideEvent class represents an event of a skier riding a lift, including relevant details such as IDs and time
public class SkierLiftRideEvent {
    private final int skierId;   // ID of the skier
    private final int resortId;  // ID of the resort
    private final int liftId;    // ID of the lift
    private final int seasonId;  // ID of the season
    private final int dayId;     // ID of the day
    private final int time;      // Time of the lift ride

    // Parameterized constructor to initialize skierId, resortId, liftId, seasonId, dayId, and time
    public SkierLiftRideEvent(int skierId, int resortId, int liftId, int seasonId, int dayId, int time) {
        this.skierId = skierId;   // Assigning skierId
        this.resortId = resortId; // Assigning resortId
        this.liftId = liftId;     // Assigning liftId
        this.seasonId = seasonId; // Assigning seasonId
        this.dayId = dayId;       // Assigning dayId
        this.time = time;         // Assigning time
    }

    // Getter method for skierId
    public int getSkierId() {
        return skierId;
    }

    // Getter method for resortId
    public int getResortId() {
        return resortId;
    }

    // Getter method for liftId
    public int getLiftId() {
        return liftId;
    }

    // Getter method for seasonId
    public int getSeasonId() {
        return seasonId;
    }

    // Getter method for dayId
    public int getDayId() {
        return dayId;
    }

    // Getter method for time
    public int getTime() {
        return time;
    }
}
