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
package ch.rasc.bsoncodec.test.collection;

import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;
import ch.rasc.bsoncodec.annotation.Field;

@BsonDocument
public class CollectionWithImplPojo {

	private ObjectId id;

	private List<Integer> list1;

	@Field
	private List<Integer> list2;

	@Field(collectionImplementationClass = LinkedList.class)
	private List<Integer> list3;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<Integer> getList1() {
		return this.list1;
	}

	public void setList1(List<Integer> list1) {
		this.list1 = list1;
	}

	public List<Integer> getList2() {
		return this.list2;
	}

	public void setList2(List<Integer> list2) {
		this.list2 = list2;
	}

	public List<Integer> getList3() {
		return this.list3;
	}

	public void setList3(List<Integer> list3) {
		this.list3 = list3;
	}

}
