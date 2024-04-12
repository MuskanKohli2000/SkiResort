package org.example.schema;

// SkierData class represents data related to a skier, including resort, season, day, and skier ID
public class SkierData {
    private String resortID; // ID of the resort
    private String seasonID; // ID of the season
    private String dayID;    // ID of the day
    private int skierID;     // ID of the skier

    // Parameterized constructor to initialize resortID, seasonID, dayID, and skierID
    public SkierData(String resortID, String seasonID, String dayID, int skierID) {
        this.resortID = resortID; // Assigning resortID
        this.seasonID = seasonID; // Assigning seasonID
        this.dayID = dayID;       // Assigning dayID
        this.skierID = skierID;   // Assigning skierID
    }

    // Override toString method to print SkierData object as a string
    @Override
    public String toString() {
        return "SkierData [resortID=" + resortID + ", seasonID=" + seasonID + ", dayID=" + dayID + ", skierID=" + skierID + "]";
    }

    // Getter method for resortID
    public String getResortID() {
        return resortID;
    }

    // Setter method for resortID
    public void setResortID(String resortID) {
        this.resortID = resortID;
    }

    // Getter method for seasonID
    public String getSeasonID() {
        return seasonID;
    }

    // Setter method for seasonID
    public void setSeasonID(String seasonID) {
        this.seasonID = seasonID;
    }

    // Getter method for dayID
    public String getDayID() {
        return dayID;
    }

    // Setter method for dayID
    public void setDayID(String dayID) {
        this.dayID = dayID;
    }

    // Getter method for skierID
    public int getSkierID() {
        return skierID;
    }

    // Setter method for skierID
    public void setSkierID(int skierID) {
        this.skierID = skierID;
    }
}
