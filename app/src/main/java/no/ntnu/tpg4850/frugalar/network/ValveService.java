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


                try {
                    HttpGet get = new HttpGet(BASE_URL + "/valve/" + id);
                    Log.i("ValveMessage", "Sent request to" + BASE_URL + "/valve/" + id );
                    //StringEntity se = new StringEntity( json.toString());
                    //se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    //post.setEntity(se);
                    response = client.execute(get);

                    /*Checking response */
                    if(response!=null){

                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
                        try {
                            Valve v = createValve(reader);
                            qrCode.setValve(v);
                        }
                        finally {
                            reader.close();
                        }
                        Log.i("ValveMessage", response.toString());
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    Log.i("ValveMessage", "Cannot establish connection");
        //createDialog("Error", "Cannot Estabilish Connection");
    }

    Looper.loop(); //Loop in the message queue
}
};

        t.start();


    }

    private Valve createValve(JsonReader reader) throws IOException {
        Valve v = new Valve();
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                v.id = reader.nextInt() + "";
            }
            else if(name.equals("title")) {
                v.text = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return v;
    }
}
