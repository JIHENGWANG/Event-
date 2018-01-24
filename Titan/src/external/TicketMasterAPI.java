package external;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_TERM = ""; // no restriction
	private static final String API_KEY = "ea3vMExAEwa6rLmkx4tWCjfCJ0MMLfX0";
	
	
	private String urlEncodeHelper(String term) {
		try {
			term = java.net.URLEncoder.encode(term, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return term;
	}
	
	private JSONObject getVenue(JSONObject event) throws JSONException {
		return null;
	}

	private String getImageUrl(JSONObject event) throws JSONException {
		return null;
	}

	private Set<String> getCategories(JSONObject event) throws JSONException {
		return null;
	}

	// Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> list = new ArrayList<>();
		for (int i = 0; i < events.length(); i++) {
			JSONObject object = events.getJSONObject(i);
			// Parse json object fetched from Yelp API specifically.
			ItemBuilder builder = new ItemBuilder();
			// Builder pattern gives us flexibility to construct an item.
			if(!object.isNull("name")) {
				builder.setName(object.getString("name"));
			}
			if(!object.isNull("id")) {
				builder.setName(object.getString("id"));
			}
			if(!object.isNull("url")) {
				builder.setName(object.getString("url"));
			}
			if(!object.isNull("distance")) {
				builder.setDistance(object.getDouble("distance"));
			}
			JSONObject venue = getVenue(object);
			builder.setImageUrl(getImageUrl(object));
			builder.setCategories(getCategories(object));
		
			Item item = builder.build();
			list.add(item);
		}
		return list;
	}

	
	public List<Item> search(double lat, double lon, String term) {
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		if (term == null) {
			term = DEFAULT_TERM;
		}
		term = urlEncodeHelper(term);
		//apikey=12345&geoPoint=abcd&keyword=music&radius=50
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, term, 50);
		try{
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + URL + "?" + query);
			System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine = "";
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("_embedded")) {
				//return new JSONArray ();
				return new ArrayList<> ();
			}
			JSONObject embedded = obj.getJSONObject("_embedded");
			JSONArray events = embedded.getJSONArray("events");
			//return events;
			return getItemList(events);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<> ();

    }

	
	private void queryAPI(double lat, double lon) {
		List<Item> itemList = search(lat, lon, null);
		try {
			for (Item item : itemList) {
				JSONObject jsonObject = item.toJSONObject();
				System.out.println(jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*JSONArray events = search(lat, lon, null);
		try {
		    for (int i = 0; i < events.length(); i++) {
		        JSONObject event = events.getJSONObject(i);
		        System.out.println(event);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}*/


	}

	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);
	}

}
