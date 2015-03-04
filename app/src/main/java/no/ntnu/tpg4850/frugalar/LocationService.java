package no.ntnu.tpg4850.frugalar;

import android.util.Log;

import com.customlbs.library.IndoorsException;
import com.customlbs.library.IndoorsFactory;
import com.customlbs.library.IndoorsLocationListener;
import com.customlbs.library.model.Building;
import com.customlbs.library.model.Zone;
import com.customlbs.shared.Coordinate;
import com.customlbs.surface.library.IndoorsSurfaceFactory;
import com.customlbs.surface.library.IndoorsSurfaceFragment;
import com.google.vrtoolkit.cardboard.CardboardActivity;

import java.util.List;

/**
 * Created by Olav on 04.03.2015.
 */
public class LocationService implements IndoorsLocationListener {

    private static final String TAG = "Location";

    public LocationService(CardboardActivity parent) {
        IndoorsFactory.Builder indoorsBuilder = new IndoorsFactory.Builder();
        IndoorsSurfaceFactory.Builder surfaceBuilder = new IndoorsSurfaceFactory.Builder();
        indoorsBuilder.setContext(parent);
        indoorsBuilder.setApiKey("8fe0cc81-2098-41cf-b8c5-9fa5874ef8a6");
        indoorsBuilder.setBuildingId((long) 272563595);
        indoorsBuilder.setUserInteractionListener(this);
        surfaceBuilder.setIndoorsBuilder(indoorsBuilder);

        indoorsFragment = surfaceBuilder.build();

        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    }
    private IndoorsSurfaceFragment indoorsFragment;

    @Override
    public void loadingBuilding(int i) {
        Log.i(TAG, "Loading Building");

    }

    @Override
    public void buildingLoaded(Building building) {
        Log.i(TAG, "Loaded Building");
    }

    @Override
    public void leftBuilding(Building building) {
        Log.i(TAG, "Left building");
    }

    @Override
    public void positionUpdated(Coordinate coordinate, int i) {
        Log.i(TAG, "position update" + coordinate);
    }

    @Override
    public void orientationUpdated(float v) {
        Log.i(TAG, "Orientation");
    }

    @Override
    public void changedFloor(int i, String s) {
        Log.i(TAG, "Changed floor");
    }

    @Override
    public void enteredZones(List<Zone> zones) {
        Log.i(TAG, "Entered zone");
    }

    @Override
    public void onError(IndoorsException e) {
        Log.i(TAG, "Error");
    }
}
