package ch.rasc.bsoncodec.test.collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@SuppressWarnings("rawtypes")
@BsonDocument
public class CollectionWithUnknownTypes {

	private ObjectId id;

	private List list;

	private Set set;

	private Map map;

	private Map<String, Object> stringObjectMap;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List getList() {
		return this.list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public Set getSet() {
		return this.set;
	}

	public void setSet(Set set) {
		this.set = set;
	}

	public Map getMap() {
		return this.map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public Map<String, Object> getStringObjectMap() {
		return this.stringObjectMap;
	}

	public void setStringObjectMap(Map<String, Object> stringObjectMap) {
		this.stringObjectMap = stringObjectMap;
	}

}
