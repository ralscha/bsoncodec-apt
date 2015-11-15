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
package ch.rasc.bsoncodec.test.empty;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class EmptyAndNullTest extends AbstractMongoDBTest {

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(
						new ch.rasc.bsoncodec.test.empty.PojoCodecProvider()));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@Test
	public void testInsertPojoWithNullValues() {
		MongoDatabase db = connect();

		StoreNullPojo pojo = new StoreNullPojo();
		pojo.setPrimitive(33);
		pojo.setString(null);
		pojo.setWrapper(null);
		pojo.setStringWithData("ralph");
		pojo.setWrapperWithData(77);

		MongoCollection<StoreNullPojo> coll = db.getCollection("StoreNullPojo",
				StoreNullPojo.class);
		coll.insertOne(pojo);

		StoreNullPojo readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		Document doc = db.getCollection("StoreNullPojo").find().first();
		assertThat(doc).hasSize(6);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("primitive")).isEqualTo(33);
		assertThat(doc.get("wrapper")).isNull();
		assertThat(doc.get("string")).isNull();
		assertThat(doc.get("wrapperWithData")).isEqualTo(77);
		assertThat(doc.get("stringWithData")).isEqualTo("ralph");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertPojoWithEmptyCollection() {
		MongoDatabase db = connect();

		StoreEmptyPojo pojo = new StoreEmptyPojo();
		pojo.setNullList(null);
		pojo.setEmptyList(Collections.emptyList());
		pojo.setNotEmptyList(Arrays.asList("a", "b", "c"));
		pojo.setNullValue(null);
		pojo.setNonNullValue("ralph");

		MongoCollection<StoreEmptyPojo> coll = db.getCollection("StoreEmptyPojo",
				StoreEmptyPojo.class);
		coll.insertOne(pojo);

		StoreEmptyPojo readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		Document doc = db.getCollection("StoreEmptyPojo").find().first();
		assertThat(doc).hasSize(4);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat((List<String>) doc.get("emptyList")).isEmpty();
		assertThat((List<String>) doc.get("notEmptyList")).containsExactly("a", "b", "c");
		assertThat(doc.get("nonNullValue")).isEqualTo("ralph");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertPojoWithEmptyCollectionAndNullValue() {
		MongoDatabase db = connect();

		StoreEmptyAndNullPojo pojo = new StoreEmptyAndNullPojo();
		pojo.setNullList(null);
		pojo.setEmptyList(Collections.emptyList());
		pojo.setNotEmptyList(Arrays.asList("a", "b", "c"));
		pojo.setNullValue(null);
		pojo.setNonNullValue("ralph");

		MongoCollection<StoreEmptyAndNullPojo> coll = db
				.getCollection("StoreEmptyAndNullPojo", StoreEmptyAndNullPojo.class);
		coll.insertOne(pojo);

		StoreEmptyAndNullPojo readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		Document doc = db.getCollection("StoreEmptyAndNullPojo").find().first();
		assertThat(doc).hasSize(6);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("nullList")).isNull();
		assertThat((List<String>) doc.get("emptyList")).isEmpty();
		assertThat((List<String>) doc.get("notEmptyList")).containsExactly("a", "b", "c");
		assertThat(doc.get("nullValue")).isNull();
		assertThat(doc.get("nonNullValue")).isEqualTo("ralph");

	}
}
