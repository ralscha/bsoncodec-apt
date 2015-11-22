/**
 * Copyright 2015-2015 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.rasc.bsoncodec.test.empty;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument(storeNullValues = true, storeEmptyCollections = true)
public class StoreEmptyAndNullPojo {
	private ObjectId id;

	private String nullValue;

	private String nonNullValue;

	private List<Integer> nullList;

	private List<Integer> emptyList;

	private List<String> notEmptyList;

	private Map<String, Long> nullMap;

	private Map<String, Long> emptyMap;

	private Map<String, Long> notEmptyMap;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<Integer> getEmptyList() {
		return this.emptyList;
	}

	public void setEmptyList(List<Integer> emptyList) {
		this.emptyList = emptyList;
	}

	public List<String> getNotEmptyList() {
		return this.notEmptyList;
	}

	public List<Integer> getNullList() {
		return this.nullList;
	}

	public void setNullList(List<Integer> nullList) {
		this.nullList = nullList;
	}

	public void setNotEmptyList(List<String> notEmptyList) {
		this.notEmptyList = notEmptyList;
	}

	public String getNullValue() {
		return this.nullValue;
	}

	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	public String getNonNullValue() {
		return this.nonNullValue;
	}

	public void setNonNullValue(String nonNullValue) {
		this.nonNullValue = nonNullValue;
	}

	public Map<String, Long> getNullMap() {
		return this.nullMap;
	}

	public void setNullMap(Map<String, Long> nullMap) {
		this.nullMap = nullMap;
	}

	public Map<String, Long> getEmptyMap() {
		return this.emptyMap;
	}

	public void setEmptyMap(Map<String, Long> emptyMap) {
		this.emptyMap = emptyMap;
	}

	public Map<String, Long> getNotEmptyMap() {
		return this.notEmptyMap;
	}

	public void setNotEmptyMap(Map<String, Long> notEmptyMap) {
		this.notEmptyMap = notEmptyMap;
	}

}
