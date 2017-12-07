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
package ch.rasc.bsoncodec.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.assertj.core.data.MapEntry;
import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.AtomicBooleanPojo;
import ch.rasc.bsoncodec.test.pojo.AtomicBooleanPojoCodec;

public class AtomicBooleanPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "AtomicBooleans";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries
						.fromCodecs(new AtomicBooleanPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static AtomicBooleanPojo insert(MongoDatabase db) {
		MongoCollection<AtomicBooleanPojo> coll = db.getCollection(COLL_NAME,
				AtomicBooleanPojo.class);
		AtomicBooleanPojo pojo = new AtomicBooleanPojo();
		pojo.setScalar(new AtomicBoolean(true));
		pojo.setArray(new AtomicBoolean[] { new AtomicBoolean(true),
				new AtomicBoolean(false) });
		pojo.setArray2(new AtomicBoolean[][] { { new AtomicBoolean(false) },
				{ new AtomicBoolean(true) } });
		pojo.setList(Arrays.asList(new AtomicBoolean(true)));

		Set<AtomicBoolean> set = new HashSet<>();
		set.add(new AtomicBoolean(false));
		pojo.setSet(set);

		Map<String, AtomicBoolean> map = new HashMap<>();
		map.put("one", new AtomicBoolean(true));
		map.put("two", new AtomicBoolean(false));
		map.put("three", new AtomicBoolean(true));
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static AtomicBooleanPojo insertEmpty(MongoDatabase db) {
		MongoCollection<AtomicBooleanPojo> coll = db.getCollection(COLL_NAME,
				AtomicBooleanPojo.class);
		AtomicBooleanPojo pojo = new AtomicBooleanPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		insert(db);

		MongoCollection<AtomicBooleanPojo> coll = db.getCollection(COLL_NAME,
				AtomicBooleanPojo.class);
		AtomicBooleanPojo read = coll.find().first();

		assertThat(read.getScalar().get()).isEqualTo(true);
		assertThat(read.getArray()).hasSize(2);
		assertThat(read.getArray()[0].get()).isEqualTo(true);
		assertThat(read.getArray()[1].get()).isEqualTo(false);

		assertThat(read.getArray2()).hasSize(2);
		assertThat(read.getArray2()[0][0].get()).isEqualTo(false);
		assertThat(read.getArray2()[1][0].get()).isEqualTo(true);

		assertThat(read.getList()).hasSize(1);
		assertThat(read.getSet()).hasSize(1);
		assertThat(read.getList().iterator().next().get()).isEqualTo(true);
		assertThat(read.getSet().iterator().next().get()).isEqualTo(false);

		AtomicBooleanPojo empty = coll.find().projection(Projections.include("id"))
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
		AtomicBooleanPojo pojo = insertEmpty(db);

		MongoCollection<AtomicBooleanPojo> coll = db.getCollection(COLL_NAME,
				AtomicBooleanPojo.class);
		AtomicBooleanPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		AtomicBooleanPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo(pojo.getScalar().get());
		assertThat((List<Boolean>) doc.get("array")).containsExactly(true, false);
		assertThat((List<List<Boolean>>) doc.get("array2"))
				.containsExactly(Arrays.asList(false), Arrays.asList(true));
		assertThat((List<Boolean>) doc.get("list")).containsExactly(Boolean.TRUE);
		assertThat((List<Boolean>) doc.get("set")).containsExactly(false);

		assertThat((Map<String, Boolean>) doc.get("map")).containsOnly(
				MapEntry.entry("one", true), MapEntry.entry("two", false),
				MapEntry.entry("three", true), MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		AtomicBooleanPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
