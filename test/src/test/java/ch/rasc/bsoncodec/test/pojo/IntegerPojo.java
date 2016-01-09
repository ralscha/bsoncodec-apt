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
public class IntegerPojo {

	private ObjectId id;

	private int scalarPrimitive;
	private Integer scalar;
	private int[] arrayPrimitive;
	private Integer[] array;
	private int[][] array2Primitive;
	private Integer[][] array2;
	private List<Integer> list;
	private Set<Integer> set;
	private Map<String, Integer> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getScalarPrimitive() {
		return this.scalarPrimitive;
	}

	public void setScalarPrimitive(int scalarPrimitive) {
		this.scalarPrimitive = scalarPrimitive;
	}

	public Integer getScalar() {
		return this.scalar;
	}

	public void setScalar(Integer scalar) {
		this.scalar = scalar;
	}

	public int[] getArrayPrimitive() {
		return this.arrayPrimitive;
	}

	public void setArrayPrimitive(int[] arrayPrimitive) {
		this.arrayPrimitive = arrayPrimitive;
	}

	public Integer[] getArray() {
		return this.array;
	}

	public void setArray(Integer[] array) {
		this.array = array;
	}

	public List<Integer> getList() {
		return this.list;
	}

	public void setList(List<Integer> list) {
		this.list = list;
	}

	public Set<Integer> getSet() {
		return this.set;
	}

	public void setSet(Set<Integer> set) {
		this.set = set;
	}

	public int[][] getArray2Primitive() {
		return this.array2Primitive;
	}

	public void setArray2Primitive(int[][] array2Primitive) {
		this.array2Primitive = array2Primitive;
	}

	public Integer[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Integer[][] array2) {
		this.array2 = array2;
	}

	public Map<String, Integer> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Integer> map) {
		this.map = map;
	}

}
