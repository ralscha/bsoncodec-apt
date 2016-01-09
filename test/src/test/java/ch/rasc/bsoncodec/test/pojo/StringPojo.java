/**
 * Copyright 2015-2016 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.rasc.bsoncodec.test.pojo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class StringPojo {

	private ObjectId id;

	private String scalar;
	private String[] array;
	private String[][] array2;
	private List<String> list;
	private Set<String> set;
	private Map<Integer, String> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getScalar() {
		return this.scalar;
	}

	public void setScalar(String scalar) {
		this.scalar = scalar;
	}

	public String[] getArray() {
		return this.array;
	}

	public void setArray(String[] array) {
		this.array = array;
	}

	public String[][] getArray2() {
		return this.array2;
	}

	public void setArray2(String[][] array2) {
		this.array2 = array2;
	}

	public List<String> getList() {
		return this.list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public Set<String> getSet() {
		return this.set;
	}

	public void setSet(Set<String> set) {
		this.set = set;
	}

	public Map<Integer, String> getMap() {
		return this.map;
	}

	public void setMap(Map<Integer, String> map) {
		this.map = map;
	}

}
