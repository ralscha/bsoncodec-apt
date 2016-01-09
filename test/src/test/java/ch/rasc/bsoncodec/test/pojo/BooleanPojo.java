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
public class BooleanPojo {

	private ObjectId id;

	private boolean scalarPrimitive;
	private Boolean scalar;
	private boolean[] arrayPrimitive;
	private Boolean[] array;
	private boolean[][] array2Primitive;
	private Boolean[][] array2;
	private List<Boolean> list;
	private Set<Boolean> set;
	private Map<String, Boolean> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public boolean getScalarPrimitive() {
		return this.scalarPrimitive;
	}

	public void setScalarPrimitive(boolean scalarPrimitive) {
		this.scalarPrimitive = scalarPrimitive;
	}

	public Boolean getScalar() {
		return this.scalar;
	}

	public void setScalar(Boolean scalar) {
		this.scalar = scalar;
	}

	public boolean[] getArrayPrimitive() {
		return this.arrayPrimitive;
	}

	public void setArrayPrimitive(boolean[] arrayPrimitive) {
		this.arrayPrimitive = arrayPrimitive;
	}

	public Boolean[] getArray() {
		return this.array;
	}

	public void setArray(Boolean[] array) {
		this.array = array;
	}

	public List<Boolean> getList() {
		return this.list;
	}

	public void setList(List<Boolean> list) {
		this.list = list;
	}

	public Set<Boolean> getSet() {
		return this.set;
	}

	public void setSet(Set<Boolean> set) {
		this.set = set;
	}

	public boolean[][] getArray2Primitive() {
		return this.array2Primitive;
	}

	public void setArray2Primitive(boolean[][] array2Primitive) {
		this.array2Primitive = array2Primitive;
	}

	public Boolean[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Boolean[][] array2) {
		this.array2 = array2;
	}

	public Map<String, Boolean> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Boolean> map) {
		this.map = map;
	}

}
