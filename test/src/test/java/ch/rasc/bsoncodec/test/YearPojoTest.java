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

import java.time.Year;
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

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.YearPojo;
import ch.rasc.bsoncodec.test.pojo.YearPojoCodec;
import ch.rasc.bsoncodec.time.YearInt32Codec;

public class YearPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "years";

	static class YearCodecProvider implements CodecProvider {
		@Override
		@SuppressWarnings("unchecked")
		public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
			if (clazz.equals(YearPojo.class)) {
				return (Codec<T>) new YearPojoCodec(registry, new ObjectIdGenerator());
			}
			return null;
		}
	}

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(new YearCodecProvider()),
				CodecRegistries.fromCodecs(new YearInt32Codec()));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static YearPojo insert(MongoDatabase db) {
		MongoCollection<YearPojo> coll = db.getCollection(COLL_NAME, YearPojo.class);
		YearPojo pojo = new YearPojo();
		pojo.setScalar(Year.of(2001));
		pojo.setArray(new Year[] { Year.of(2002), Year.of(2003), Year.of(2004) });
		pojo.setArray2(
				new Year[][] { { Year.of(2005) }, { Year.of(2006), Year.of(2007) } });
		pojo.setList(Arrays.asList(Year.of(2008), Year.of(2009)));

		Set<Year> set = new HashSet<>();
		set.add(Year.of(2012));
		pojo.setSet(set);

		Map<String, Year> map = new HashMap<>();
		map.put("one", Year.of(2011));
		map.put("two", Year.of(2012));
		map.put("three", Year.of(2013));
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static YearPojo insertEmpty(MongoDatabase db) {
		MongoCollection<YearPojo> coll = db.getCollection(COLL_NAME, YearPojo.class);
		YearPojo pojo = new YearPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		YearPojo pojo = insert(db);

		MongoCollection<YearPojo> coll = db.getCollection(COLL_NAME, YearPojo.class);
		YearPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);

		YearPojo empty = coll.find().projection(Projections.include("id")).first();
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
		YearPojo pojo = insertEmpty(db);

		MongoCollection<YearPojo> coll = db.getCollection(COLL_NAME, YearPojo.class);
		YearPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		YearPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo(2001);
		assertThat((List<Integer>) doc.get("array")).containsExactly(2002, 2003, 2004);
		assertThat((List<List<Integer>>) doc.get("array2"))
				.containsExactly(Arrays.asList(2005), Arrays.asList(2006, 2007));
		assertThat((List<Integer>) doc.get("list")).containsExactly(2008, 2009);
		assertThat((List<Integer>) doc.get("set")).containsExactly(2012);
		assertThat((Map<String, Integer>) doc.get("map")).containsOnly(
				MapEntry.entry("one", 2011), MapEntry.entry("two", 2012),
				MapEntry.entry("three", 2013), MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		YearPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
