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
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class FloatPojo {

	private ObjectId id;

	private float scalarPrimitive;
	private Float scalar;
	private float[] arrayPrimitive;
	private Float[] array;
	private float[][] array2Primitive;
	private Float[][] array2;
	private List<Float> list;
	private Set<Float> set;
	private Map<String, Float> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public float getScalarPrimitive() {
		return this.scalarPrimitive;
	}

	public void setScalarPrimitive(float scalarPrimitive) {
		this.scalarPrimitive = scalarPrimitive;
	}

	public Float getScalar() {
		return this.scalar;
	}

	public void setScalar(Float scalar) {
		this.scalar = scalar;
	}

	public float[] getArrayPrimitive() {
		return this.arrayPrimitive;
	}

	public void setArrayPrimitive(float[] arrayPrimitive) {
		this.arrayPrimitive = arrayPrimitive;
	}

	public Float[] getArray() {
		return this.array;
	}

	public void setArray(Float[] array) {
		this.array = array;
	}

	public List<Float> getList() {
		return this.list;
	}

	public void setList(List<Float> list) {
		this.list = list;
	}

	public Set<Float> getSet() {
		return this.set;
	}

	public void setSet(Set<Float> set) {
		this.set = set;
	}

	public float[][] getArray2Primitive() {
		return this.array2Primitive;
	}

	public void setArray2Primitive(float[][] array2Primitive) {
		this.array2Primitive = array2Primitive;
	}

	public Float[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Float[][] array2) {
		this.array2 = array2;
	}

	public Map<String, Float> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Float> map) {
		this.map = map;
	}

}
