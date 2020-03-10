/**
 * 
 */
package loadbalancingbroker.zmqutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author User
 *
 */
public class JsonCreator {

	private static GsonBuilder builder = new GsonBuilder();
	private static Gson gson;

	public static Gson getJsonBuilder() {
		if (gson == null) {
			builder.setPrettyPrinting().serializeNulls();
			gson = builder.create();
		}
		return gson;
	}

}
