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

import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class Decimal128Pojo {

	private ObjectId id;

	private Decimal128 scalar;
	private Decimal128[] array;
	private Decimal128[][] array2;
	private List<Decimal128> list;
	private Set<Decimal128> set;
	private Map<String, Decimal128> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Decimal128 getScalar() {
		return this.scalar;
	}

	public void setScalar(Decimal128 scalar) {
		this.scalar = scalar;
	}

	public Decimal128[] getArray() {
		return this.array;
	}

	public void setArray(Decimal128[] array) {
		this.array = array;
	}

	public Decimal128[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Decimal128[][] array2) {
		this.array2 = array2;
	}

	public List<Decimal128> getList() {
		return this.list;
	}

	public void setList(List<Decimal128> list) {
		this.list = list;
	}

	public Set<Decimal128> getSet() {
		return this.set;
	}

	public void setSet(Set<Decimal128> set) {
		this.set = set;
	}

	public Map<String, Decimal128> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Decimal128> map) {
		this.map = map;
	}

}
