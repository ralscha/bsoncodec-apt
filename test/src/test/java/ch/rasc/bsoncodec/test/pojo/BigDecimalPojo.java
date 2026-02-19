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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class BigDecimalPojo {

	private ObjectId id;

	private BigDecimal scalar;
	private BigDecimal[] array;
	private BigDecimal[][] array2;
	private List<BigDecimal> list;
	private Set<BigDecimal> set;
	private Map<String, BigDecimal> map;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public BigDecimal getScalar() {
		return this.scalar;
	}

	public void setScalar(BigDecimal scalar) {
		this.scalar = scalar;
	}

	public BigDecimal[] getArray() {
		return this.array;
	}

	public void setArray(BigDecimal[] array) {
		this.array = array;
	}

	public BigDecimal[][] getArray2() {
		return this.array2;
	}

	public void setArray2(BigDecimal[][] array2) {
		this.array2 = array2;
	}

	public List<BigDecimal> getList() {
		return this.list;
	}

	public void setList(List<BigDecimal> list) {
		this.list = list;
	}

	public Set<BigDecimal> getSet() {
		return this.set;
	}

	public void setSet(Set<BigDecimal> set) {
		this.set = set;
	}

	public Map<String, BigDecimal> getMap() {
		return this.map;
	}

	public void setMap(Map<String, BigDecimal> map) {
		this.map = map;
	}

}
