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
package ch.rasc.bsoncodec.test.field;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;
import ch.rasc.bsoncodec.annotation.Field;
import ch.rasc.bsoncodec.annotation.Id;

@BsonDocument
public class OrderDescPojo {

	@Id
	private ObjectId id;

	@Field(order = 30)
	private String name;

	@Field(order = 20)
	private String sameName;

	@Field(order = 10)
	private int i;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSameName() {
		return this.sameName;
	}

	public void setSameName(String sameName) {
		this.sameName = sameName;
	}

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

}
