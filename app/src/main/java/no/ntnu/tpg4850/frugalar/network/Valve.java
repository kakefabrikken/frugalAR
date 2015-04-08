package no.ntnu.tpg4850.frugalar.network;

import java.util.ArrayList;
import java.util.Date;

public class Valve {

    public String id;
    public boolean isCorrect;
    public ArrayList<Message> history = new ArrayList<Message>();
    public Date installed;
    public boolean error;
    public boolean workPermission;
    public String workPermissionInfo;
    public boolean bypass;
    public String type;
    public String status;
    public double valveStatus;
    public int turnsToOpen;
    public int turnsToClosed;
    public double temperature;
    public String temperatureInfo;
    public String flow;
    public double DeltaPressure;
    public double upstream;
    public double downstream;
    public String area;
    public String supplier;





    public String toString() {
        return this.id  +" " + this.status;
    }

}
