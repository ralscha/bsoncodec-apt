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

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class EnumPojo {
	private ObjectId id;
	private Day scalar;
	private Day[] array;
	private Day[][] array2;
	private List<Day> list;
	private Set<Day> set;
	private EnumSet<Day> enumSet1;
	private EnumSet<Day> enumSet2;
	private EnumSet<Day> enumSet3;
	private Map<String, Day> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Day getScalar() {
		return this.scalar;
	}

	public void setScalar(Day scalar) {
		this.scalar = scalar;
	}

	public Day[] getArray() {
		return this.array;
	}

	public void setArray(Day[] array) {
		this.array = array;
	}

	public List<Day> getList() {
		return this.list;
	}

	public Day[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Day[][] array2) {
		this.array2 = array2;
	}

	public void setList(List<Day> list) {
		this.list = list;
	}

	public Set<Day> getSet() {
		return this.set;
	}

	public void setSet(Set<Day> set) {
		this.set = set;
	}

	public EnumSet<Day> getEnumSet1() {
		return this.enumSet1;
	}

	public void setEnumSet1(EnumSet<Day> enumSet1) {
		this.enumSet1 = enumSet1;
	}

	public EnumSet<Day> getEnumSet2() {
		return this.enumSet2;
	}

	public void setEnumSet2(EnumSet<Day> enumSet2) {
		this.enumSet2 = enumSet2;
	}

	public EnumSet<Day> getEnumSet3() {
		return this.enumSet3;
	}

	public void setEnumSet3(EnumSet<Day> enumSet3) {
		this.enumSet3 = enumSet3;
	}

	public Map<String, Day> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Day> map) {
		this.map = map;
	}

}
