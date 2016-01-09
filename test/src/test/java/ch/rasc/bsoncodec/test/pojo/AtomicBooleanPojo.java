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
import java.util.concurrent.atomic.AtomicBoolean;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class AtomicBooleanPojo {

	private ObjectId id;

	private AtomicBoolean scalar;
	private AtomicBoolean[] array;
	private AtomicBoolean[][] array2;
	private List<AtomicBoolean> list;
	private Set<AtomicBoolean> set;
	private Map<String, AtomicBoolean> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public AtomicBoolean getScalar() {
		return this.scalar;
	}

	public void setScalar(AtomicBoolean scalar) {
		this.scalar = scalar;
	}

	public AtomicBoolean[] getArray() {
		return this.array;
	}

	public void setArray(AtomicBoolean[] array) {
		this.array = array;
	}

	public AtomicBoolean[][] getArray2() {
		return this.array2;
	}

	public void setArray2(AtomicBoolean[][] array2) {
		this.array2 = array2;
	}

	public List<AtomicBoolean> getList() {
		return this.list;
	}

	public void setList(List<AtomicBoolean> list) {
		this.list = list;
	}

	public Set<AtomicBoolean> getSet() {
		return this.set;
	}

	public void setSet(Set<AtomicBoolean> set) {
		this.set = set;
	}

	public Map<String, AtomicBoolean> getMap() {
		return this.map;
	}

	public void setMap(Map<String, AtomicBoolean> map) {
		this.map = map;
	}

}
