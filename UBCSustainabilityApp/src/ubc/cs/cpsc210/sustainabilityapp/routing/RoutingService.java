package ubc.cs.cpsc210.sustainabilityapp.routing;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ubc.cs.cpsc210.sustainabilityapp.model.LatLong;

/**
 * Wrapper around a service which calculates routes between geographic
 * locations. This class may be called concurrently from multiple threads -- it
 * is thread-safe.
 * 
 * Currently, this class wraps the www.yournavigation.org API (<a
 * href="http://wiki.openstreetmap.org/wiki/YOURS#Routing_API"
 * >http://wiki.openstreetmap.org/wiki/YOURS#Routing_API</a>).
 */
public class RoutingService {
	private final static String LOG_TAG = "RoutingService";

	/**
	 * Caches routes retrieved by their endpoints. Access to this map must be
	 * synchronized on the map.
	 */
	private Map<RouteEndpoints, RouteInfo> routeCache = new HashMap<RouteEndpoints, RouteInfo>();

	/**
	 * Client for making HTTP requests to the API of the service.
	 */
	private HttpClient client;

	public RoutingService() {
		// Create an HttpClient with the ThreadSafeClientConnManager.
		// This connection manager must be used if more than one thread will
		// be accessing the HttpClient.
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				params, schemeRegistry);
		client = new DefaultHttpClient(cm, params);
	}

	public void shutdown() {
		if (client != null) {
			client.getConnectionManager().shutdown();
		}
	}

	/**
	 * Calculate route for given start point and end point. An internet
	 * connection must be available. See {@link getRouteFromService} for further
	 * information on route generation.
	 * 
	 * @param start
	 *            The start point of the route.
	 * @param end
	 *            The end point of the route.
	 * @param useCache
	 *            Indicates whether the service should return a cached route, if
	 *            one exists. If this flag is set to true, and a cached route is
	 *            not available, then the new route obtained from the server
	 *            will be cached.
	 * @return Information on the route calculated, including the waypoints.
	 * @throws IOException
	 *             If an error occurs while retrieving the route from the
	 *             server.
	 */
	public RouteInfo getRoute(LatLong start, LatLong end, boolean useCache)
			throws IOException {
		
		RouteEndpoints rep = new RouteEndpoints(start, end);
		RouteInfo rinfo = new RouteInfo(null);
		if (useCache)
		{
			if(getCachedRoute(rep) == null)
			{
				addRouteToCache(rep, getRouteFromService(rep));
				rinfo = getRouteFromService(rep);
			}
			if(getCachedRoute(rep) != null)
			{
				rinfo = getCachedRoute(rep);
			}
		}
		return rinfo;
		// TODO: implement me (and return a result that is not null!)
	}
	 
	/**
	     * A method for asking the routing service for written directions 
	     * from a start point to an endpoint.
	     * 
	     * @param start, end  
	     *  start and end points of the route segment 
	     * @return
	     *  String describing the directions
	     * @throws IOException
	     * @TODO consider merging this method with getRouteFromService
	     */
	public String getDirections(LatLong start, LatLong end) throws IOException {
		//TODO: implement me (and return a result that is not null!)
		String disDir = "";
		try { 
		URI uri = new URI( 
		"http://yours.cs.ubc.ca/yours/gosmore-instructions.php?" 
		+ "&flat=" + start.getLatitude() 
		+ "&flon=" + start.getLongitude() 
		+ "&tlat=" + end.getLatitude() 
		+ "&tlon=" + end.getLongitude() 
		+ "&v=foot&fast=0&format=geojson&layer=mapnik&instructions=1"); 

		HttpGet request = new HttpGet(uri); 
		ResponseHandler<String> response = new BasicResponseHandler(); 
		String result = client.execute(request, response);

		JSONObject object = (JSONObject) new JSONTokener(result) 
		.nextValue(); 
		
		disDir = object.getJSONObject("properties").getString("description");
		
		

		} catch (URISyntaxException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
		} catch (JSONException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
		} 

		return disDir;
	}

	/**
	 * Calculate route for given endpoints. Currently, we use the
	 * www.yournavigation.org API (<a
	 * href="http://wiki.openstreetmap.org/wiki/YOURS#Routing_API"
	 * >http://wiki.openstreetmap.org/wiki/YOURS#Routing_API</a>), with result
	 * format set to geojson, vehicle set to foot and route type set to shortest
	 * (rather than fastest). Using route type of fastest can result in
	 * different routes between the same two points, depending on the direction
	 * traveled.
	 * 
	 * Subclasses can override this method to connect to alternate routing
	 * services.
	 * 
	 * @param endpoints
	 *            Endpoints of the route.
	 * @return Information on the route calculated, including waypoints.
	 * @throws IOException
	 *             If an error occurs while retrieving the route from the
	 *             server.
	 */
	private RouteInfo getRouteFromService(RouteEndpoints endpoints)
			throws IOException {
		// TODO: implement me (and return a result that is not null!)
		RouteInfo route = null; 
		try { 
		URI uri = new URI( 
		"http://yours.cs.ubc.ca/yours/api/1.0/gosmore.php?format=geojson" 
		+ "&flat=" + endpoints.getStart().getLatitude() 
		+ "&flon=" + endpoints.getStart().getLongitude() 
		+ "&tlat=" + endpoints.getEnd().getLatitude() 
		+ "&tlon=" + endpoints.getEnd().getLongitude() 
		+ "&v=foot&fast=0"); 

		HttpGet request = new HttpGet(uri); 
		ResponseHandler<String> response = new BasicResponseHandler(); 

		String result = client.execute(request, response); 
		JSONObject object = (JSONObject) new JSONTokener(result) 
		.nextValue(); 
		JSONArray coordinates = object.getJSONArray("coordinates"); 
		double latitude; 
		double longitude; 
		ArrayList<LatLong> rInfo = new ArrayList<LatLong>(); 

		for (int i = 0; i < coordinates.length(); i++) { 
		longitude = coordinates.getJSONArray(i).getDouble(0); 
		latitude = coordinates.getJSONArray(i).getDouble(1); 
		LatLong latLong = new LatLong(latitude, longitude); 
		rInfo.add(latLong); 
		} 

		route = new RouteInfo(rInfo); 

		} catch (URISyntaxException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
		} catch (JSONException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
		} 

		return route; 
	}

	private RouteInfo getCachedRoute(RouteEndpoints endpoints) {
		synchronized (routeCache) {
			return routeCache.get(endpoints);
		}
	}

	private void addRouteToCache(RouteEndpoints endpoints, RouteInfo routeInfo) {
		synchronized (routeCache) {
			routeCache.put(endpoints, routeInfo);
		}
	}
}
