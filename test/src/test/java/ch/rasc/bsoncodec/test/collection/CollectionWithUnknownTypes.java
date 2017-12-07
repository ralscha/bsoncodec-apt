/**
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
