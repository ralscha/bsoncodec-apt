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
package ch.rasc.bsoncodec.test.empty;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument(storeNullValues = true)
public class StoreNullPojo {

	private ObjectId id;

	private int primitive;

	private Integer wrapper;

	private String string;

	private Integer wrapperWithData;

	private String stringWithData;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getPrimitive() {
		return this.primitive;
	}

	public void setPrimitive(int primitive) {
		this.primitive = primitive;
	}

	public Integer getWrapper() {
		return this.wrapper;
	}

	public void setWrapper(Integer wrapper) {
		this.wrapper = wrapper;
	}

	public String getString() {
		return this.string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public Integer getWrapperWithData() {
		return this.wrapperWithData;
	}

	public void setWrapperWithData(Integer wrapperWithData) {
		this.wrapperWithData = wrapperWithData;
	}

	public String getStringWithData() {
		return this.stringWithData;
	}

	public void setStringWithData(String stringWithData) {
		this.stringWithData = stringWithData;
	}

}
