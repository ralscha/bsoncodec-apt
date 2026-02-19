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
package ch.rasc.bsoncodec.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.data.MapEntry;
import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.jupiter.api.Test;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.AtomicIntegerPojo;
import ch.rasc.bsoncodec.test.pojo.AtomicIntegerPojoCodec;

public class AtomicIntegerPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "AtomicIntegers";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries
						.fromCodecs(new AtomicIntegerPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static AtomicIntegerPojo insert(MongoDatabase db) {
		MongoCollection<AtomicIntegerPojo> coll = db.getCollection(COLL_NAME,
				AtomicIntegerPojo.class);
		AtomicIntegerPojo pojo = new AtomicIntegerPojo();
		pojo.setScalar(new AtomicInteger(1));
		pojo.setArray(new AtomicInteger[] { new AtomicInteger(2), new AtomicInteger(3) });
		pojo.setArray2(new AtomicInteger[][] { { new AtomicInteger(4) },
				{ new AtomicInteger(5) } });
		pojo.setList(Arrays.asList(new AtomicInteger(6)));

		Set<AtomicInteger> set = new HashSet<>();
		set.add(new AtomicInteger(7));
		pojo.setSet(set);

		Map<String, AtomicInteger> map = new HashMap<>();
		map.put("one", new AtomicInteger(1));
		map.put("two", new AtomicInteger(2));
		map.put("three", new AtomicInteger(3));
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static AtomicIntegerPojo insertEmpty(MongoDatabase db) {
		MongoCollection<AtomicIntegerPojo> coll = db.getCollection(COLL_NAME,
				AtomicIntegerPojo.class);
		AtomicIntegerPojo pojo = new AtomicIntegerPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		insert(db);

		MongoCollection<AtomicIntegerPojo> coll = db.getCollection(COLL_NAME,
				AtomicIntegerPojo.class);
		AtomicIntegerPojo read = coll.find().first();

		assertThat(read.getScalar().get()).isEqualTo(1);
		assertThat(read.getArray()).hasSize(2);
		assertThat(read.getArray()[0].get()).isEqualTo(2);
		assertThat(read.getArray()[1].get()).isEqualTo(3);

		assertThat(read.getArray2()).hasDimensions(2,1);
		assertThat(read.getArray2()[0][0].get()).isEqualTo(4);
		assertThat(read.getArray2()[1][0].get()).isEqualTo(5);

		assertThat(read.getList()).hasSize(1);
		assertThat(read.getSet()).hasSize(1);
		assertThat(read.getList().iterator().next().get()).isEqualTo(6);
		assertThat(read.getSet().iterator().next().get()).isEqualTo(7);

		AtomicIntegerPojo empty = coll.find().projection(Projections.include("id"))
				.first();
		assertThat(empty.getScalar()).isNull();
		assertThat(empty.getArray()).isNull();
		assertThat(empty.getArray2()).isNull();
		assertThat(empty.getList()).isNull();
		assertThat(empty.getSet()).isNull();
		assertThat(empty.getMap()).isNull();
	}

	@Test
	public void testInsertAndFindEmpty() {
		MongoDatabase db = connect();
		AtomicIntegerPojo pojo = insertEmpty(db);

		MongoCollection<AtomicIntegerPojo> coll = db.getCollection(COLL_NAME,
				AtomicIntegerPojo.class);
		AtomicIntegerPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		AtomicIntegerPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo(pojo.getScalar().get());
		assertThat((List<Integer>) doc.get("array")).containsExactly(2, 3);
		assertThat((List<List<Integer>>) doc.get("array2"))
				.containsExactly(Arrays.asList(4), Arrays.asList(5));
		assertThat((List<Integer>) doc.get("list")).containsExactly(6);
		assertThat((List<Integer>) doc.get("set")).containsExactly(7);

		assertThat((Map<String, Integer>) doc.get("map")).containsOnly(
				MapEntry.entry("one", 1), MapEntry.entry("two", 2),
				MapEntry.entry("three", 3), MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		AtomicIntegerPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
