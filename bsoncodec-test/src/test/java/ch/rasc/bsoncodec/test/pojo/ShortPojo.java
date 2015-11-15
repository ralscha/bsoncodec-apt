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

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class ShortPojo {

	private ObjectId id;

	private short scalarPrimitive;
	private Short scalar;
	private short[] arrayPrimitive;
	private Short[] array;
	private short[][] array2Primitive;
	private Short[][] array2;
	private List<Short> list;
	private Set<Short> set;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public short getScalarPrimitive() {
		return this.scalarPrimitive;
	}

	public void setScalarPrimitive(short scalarPrimitive) {
		this.scalarPrimitive = scalarPrimitive;
	}

	public Short getScalar() {
		return this.scalar;
	}

	public void setScalar(Short scalar) {
		this.scalar = scalar;
	}

	public short[] getArrayPrimitive() {
		return this.arrayPrimitive;
	}

	public void setArrayPrimitive(short[] arrayPrimitive) {
		this.arrayPrimitive = arrayPrimitive;
	}

	public Short[] getArray() {
		return this.array;
	}

	public void setArray(Short[] array) {
		this.array = array;
	}

	public List<Short> getList() {
		return this.list;
	}

	public void setList(List<Short> list) {
		this.list = list;
	}

	public Set<Short> getSet() {
		return this.set;
	}

	public void setSet(Set<Short> set) {
		this.set = set;
	}

	public short[][] getArray2Primitive() {
		return this.array2Primitive;
	}

	public void setArray2Primitive(short[][] array2Primitive) {
		this.array2Primitive = array2Primitive;
	}

	public Short[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Short[][] array2) {
		this.array2 = array2;
	}

}
