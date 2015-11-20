package ch.rasc.bsoncodec.test.hierarchy;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class User extends AbstractDocument {
	private String email;

	private String name;

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
