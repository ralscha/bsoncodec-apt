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

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class YearPojo {

	private ObjectId id;

	private Year scalar;
	private Year[] array;
	private Year[][] array2;
	private List<Year> list;
	private Set<Year> set;
	private Map<String, Year> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Year getScalar() {
		return this.scalar;
	}

	public void setScalar(Year scalar) {
		this.scalar = scalar;
	}

	public Year[] getArray() {
		return this.array;
	}

	public void setArray(Year[] array) {
		this.array = array;
	}

	public Year[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Year[][] array2) {
		this.array2 = array2;
	}

	public List<Year> getList() {
		return this.list;
	}

	public void setList(List<Year> list) {
		this.list = list;
	}

	public Set<Year> getSet() {
		return this.set;
	}

	public void setSet(Set<Year> set) {
		this.set = set;
	}

	public Map<String, Year> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Year> map) {
		this.map = map;
	}

}
