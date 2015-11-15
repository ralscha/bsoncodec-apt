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
package ch.rasc.bsoncodec.test.pojo;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class AtomicLongPojo {

	private ObjectId id;

	private AtomicLong scalar;
	private AtomicLong[] array;
	private AtomicLong[][] array2;
	private List<AtomicLong> list;
	private Set<AtomicLong> set;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public AtomicLong getScalar() {
		return this.scalar;
	}

	public void setScalar(AtomicLong scalar) {
		this.scalar = scalar;
	}

	public AtomicLong[] getArray() {
		return this.array;
	}

	public void setArray(AtomicLong[] array) {
		this.array = array;
	}

	public AtomicLong[][] getArray2() {
		return this.array2;
	}

	public void setArray2(AtomicLong[][] array2) {
		this.array2 = array2;
	}

	public List<AtomicLong> getList() {
		return this.list;
	}

	public void setList(List<AtomicLong> list) {
		this.list = list;
	}

	public Set<AtomicLong> getSet() {
		return this.set;
	}

	public void setSet(Set<AtomicLong> set) {
		this.set = set;
	}

}
