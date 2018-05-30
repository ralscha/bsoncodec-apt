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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.data.MapEntry;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.InstantPojo;
import ch.rasc.bsoncodec.test.pojo.InstantPojoCodec;
import ch.rasc.bsoncodec.time.InstantInt64Codec;

public class InstantPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "Instants";

	static class InstantCodecProvider implements CodecProvider {
		@Override
		@SuppressWarnings("unchecked")
		public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
			if (clazz.equals(InstantPojo.class)) {
				return (Codec<T>) new InstantPojoCodec(registry, new ObjectIdGenerator());
			}
			return null;
		}
	}

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(				
				CodecRegistries.fromProviders(new InstantCodecProvider()),
				CodecRegistries.fromCodecs(new InstantInt64Codec()),
				MongoClient.getDefaultCodecRegistry());

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static Instant createInstant(int year, int month, int day) {
		return LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant();
	}

	private static InstantPojo insert(MongoDatabase db) {
		MongoCollection<InstantPojo> coll = db.getCollection(COLL_NAME,
				InstantPojo.class);
		InstantPojo pojo = new InstantPojo();
		pojo.setScalar(createInstant(2016, 1, 1));
		pojo.setArray(new Instant[] { createInstant(2016, 2, 1),
				createInstant(2016, 2, 2), createInstant(2016, 2, 3) });
		pojo.setArray2(new Instant[][] {
				{ createInstant(2016, 3, 1), createInstant(2016, 3, 2) },
				{ createInstant(2016, 4, 1), createInstant(2016, 4, 2) } });
		pojo.setList(Arrays.asList(createInstant(2016, 12, 20)));

		Set<Instant> set = new HashSet<>();
		set.add(createInstant(2016, 8, 8));
		set.add(createInstant(2016, 9, 9));
		pojo.setSet(set);

		Map<String, Instant> map = new HashMap<>();
		map.put("one", createInstant(2017, 1, 1));
		map.put("two", createInstant(2017, 1, 2));
		map.put("three", createInstant(2017, 1, 3));
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static InstantPojo insertEmpty(MongoDatabase db) {
		MongoCollection<InstantPojo> coll = db.getCollection(COLL_NAME,
				InstantPojo.class);
		InstantPojo pojo = new InstantPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		InstantPojo pojo = insert(db);

		MongoCollection<InstantPojo> coll = db.getCollection(COLL_NAME,
				InstantPojo.class);
		InstantPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);

		InstantPojo empty = coll.find().projection(Projections.include("id")).first();
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
		InstantPojo pojo = insertEmpty(db);

		MongoCollection<InstantPojo> coll = db.getCollection(COLL_NAME,
				InstantPojo.class);
		InstantPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		InstantPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo(createInstant(2016, 1, 1).toEpochMilli());
		assertThat((List<Long>) doc.get("array")).containsExactly(
				createInstant(2016, 2, 1).toEpochMilli(),
				createInstant(2016, 2, 2).toEpochMilli(),
				createInstant(2016, 2, 3).toEpochMilli());
		assertThat((List<List<Long>>) doc.get("array2")).containsExactly(
				Arrays.asList(createInstant(2016, 3, 1).toEpochMilli(),
						createInstant(2016, 3, 2).toEpochMilli()),
				Arrays.asList(createInstant(2016, 4, 1).toEpochMilli(),
						createInstant(2016, 4, 2).toEpochMilli()));
		assertThat((List<Long>) doc.get("list"))
				.containsExactly(createInstant(2016, 12, 20).toEpochMilli());
		assertThat((List<Long>) doc.get("set")).containsOnly(
				createInstant(2016, 8, 8).toEpochMilli(),
				createInstant(2016, 9, 9).toEpochMilli());

		assertThat((Map<String, Long>) doc.get("map")).containsOnly(
				MapEntry.entry("one", createInstant(2017, 1, 1).toEpochMilli()),
				MapEntry.entry("two", createInstant(2017, 1, 2).toEpochMilli()),
				MapEntry.entry("three", createInstant(2017, 1, 3).toEpochMilli()),
				MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		InstantPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
