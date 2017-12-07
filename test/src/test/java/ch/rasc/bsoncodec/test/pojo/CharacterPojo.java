/**
 * Copyright 2015-2017 the original author or authors.
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
public class CharacterPojo {

	private ObjectId id;

	private char scalarPrimitive;
	private Character scalar;
	private char[] arrayPrimitive;
	private Character[] array;
	private char[][] array2Primitive;
	private Character[][] array2;
	private List<Character> list;
	private Set<Character> set;
	private Map<String, Character> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public char getScalarPrimitive() {
		return this.scalarPrimitive;
	}

	public void setScalarPrimitive(char scalarPrimitive) {
		this.scalarPrimitive = scalarPrimitive;
	}

	public Character getScalar() {
		return this.scalar;
	}

	public void setScalar(Character scalar) {
		this.scalar = scalar;
	}

	public char[] getArrayPrimitive() {
		return this.arrayPrimitive;
	}

	public void setArrayPrimitive(char[] arrayPrimitive) {
		this.arrayPrimitive = arrayPrimitive;
	}

	public Character[] getArray() {
		return this.array;
	}

	public void setArray(Character[] array) {
		this.array = array;
	}

	public List<Character> getList() {
		return this.list;
	}

	public void setList(List<Character> list) {
		this.list = list;
	}

	public Set<Character> getSet() {
		return this.set;
	}

	public void setSet(Set<Character> set) {
		this.set = set;
	}

	public char[][] getArray2Primitive() {
		return this.array2Primitive;
	}

	public void setArray2Primitive(char[][] array2Primitive) {
		this.array2Primitive = array2Primitive;
	}

	public Character[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Character[][] array2) {
		this.array2 = array2;
	}

	public Map<String, Character> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Character> map) {
		this.map = map;
	}

}
