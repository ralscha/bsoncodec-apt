package ch.rasc.bsoncodec.test.hierarchy;

import org.bson.types.ObjectId;

public abstract class AbstractDocument {
	private ObjectId id;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

}
