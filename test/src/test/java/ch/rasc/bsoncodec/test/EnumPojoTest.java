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
import java.util.EnumMap;
import java.util.EnumSet;
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
import org.junit.Test;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.Day;
import ch.rasc.bsoncodec.test.pojo.EnumPojo;
import ch.rasc.bsoncodec.test.pojo.EnumPojoCodec;

public class EnumPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "enums";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(new EnumPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static EnumPojo insert(MongoDatabase db) {
		MongoCollection<EnumPojo> coll = db.getCollection(COLL_NAME, EnumPojo.class);
		EnumPojo pojo = new EnumPojo();
		pojo.setScalar(Day.MONDAY);
		pojo.setArray(new Day[] { Day.TUESDAY, Day.WEDNESDAY });
		pojo.setArray2(new Day[][] { { Day.SATURDAY }, { Day.SUNDAY } });
		pojo.setList(Arrays.asList(Day.THURSDAY));
		pojo.setEnumSet1(EnumSet.of(Day.SUNDAY));
		pojo.setEnumSet2(EnumSet.allOf(Day.class));
		pojo.setEnumSet3(EnumSet.noneOf(Day.class));

		Set<Day> set = new HashSet<>();
		set.add(Day.MONDAY);
		set.add(Day.FRIDAY);
		pojo.setSet(set);

		Map<String, Day> map = new HashMap<>();
		map.put("one", Day.MONDAY);
		map.put("two", Day.TUESDAY);
		map.put("three", Day.WEDNESDAY);
		map.put("null", null);
		pojo.setMap(map);

		EnumMap<Day, Integer> enumMap = new EnumMap<>(Day.class);
		enumMap.put(Day.MONDAY, 1);
		enumMap.put(Day.FRIDAY, 5);
		enumMap.put(Day.SATURDAY, 6);
		pojo.setEnumMap(enumMap);

		coll.insertOne(pojo);
		return pojo;
	}

	private static EnumPojo insertEmpty(MongoDatabase db) {
		MongoCollection<EnumPojo> coll = db.getCollection(COLL_NAME, EnumPojo.class);
		EnumPojo pojo = new EnumPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		EnumPojo pojo = insert(db);

		MongoCollection<EnumPojo> coll = db.getCollection(COLL_NAME, EnumPojo.class);
		EnumPojo read = coll.find().first();

		assertThat(read).usingRecursiveComparison().
		ignoringFields("enumSet3").isEqualTo(pojo);
		assertThat(read.getEnumSet3()).isNull();

		EnumPojo empty = coll.find().projection(Projections.include("id")).first();
		assertThat(empty.getScalar()).isNull();
		assertThat(empty.getArray()).isNull();
		assertThat(empty.getArray2()).isNull();
		assertThat(empty.getList()).isNull();
		assertThat(empty.getSet()).isNull();
		assertThat(empty.getEnumSet1()).isNull();
		assertThat(empty.getEnumSet2()).isNull();
		assertThat(empty.getEnumSet3()).isNull();
		assertThat(empty.getMap()).isNull();
		assertThat(empty.getEnumMap()).isNull();
	}

	@Test
	public void testInsertAndFindEmpty() {
		MongoDatabase db = connect();
		EnumPojo pojo = insertEmpty(db);

		MongoCollection<EnumPojo> coll = db.getCollection(COLL_NAME, EnumPojo.class);
		EnumPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		EnumPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(10);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo(Day.MONDAY.name());
		assertThat((List<String>) doc.get("array")).containsExactly(Day.TUESDAY.name(),
				Day.WEDNESDAY.name());
		assertThat((List<List<String>>) doc.get("array2")).containsExactly(
				Arrays.asList(Day.SATURDAY.name()), Arrays.asList(Day.SUNDAY.name()));
		assertThat((List<String>) doc.get("list")).containsExactly(Day.THURSDAY.name());
		assertThat((List<String>) doc.get("set")).containsOnly(Day.MONDAY.name(),
				Day.FRIDAY.name());

		assertThat((List<String>) doc.get("enumSet1")).containsExactly(Day.SUNDAY.name());
		assertThat((List<String>) doc.get("enumSet2")).containsExactly(Day.SUNDAY.name(),
				Day.MONDAY.name(), Day.TUESDAY.name(), Day.WEDNESDAY.name(),
				Day.THURSDAY.name(), Day.FRIDAY.name(), Day.SATURDAY.name());
		assertThat((List<String>) doc.get("enumSet3")).isNull();

		assertThat((Map<String, String>) doc.get("map")).containsOnly(
				MapEntry.entry("one", Day.MONDAY.name()),
				MapEntry.entry("two", Day.TUESDAY.name()),
				MapEntry.entry("three", Day.WEDNESDAY.name()),
				MapEntry.entry("null", null));

		assertThat((Map<String, Integer>) doc.get("enumMap")).containsOnly(
				MapEntry.entry("MONDAY", 1), MapEntry.entry("FRIDAY", 5),
				MapEntry.entry("SATURDAY", 6));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		EnumPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
