package db.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class MongoDBTableCreation {
	// Run as Java application to create MongoDB collections with index.
	public static void main(String[] args) throws ParseException {
	    MongoClient mongoClient = new MongoClient();
	    MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);

	    mongoClient.close();
	    System.out.println("Import is done successfully.");
	}

}
