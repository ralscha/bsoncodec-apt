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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.data.MapEntry;
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
		pojo.setNullMap(null);
		pojo.setEmptyMap(Collections.emptyMap());
		pojo.setNotEmptyMap(Collections.singletonMap("a", 10L));

		MongoCollection<StoreEmptyPojo> coll = db.getCollection("StoreEmptyPojo",
				StoreEmptyPojo.class);
		coll.insertOne(pojo);

		StoreEmptyPojo readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		Document doc = db.getCollection("StoreEmptyPojo").find().first();
		assertThat(doc).hasSize(6);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat((List<String>) doc.get("emptyList")).isEmpty();
		assertThat((Map<String, Long>) doc.get("emptyMap")).isEmpty();
		assertThat((List<String>) doc.get("notEmptyList")).containsExactly("a", "b", "c");
		assertThat((Map<String, Long>) doc.get("notEmptyMap"))
				.containsExactly(MapEntry.entry("a", 10L));
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
		pojo.setNullMap(null);
		pojo.setEmptyMap(Collections.emptyMap());
		pojo.setNotEmptyMap(Collections.singletonMap("a", 10L));

		MongoCollection<StoreEmptyAndNullPojo> coll = db
				.getCollection("StoreEmptyAndNullPojo", StoreEmptyAndNullPojo.class);
		coll.insertOne(pojo);

		StoreEmptyAndNullPojo readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		Document doc = db.getCollection("StoreEmptyAndNullPojo").find().first();
		assertThat(doc).hasSize(9);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("nullList")).isNull();
		assertThat(doc.get("nullMap")).isNull();
		assertThat((List<String>) doc.get("emptyList")).isEmpty();
		assertThat((Map<String, Long>) doc.get("emptyMap")).isEmpty();
		assertThat((List<String>) doc.get("notEmptyList")).containsExactly("a", "b", "c");
		assertThat((Map<String, Long>) doc.get("notEmptyMap"))
				.containsExactly(MapEntry.entry("a", 10L));
		assertThat(doc.get("nullValue")).isNull();
		assertThat(doc.get("nonNullValue")).isEqualTo("ralph");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testInsertCollectionWithNullValueStoreNull() {
		MongoDatabase db = connect();
		NullValueStoreNull pojo = new NullValueStoreNull();
		pojo.setList(Arrays.asList("one", "two", null, "three", "four"));
		Map<String, Integer> map = new HashMap<>();
		map.put("one", 1);
		map.put("two", null);
		map.put("three", 3);
		pojo.setMap(map);

		MongoCollection<NullValueStoreNull> coll = db.getCollection("NullValueStoreNull",
				NullValueStoreNull.class);
		coll.insertOne(pojo);

		NullValueStoreNull readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		Document doc = db.getCollection("NullValueStoreNull").find().first();
		assertThat(doc).hasSize(3);

		List<String> list = (List<String>) doc.get("list");
		map = (Map<String, Integer>) doc.get("map");
		assertThat(list).containsExactly("one", "two", null, "three", "four");
		assertThat(map).containsOnly(MapEntry.entry("one", 1),
				MapEntry.entry("two", null), MapEntry.entry("three", 3));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testInsertCollectionWithNullValueDoNotStoreNull() {
		MongoDatabase db = connect();
		NullValueDoNotStoreNull pojo = new NullValueDoNotStoreNull();
		pojo.setList(Arrays.asList("one", "two", null, "three", "four"));
		Map<String, Integer> map = new HashMap<>();
		map.put("one", 1);
		map.put("two", null);
		map.put("three", 3);
		pojo.setMap(map);

		MongoCollection<NullValueDoNotStoreNull> coll = db
				.getCollection("NullValueDoNotStoreNull", NullValueDoNotStoreNull.class);
		coll.insertOne(pojo);

		NullValueDoNotStoreNull readPojo = coll.find().first();
		assertThat(readPojo).isEqualToIgnoringGivenFields(pojo, "list");
		assertThat(readPojo.getList()).containsExactly("one", "two", null, "three",
				"four");
		Document doc = db.getCollection("NullValueDoNotStoreNull").find().first();
		assertThat(doc).hasSize(3);

		List<String> list = (List<String>) doc.get("list");
		map = (Map<String, Integer>) doc.get("map");
		assertThat(list).containsExactly("one", "two", null, "three", "four");
		assertThat(map).containsOnly(MapEntry.entry("one", 1),
				MapEntry.entry("two", null), MapEntry.entry("three", 3));
	}
}
