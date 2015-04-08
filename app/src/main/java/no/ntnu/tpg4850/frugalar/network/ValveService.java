package no.ntnu.tpg4850.frugalar.network;

import android.os.Looper;
import android.util.JsonReader;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import no.ntnu.tpg4850.frugalar.scanner.QRCode;

/**
 * Created by Olav on 25.03.2015.
 */
public class ValveService {
    public final static String BASE_URL = "http://private-b1522-frugular.apiary-mock.com";
    public void get(final String id, final QRCode qrCode) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                JsonReader reader = null;
                try {
                    HttpGet get = new HttpGet(BASE_URL + "/valve/" + id);
                    Log.i("ValveMessage", "Sent request to" + BASE_URL + "/valve/" + id );

                    response = client.execute(get);

                    /*Checking response */
                    if(response!=null){

                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
                        reader.setLenient(true);

                        Log.i("ValveMessage", response.toString());
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    Log.i("ValveMessage", "Cannot establish connection");
        //createDialog("Error", "Cannot Estabilish Connection");
                }

                if(reader != null) {
                    try {
                        Valve v = createValve(reader);
                        qrCode.setValve(v);
                        reader.close();

                    } catch (Exception e) {
                        Log.i("ValveMessage", "Error in json converting");
                        Log.i("ValveMessage", e.getMessage());
                        Log.i("ValveMessage", e.getLocalizedMessage());
                        for(StackTraceElement t: e.getStackTrace()) {
                            Log.i("ValveMessage", t.toString());
                        }
                    }

                }
                Looper.loop(); //Loop in the message queue

            }
};

        t.start();


    }

    /**
     * createValve takes a json recieved from the valve endpoint and unmarshall the json
     * object to a java object. The method is custom for the valve endpoint. Could be replaced by
     * a json libary to achieve cleaner code.
     * @param reader
     * @return Valve object
     * @throws IOException
     */
    private Valve createValve(JsonReader reader) throws Exception {

        Valve v = new Valve();
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                v.id = reader.nextInt() + "";
            }
            else if(name.equals("status")) {
                v.status = reader.nextString();
            }
            else if(name.equals("history")) {
                reader.beginArray();
                ArrayList<Message> history = new ArrayList<Message>();

                while(reader.hasNext()) {
                    reader.beginObject();
                    Message m = new Message();
                    while(reader.hasNext()) {
                        String innerName = reader.nextName();
                        if (innerName.equals("date")) {
                            m.date = ValveService.createDate(reader.nextString());
                        }
                        else if (innerName.equals("message")) {
                            m.message = reader.nextString();
                        }
                        else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    history.add(m);
                }
                reader.endArray();
                v.history = history;
            }
            else if(name.equals("status")) {
                v.status = reader.nextString();
            }
            else if(name.equals("installed")) {
                v.installed = ValveService.createDate(reader.nextString());
            }
            else if(name.equals("work_permission")) {
                v.workPermission = reader.nextBoolean();
            }
            else if(name.equals("work_permission_info")) {
                v.workPermissionInfo = reader.nextString();
            }
            else if(name.equals("valve_state")) {
                v.valveStatus = reader.nextDouble();
            }
            else if(name.equals("type")) {
                v.type = reader.nextString();
            }
            else if(name.equals("turns_open")) {
                v.turnsToOpen = reader.nextInt();
            }
            else if(name.equals("turns_closed")) {
                v.turnsToClosed = reader.nextInt();
            }
            else if(name.equals("turns_closed")) {
                v.turnsToClosed = reader.nextInt();
            }
            else if(name.equals("temperature")) {
                v.temperature = reader.nextDouble();
            }
            else if(name.equals("temperature_info")) {
                v.temperatureInfo = reader.nextString();
            }
            else if(name.equals("flow")) {
                v.flow = reader.nextString();
            }
            else if(name.equals("supplier")) {
                v.supplier = reader.nextString();
            }
            else if(name.equals("error")) {
                v.error = reader.nextBoolean();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return v;
    }

    private static Date createDate(String iso8601string) throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        return date;
    }
}
