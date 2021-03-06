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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class BytePojo {

	private ObjectId id;

	private byte scalarPrimitive;
	private Byte scalar;
	private byte[] arrayPrimitive;
	private Byte[] array;
	// private byte[][] array2Primitive;
	// private Byte[][] array2;
	private List<Byte> list;
	private Set<Byte> set;
	private Map<String, Byte> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public byte getScalarPrimitive() {
		return this.scalarPrimitive;
	}

	public void setScalarPrimitive(byte scalarPrimitive) {
		this.scalarPrimitive = scalarPrimitive;
	}

	public Byte getScalar() {
		return this.scalar;
	}

	public void setScalar(Byte scalar) {
		this.scalar = scalar;
	}

	public byte[] getArrayPrimitive() {
		return this.arrayPrimitive;
	}

	public void setArrayPrimitive(byte[] arrayPrimitive) {
		this.arrayPrimitive = arrayPrimitive;
	}

	public Byte[] getArray() {
		return this.array;
	}

	public void setArray(Byte[] array) {
		this.array = array;
	}

	public List<Byte> getList() {
		return this.list;
	}

	public void setList(List<Byte> list) {
		this.list = list;
	}

	public Set<Byte> getSet() {
		return this.set;
	}

	public void setSet(Set<Byte> set) {
		this.set = set;
	}

	public Map<String, Byte> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Byte> map) {
		this.map = map;
	}

}
