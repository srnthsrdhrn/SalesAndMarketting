package Modules;

import java.util.List;

/**
 * Created by user on 9/13/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
