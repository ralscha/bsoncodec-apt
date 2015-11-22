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
public class DoublePojo {

	private ObjectId id;

	private double scalarPrimitive;
	private Double scalar;
	private double[] arrayPrimitive;
	private Double[] array;
	private double[][] array2Primitive;
	private Double[][] array2;
	private List<Double> list;
	private Set<Double> set;
	private Map<String, Double> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public double getScalarPrimitive() {
		return this.scalarPrimitive;
	}

	public void setScalarPrimitive(double scalarPrimitive) {
		this.scalarPrimitive = scalarPrimitive;
	}

	public Double getScalar() {
		return this.scalar;
	}

	public void setScalar(Double scalar) {
		this.scalar = scalar;
	}

	public double[] getArrayPrimitive() {
		return this.arrayPrimitive;
	}

	public void setArrayPrimitive(double[] arrayPrimitive) {
		this.arrayPrimitive = arrayPrimitive;
	}

	public Double[] getArray() {
		return this.array;
	}

	public void setArray(Double[] array) {
		this.array = array;
	}

	public List<Double> getList() {
		return this.list;
	}

	public void setList(List<Double> list) {
		this.list = list;
	}

	public Set<Double> getSet() {
		return this.set;
	}

	public void setSet(Set<Double> set) {
		this.set = set;
	}

	public double[][] getArray2Primitive() {
		return this.array2Primitive;
	}

	public void setArray2Primitive(double[][] array2Primitive) {
		this.array2Primitive = array2Primitive;
	}

	public Double[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Double[][] array2) {
		this.array2 = array2;
	}

	public Map<String, Double> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Double> map) {
		this.map = map;
	}

}
