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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import ch.rasc.bsoncodec.test.pojo.DatePojo;
import ch.rasc.bsoncodec.test.pojo.DatePojoCodec;

public class DatePojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "Dates";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(new DatePojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static Date createDate(int year, int month, int day) {
		return Date.from(
				LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant());
	}

	private static DatePojo insert(MongoDatabase db) {
		MongoCollection<DatePojo> coll = db.getCollection(COLL_NAME, DatePojo.class);
		DatePojo pojo = new DatePojo();
		pojo.setScalar(createDate(2015, 1, 1));
		pojo.setArray(new Date[] { createDate(2015, 2, 1), createDate(2015, 2, 2),
				createDate(2015, 2, 3) });
		pojo.setArray2(new Date[][] { { createDate(2015, 3, 1), createDate(2015, 3, 2) },
				{ createDate(2015, 4, 1), createDate(2015, 4, 2) } });
		pojo.setList(Arrays.asList(createDate(2015, 12, 20)));

		Set<Date> set = new HashSet<>();
		set.add(createDate(2015, 8, 8));
		set.add(createDate(2015, 9, 9));
		pojo.setSet(set);

		Map<Long, Date> map = new HashMap<>();
		map.put(1L, createDate(2017, 1, 1));
		map.put(2L, createDate(2017, 1, 2));
		map.put(3L, createDate(2017, 1, 3));
		map.put(4L, null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static DatePojo insertEmpty(MongoDatabase db) {
		MongoCollection<DatePojo> coll = db.getCollection(COLL_NAME, DatePojo.class);
		DatePojo pojo = new DatePojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		DatePojo pojo = insert(db);

		MongoCollection<DatePojo> coll = db.getCollection(COLL_NAME, DatePojo.class);
		DatePojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);

		DatePojo empty = coll.find().projection(Projections.include("id")).first();
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
		DatePojo pojo = insertEmpty(db);

		MongoCollection<DatePojo> coll = db.getCollection(COLL_NAME, DatePojo.class);
		DatePojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		DatePojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo(createDate(2015, 1, 1));
		assertThat((List<Date>) doc.get("array")).containsExactly(createDate(2015, 2, 1),
				createDate(2015, 2, 2), createDate(2015, 2, 3));
		assertThat((List<List<Date>>) doc.get("array2")).containsExactly(
				Arrays.asList(createDate(2015, 3, 1), createDate(2015, 3, 2)),
				Arrays.asList(createDate(2015, 4, 1), createDate(2015, 4, 2)));
		assertThat((List<Date>) doc.get("list"))
				.containsExactly(createDate(2015, 12, 20));
		assertThat((List<Date>) doc.get("set")).containsOnly(createDate(2015, 8, 8),
				createDate(2015, 9, 9));

		assertThat((Map<String, Date>) doc.get("map")).containsOnly(
				MapEntry.entry("1", createDate(2017, 1, 1)),
				MapEntry.entry("2", createDate(2017, 1, 2)),
				MapEntry.entry("3", createDate(2017, 1, 3)), MapEntry.entry("4", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		DatePojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
