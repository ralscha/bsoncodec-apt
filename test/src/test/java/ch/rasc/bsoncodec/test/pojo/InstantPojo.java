/**
 * Copyright 2015-2018 the original author or authors.
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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class InstantPojo {

	private ObjectId id;

	private Instant scalar;
	private Instant[] array;
	private Instant[][] array2;
	private List<Instant> list;
	private Set<Instant> set;
	private Map<String, Instant> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Instant getScalar() {
		return this.scalar;
	}

	public void setScalar(Instant scalar) {
		this.scalar = scalar;
	}

	public Instant[] getArray() {
		return this.array;
	}

	public void setArray(Instant[] array) {
		this.array = array;
	}

	public Instant[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Instant[][] array2) {
		this.array2 = array2;
	}

	public List<Instant> getList() {
		return this.list;
	}

	public void setList(List<Instant> list) {
		this.list = list;
	}

	public Set<Instant> getSet() {
		return this.set;
	}

	public void setSet(Set<Instant> set) {
		this.set = set;
	}

	public Map<String, Instant> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Instant> map) {
		this.map = map;
	}

}
