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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class DatePojo {

	private ObjectId id;

	private Date scalar;
	private Date[] array;
	private Date[][] array2;
	private List<Date> list;
	private Set<Date> set;
	private Map<Long, Date> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Date getScalar() {
		return this.scalar;
	}

	public void setScalar(Date scalar) {
		this.scalar = scalar;
	}

	public Date[] getArray() {
		return this.array;
	}

	public void setArray(Date[] array) {
		this.array = array;
	}

	public Date[][] getArray2() {
		return this.array2;
	}

	public void setArray2(Date[][] array2) {
		this.array2 = array2;
	}

	public List<Date> getList() {
		return this.list;
	}

	public void setList(List<Date> list) {
		this.list = list;
	}

	public Set<Date> getSet() {
		return this.set;
	}

	public void setSet(Set<Date> set) {
		this.set = set;
	}

	public Map<Long, Date> getMap() {
		return this.map;
	}

	public void setMap(Map<Long, Date> map) {
		this.map = map;
	}

}
