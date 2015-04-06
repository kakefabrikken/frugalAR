package no.ntnu.tpg4850.frugalar.network;

import android.os.Looper;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Olav on 25.03.2015.
 */
public class ValveService {
    public final static String URL = ".../valve";
    public void get(final String id) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpGet get = new HttpGet(URL);

                    //StringEntity se = new StringEntity( json.toString());
                    //se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    //post.setEntity(se);
                    response = client.execute(get);

                    /*Checking response */
                    if(response!=null){

                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        Log.i("ValveMessage", response.toString());
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    //createDialog("Error", "Cannot Estabilish Connection");
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }
}
