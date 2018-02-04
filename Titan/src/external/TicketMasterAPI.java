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
	
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				if (venues.length() > 0) {
					JSONObject venue = venues.getJSONObject(0);
					if (venue != null) {
						StringBuilder sb = new StringBuilder();
						if (!venue.isNull("address")) {
							JSONObject address = venue.getJSONObject("address");
							if (!address.isNull("line1")) {
								sb.append(address.getString("line1"));
							}
							if (!address.isNull("line2")) {
								sb.append(address.getString("line2"));
							}
							if (!address.isNull("line3")) {
								sb.append(address.getString("line3"));
							}
							sb.append(",");
						}
						if (!venue.isNull("city")) {
							JSONObject city = venue.getJSONObject("city");
							if (!city.isNull("name")) {
								sb.append(city.getString("name"));
							}
						}
						return sb.toString();
					}
				}
			}
		}
		return null;
	}

	private String getImageUrl(JSONObject event) throws JSONException {
		if(!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for(int i = 0; i < array.length(); i++) {
				JSONObject image = array.getJSONObject(i);
				if(!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return null;
	}

	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray obj = event.getJSONArray("classifications");
			for (int i = 0; i < obj.length(); i++) {
				JSONObject classification = obj.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						String name = segment.getString("name");
						categories.add(name);
					}
				}
			}
			return categories;
		}
		return categories;
	}
	
	private String getStringFields(JSONObject obj, String key) throws JSONException {
		return obj.isNull(key) ? null : obj.getString(key);
	}
	
	private double getDoubleFields(JSONObject obj, String key) throws JSONException {
		return obj.isNull(key) ? 0.0 : obj.getDouble(key);
	}

	// Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> list = new ArrayList<>();
		for (int i = 0; i < events.length(); i++) {
			JSONObject object = events.getJSONObject(i);
			// Parse json object fetched from Yelp API specifically.
			ItemBuilder builder = new ItemBuilder();
		
			builder.setName(getStringFields(object, "name"));
			builder.setItemId(getStringFields(object, "id"));
			builder.setUrl(getStringFields(object, "url"));
			builder.setRating(getDoubleFields(object, "rating"));
			builder.setDistance(getDoubleFields(object, "distance"));
			
			builder.setAddress(getAddress(object));
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
