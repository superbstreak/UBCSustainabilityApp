package ubc.cs.cpsc210.sustainabilityapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import ubc.cs.cpsc210.sustainabilityapp.model.LatLong;
import ubc.cs.cpsc210.sustainabilityapp.model.POIRegistry;
import ubc.cs.cpsc210.sustainabilityapp.model.PointOfInterest;
import ubc.cs.cpsc210.sustainabilityapp.model.SharedPreferencesKeyValueStore;
import ubc.cs.cpsc210.sustainabilityapp.model.TourState;
import ubc.cs.cpsc210.sustainabilityapp.routing.RouteInfo;
import ubc.cs.cpsc210.sustainabilityapp.routing.RoutingService;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Fragment holding the map in the UI.
 */
public class MapDisplayFragment extends Fragment {

	/**
	 * Log tag for LogCat messages
	 */
	private final static String LOG_TAG = "MapDisplayActivity";

	/**
	 * Location of the ICCS building
	 */
	private final static GeoPoint ICICS_GEOPOINT = new GeoPoint(49.260887,
			-123.24902);

	/**
	 * Overlay for POI markers.
	 */
	private ItemizedIconOverlay<OverlayItem> poiOverlay;

	/**
	 * Overlay for the user's current location.
	 */
	private SimpleLocationOverlay myLocationOverlay;

	/**
	 * Overlay for the route connecting the selected POI's.
	 */
	private PathOverlay tourOverlay;

	/**
	 * Overlay for the route connecting the user's current location to the
	 * nearest selected POI.
	 */
	private PathOverlay routeToTourOverlay;

	/**
	 * Manages and stores selected features and POI's.
	 */
	private TourState tourState;

	/**
	 * Currently selected POI's.
	 */
	private List<PointOfInterest> selectedPOIs;

	/**
	 * Wrapper for a service which calculates routes between POI's, and between
	 * the user's current location and the nearest selected POI.
	 */
	private RoutingService routingService;

	/**
	 * View that shows the map
	 */
	private MapView mapView;

	/**
	 * Route retriever services
	 */
	private RouteRetriever poiRouteRetriever;
	private RouteRetriever toTourRouteRetriever;

	// Add fields necessary to determine user's location
	private LocationManager manager;
	private UserLocationListener listener;
	private String provider;
	private Location myLocation;

	/**
	 * Get routing service, current state of tour and initialize location
	 * services.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(LOG_TAG, "onActivityCreated");

		routingService = ((UBCSustainabilityAppActivity) getActivity())
				.getRoutingService();

		tourState = new TourState(POIRegistry.getDefault(),
				new SharedPreferencesKeyValueStore(getActivity(),
						TourState.STORE_NAME));

		// initialize location services

		if (manager == null) {
			getActivity();
			manager = (LocationManager) getActivity().getSystemService(
					Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			provider = manager.getBestProvider(criteria, true);

			listener = new UserLocationListener();
		}

	}

	/**
	 * Set up map view with overlays for points of interest, current location
	 * and tour.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreateView");

		if (mapView == null) {
			mapView = new MapView(getActivity(), null);

			mapView.setTileSource(TileSourceFactory.MAPNIK);
			mapView.setClickable(true);
			mapView.setBuiltInZoomControls(true);

			MapController mapController = mapView.getController();

			if (savedInstanceState == null) {
				// With the Mapnik server, this zoom level results in a map
				// which
				// encompasses most of the UBC campus.
				mapController.setZoom(mapView.getMaxZoomLevel() - 4);

				// Center the map on ICICS.
				mapController.setCenter(ICICS_GEOPOINT);
			} else {
				// restore previous zoom level and map centre
				mapController.setZoom(savedInstanceState.getInt("zoomLevel"));
				int lat = savedInstanceState.getInt("latE6");
				int lon = savedInstanceState.getInt("lonE6");
				GeoPoint cntr = new GeoPoint(lat, lon);
				mapController.setCenter(cntr);
			}

			poiOverlay = createPOIOverlay();
			tourOverlay = createTourOverlay();
			routeToTourOverlay = createRouteToTourOverlay();
			myLocationOverlay = createMyLocationOverlay();

			// Order matters: overlays added later are displayed on top of
			// overlays added earlier.
			mapView.getOverlays().add(tourOverlay);
			mapView.getOverlays().add(routeToTourOverlay);
			mapView.getOverlays().add(poiOverlay);
			mapView.getOverlays().add(myLocationOverlay);
		}

		return mapView;
	}

	/**
	 * When view is destroyed, remove map view from its parent so that it can be
	 * added again when view is re-created. Stop route retriever threads as the
	 * view is about to be destroyed.
	 */
	@Override
	public void onDestroyView() {
		Log.d(LOG_TAG, "onDestroyView");

		// interrupt the RouteRetrieverThreads
		if (poiRouteRetriever != null)
			poiRouteRetriever.interrupt();

		if (toTourRouteRetriever != null)
			toTourRouteRetriever.interrupt();

		 // fix leak IntentReceiver bug by getting the MapTileProviderBase 
        // to unregister its BroadcastReceiver in detach() method
        mapView.getTileProvider().detach(); 
        
		((ViewGroup) mapView.getParent()).removeView(mapView);

		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "onDestroy");

		mapView = null;

		super.onDestroy();
	}

	/**
	 * Update the overlays based on the selected POI's and the user's current
	 * location. Request location updates.
	 */
	@Override
	public void onResume() {
		Log.d(LOG_TAG, "onResume");
		selectedPOIs = tourState.getSelectedPOIs();
		if(selectedPOIs.size() > 1)selectedPOIs.add(tourState.getSelectedPOIs().get(0));
		updateTour(selectedPOIs);

		Location location = manager.getLastKnownLocation(provider);
		updateLocation(location);

		// Request location updates at most every 10 seconds
		// or with location changes of at least 25 metres
		manager.requestLocationUpdates(provider, 10000, 25, listener);

		super.onResume();
	}

	/**
	 * Cancel location updates.
	 */
	@Override
	public void onPause() {
		Log.d(LOG_TAG, "onPause");

		// cancel location updates
		manager.removeUpdates(listener);

		super.onPause();
	}

	/**
	 * Save map's zoom level and centre.
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mapView != null) {
			outState.putInt("zoomLevel", mapView.getZoomLevel());
			IGeoPoint cntr = mapView.getMapCenter();
			outState.putInt("latE6", cntr.getLatitudeE6());
			outState.putInt("lonE6", cntr.getLongitudeE6());
			Log.i("MapSave", "Zoom: " + mapView.getZoomLevel());
		}
	}

	/**
	 * Selected POIs has changed so update tour, location and repaint.
	 */
	void update() {
		selectedPOIs = tourState.getSelectedPOIs();
		if(selectedPOIs.size() > 1)selectedPOIs.add(tourState.getSelectedPOIs().get(0));
		updateTour(selectedPOIs);

		// update user's location with last known location
		Location location = manager.getLastKnownLocation(provider);
		updateLocation(location);

		mapView.invalidate();
	}

	/**
	 * Update the POI markers and the route connecting them.
	 */
	private void updateTour(List<PointOfInterest> pois) {
		// TODO: IMPLEMENT ME!
		// First plot selected points of interest
		poiOverlay.removeAllItems();
		plotPOI(poiOverlay, pois);
		// Then determine points on tour and use helper methods in this class to
		// retrieve the tour and display it on the map
		tourOverlay.clearPath();
		List<LatLong> lol = new ArrayList<LatLong>();
		if (pois != null)
		{
			for(PointOfInterest p: pois)
			{
				LatLong latlon = p.getLatLong();
				lol.add(latlon);
				double lat = p.getLatLong().getLatitude();
				double lon = p.getLatLong().getLongitude();
				GeoPoint gp = new GeoPoint(lat, lon);
				tourOverlay.addPoint(gp);
			}
		}
		if(pois.size() > 1)
		{
			
			double lat0 = pois.get(0).getLatLong().getLatitude();
			double lon0 = pois.get(0).getLatLong().getLongitude();
			GeoPoint point0 = new GeoPoint(lat0, lon0);
			tourOverlay.addPoint(point0); //without this the route does not connect into a circle
			findRouteAndUpdateOverlay(poiRouteRetriever, tourOverlay, lol, true);
		}
	}

	/**
	 * Plot a POI on the specified overlay.
	 */
	private void plotPOI(ItemizedIconOverlay<OverlayItem> overlay,
			List<PointOfInterest> pois) {
		// TODO: IMPLEMENT ME!
		for (PointOfInterest p: pois)
		{
			double lat = p.getLatLong().getLatitude();
			double lon = p.getLatLong().getLongitude();
			GeoPoint geoP = new GeoPoint(lat, lon);
			OverlayItem GPoverlay = new OverlayItem(p.getDisplayName(), p.getDescription(), geoP);
			overlay.addItem(GPoverlay);
		}
	}

	/**
	 * Given a location and a list of POI's, find the POI closest to the
	 * specified location.
	 * 
	 * This is based on "line-of-sight" distance between points, using an
	 * approximation which works okay for short distances (surface of the earth
	 * is approximated by a plane).
	 */
	private PointOfInterest findClosestPOI(Location location,
			List<PointOfInterest> pois) {
		double approxLatitude = pois.get(0).getLatLong().getLatitude();

		PointOfInterest closest = null;
		double minDistValue = Double.MAX_VALUE;

		LatLong locationLatLong = new LatLong(location.getLatitude(),
				location.getLongitude());

		for (PointOfInterest poi : pois) {
			double distValue = getDistanceValue(approxLatitude,
					locationLatLong, poi.getLatLong());
			if (distValue < minDistValue) {
				minDistValue = distValue;
				closest = poi;
			}
		}

		return closest;
	}

	/**
	 * Get a value representing the "line-of-sight" distance between two points,
	 * using an approximation which works okay for short distances (surface of
	 * the earth is approximated by a plane).
	 * 
	 * The value returned is only usable for comparison purposes (i.e. it has a
	 * nonlinear relationship to the actual distance).
	 */
	private double getDistanceValue(double approxLatitude, LatLong pointA,
			LatLong pointB) {
		double latAdjust = Math.cos(Math.PI * approxLatitude / 180.0);
		double latDiff = pointA.getLatitude() - pointB.getLatitude();
		double longDiff = pointA.getLongitude() - pointB.getLongitude();

		return Math.pow(latDiff, 2) + Math.pow(latAdjust * longDiff, 2);
	}

	/**
	 * Create the overlay for POI markers.
	 */
	private ItemizedIconOverlay<OverlayItem> createPOIOverlay() {
		ResourceProxy rp = new DefaultResourceProxyImpl(getActivity());

		OnItemGestureListener<OverlayItem> gestureListener = new OnItemGestureListener<OverlayItem>() {
			/**
			 * Display POI's title and description in dialog box when user taps
			 * it.
			 * 
			 * @param index
			 *            index of item tapped
			 * @param oi
			 *            the OverlayItem that was tapped
			 * @return true to indicate that tap event has been handled
			 */
			@Override
			public boolean onItemSingleTapUp(int index, OverlayItem oi) {
				new AlertDialog.Builder(getActivity())
						.setPositiveButton(R.string.ok_btn, null)
						.setTitle(oi.getTitle()).setMessage(oi.getSnippet())
						.show();
				return true;
			}

			@Override
			public boolean onItemLongPress(int index, OverlayItem oi) {
				// TODO: implement this and return true.
				// Long pressing on a POI marker should create an AlertDialog
				// which displays the directions from the user's location
				// to the selected point of interest
				
				// HINT: use index to determine which POI is being selected
				String p = selectedPOIs.get(index).getDisplayName();
				String dir = getDirections(index);
				String title = "Direction to "+p;
				new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(dir).show();
				return true;
			}
		};

		return new ItemizedIconOverlay<OverlayItem>(
				new ArrayList<OverlayItem>(), getResources().getDrawable(
						R.drawable.map_pin_blue), gestureListener, rp);
	}

	private String getDirections(int index){
		// TODO ??
		String result = "";
		if(myLocation == null){
			return "User's location unknown: unable to find directions";
		}
		double lat = myLocation.getLatitude();
		double lon = myLocation.getLongitude();
		LatLong latlon = new LatLong(lat, lon);
		PointOfInterest target = tourState.getSelectedPOIs().get(index);
		if(target == null){
			return "Unknown POI destination: unable to find directions";
		}
		LatLong dest_latlon = target.getLatLong();
		try {
			result += ((UBCSustainabilityAppActivity)getActivity()).getRoutingService().getDirections(latlon, dest_latlon);
		} catch (IOException e) {
			e.printStackTrace();
		}
		result = result.replaceAll("<br>", " ");
		return result;
	}
	/**
	 * Create the overlay for the user's current location.
	 */
	private SimpleLocationOverlay createMyLocationOverlay() {
		ResourceProxy rp = new DefaultResourceProxyImpl(getActivity());
		return new SimpleLocationOverlay(getActivity(), rp);
	}

	/**
	 * Create the overlay for the route connecting the selected POI's.
	 */
	private PathOverlay createTourOverlay() {
		PathOverlay po = new PathOverlay(Color.RED, getActivity());
		Paint pathPaint = new Paint();
		pathPaint.setColor(Color.RED);
		pathPaint.setStrokeWidth(4.0f);
		pathPaint.setStyle(Style.STROKE);
		po.setPaint(pathPaint);
		return po;
	}

	/**
	 * Create the overlay connecting the user's current location to the closest
	 * selected POI.
	 */
	private PathOverlay createRouteToTourOverlay() {
		PathOverlay po = new PathOverlay(Color.BLUE, getActivity());
		Paint pathPaint = new Paint();
		pathPaint.setColor(Color.MAGENTA);
		pathPaint.setStrokeWidth(4.0f);
		pathPaint.setStyle(Style.STROKE);
		po.setPaint(pathPaint);
		return po;
	}

	/**
	 * Halts current route retriever service if it's running. Creates new route
	 * retriever and calls the routing service to obtain a route which connects
	 * the specified list of lat/long points.
	 * 
	 * @param retriever
	 *            current route retriever
	 * @param overlay
	 *            The way overlay which will be updated with the resulting
	 *            route.
	 * @param points
	 *            Points which the route must pass through.
	 * @param useCache
	 *            If set to true, the routing service will return a cached route
	 *            if one is available (and will cache the result if no cached
	 *            route is found).
	 * @return new route retriever instance
	 */
	private RouteRetriever findRouteAndUpdateOverlay(RouteRetriever retriever,
			PathOverlay overlay, List<LatLong> points, boolean useCache) {
		// Retrieve routes in a separate thread, as it can take some time and we
		// do not want to
		// block the UI thread.
		if (retriever != null && retriever.isAlive()) { // thread is still
														// running so interrupt
														// it
			retriever.interrupt();
			overlay.clearPath();
		}

		retriever = new RouteRetriever(overlay, points, useCache);
		retriever.start();
		return retriever;
	}

	/**
	 * Add a route to the specified overlay.
	 */
	private void addRouteToOverlay(PathOverlay overlay, List<LatLong> waypoints) {
		// TODO: IMPLEMENT ME!
		// Hint: this method is very similar to plotPOI except that it receives a PathOverlay object, rather than an ItemizedIconOverlay object as a parameter.  
		overlay.clearPath();
		for (LatLong loc: waypoints)
		{
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			GeoPoint geoP = new GeoPoint(lat, lon);
			overlay.addPoint(geoP);
		}
	}

	/**
	 * Calls the routing service to obtain a route which connects the specified
	 * list of lat/long points, and updates the overlay provided with the
	 * resulting route.
	 * 
	 * Routes are retrieved in a separate thread, as it can take some time and
	 * we do not want to block the UI thread.
	 */
	private class RouteRetriever extends Thread {
		private PathOverlay overlay;
		private List<LatLong> points;
		private boolean useCache;
		private boolean routeRetrieved;

		public RouteRetriever(PathOverlay overlay, List<LatLong> points,
				boolean useCache) {
			this.overlay = overlay;
			this.points = points;
			this.useCache = useCache;
			this.routeRetrieved = false;
		}

		@Override
		public void run() {

			try {
				if (points.size() > 1) {
					int i = 1;
					final List<LatLong> waypoints = new ArrayList<LatLong>();

					while (i < points.size() && !isInterrupted()) {
						LatLong currPoint = points.get(i - 1);
						LatLong nextPoint = points.get(i);
						RouteInfo info = routingService.getRoute(currPoint,
								nextPoint, useCache);

						if (info != null) {
							waypoints.add(currPoint);
							waypoints.addAll(info.getWaypoints());
							waypoints.add(nextPoint);
						}

						i++;
					}

					if (!isInterrupted()) {
						// Updates to the UI must run on the UI thread.
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								addRouteToOverlay(overlay, waypoints);
								mapView.invalidate();
							}

						});

						routeRetrieved = true;
					}
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "Error retrieving route from route service");

			} finally {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// display message to user
						if (!routeRetrieved) {
							Toast toast = Toast.makeText(getActivity(),
									R.string.rs_na_label, Toast.LENGTH_SHORT);
							toast.show();
						}
					}
				});
			}
		}
	}

	/*000
	 * update user's location
	 */
	public void updateLocation(Location location) {
		myLocation = location;
		if (location != null) {
			GeoPoint point = new GeoPoint(location.getLatitude(),
					location.getLongitude());
			myLocationOverlay.setLocation(point);
		}

		routeToTourOverlay.clearPath();
		if (selectedPOIs != null && location != null && selectedPOIs.size() > 0) {
			PointOfInterest closestPOI = findClosestPOI(location, selectedPOIs);
			List<LatLong> latLongs = new ArrayList<LatLong>();
			LatLong latLong = new LatLong(location.getLatitude(),
					location.getLongitude());
			latLongs.add(latLong);
			latLongs.add(closestPOI.getLatLong());

			toTourRouteRetriever = findRouteAndUpdateOverlay(
					toTourRouteRetriever, routeToTourOverlay, latLongs, true);
		}
	}

	private class UserLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			updateLocation(location);

		}

		@Override
		public void onProviderDisabled(String provider) {
			// Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Auto-generated method stub

		}

	}
}