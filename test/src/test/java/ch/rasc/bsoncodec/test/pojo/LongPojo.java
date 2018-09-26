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
public class LongPojo {

	private ObjectId id;

	private long scalarPrimitive;
	private Long scalar;
	private long[] arrayPrimitive;
	private Long[] array;
	private long[][] array2Primitive;
	private Long[][] array2;
	private List<Long> list;
	private Set<Long> set;
	private Map<String, Long> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public long getScalarPrimitive() {
		return this.scalarPrimitive;
	}

	public void setScalarPrimitive(long scalarPrimitive) {
		this.scalarPrimitive = scalarPrimitive;
	}

	public Long getScalar() {
		return this.scalar;
	}

	public void setScalar(Long scalar) {
		this.scalar = scalar;
	}

	public long[] getArrayPrimitive() {
		return this.arrayPrimitive;
	}

	public void setArrayPrimitive(long[] arrayPrimitive) {
		this.arrayPrimitive = arrayPrimitive;
	}

	public Long[] getArray() {
		return this.array;
	}

	public void setArray(Long[] array) {
		this.array = array;
	}

	public List<Long> getList() {
		return this.list;
	}

	public void setList(List<Long> list) {
		this.list = list;
	}

	public Set<Long> getSet() {
		return this.set;
	}

	public void setSet(Set<Long> set) {
		this.set = set;
	}

	public long[][] getArray2Primitive() {
		return this.array2Primitive;
	}

	public void setArray2Primitive(long[][] array2Primitive) {
		this.array2Primitive = array2Primitive;
	}

	public Long[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Long[][] array2) {
		this.array2 = array2;
	}

	public Map<String, Long> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Long> map) {
		this.map = map;
	}

}
