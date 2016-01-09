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
package ch.rasc.bsoncodec.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
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

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.LongPojo;
import ch.rasc.bsoncodec.test.pojo.LongPojoCodec;

public class LongPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "longs";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(new LongPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static LongPojo insert(MongoDatabase db) {
		MongoCollection<LongPojo> coll = db.getCollection(COLL_NAME, LongPojo.class);
		LongPojo pojo = new LongPojo();
		pojo.setScalarPrimitive(1L);
		pojo.setScalar(2L);
		pojo.setArrayPrimitive(new long[] { 3L, 4L, 5L });
		pojo.setArray(new Long[] { 6L, 7L, 8L });
		pojo.setArray2Primitive(new long[][] { { 11L, 12L }, { 21L, 22L } });
		pojo.setArray2(new Long[][] { { 111L, 112L }, { 221L, 222L } });
		pojo.setList(Arrays.asList(10L, 11L));

		Set<Long> set = new HashSet<>();
		set.add(12L);
		set.add(13L);
		pojo.setSet(set);

		Map<String, Long> map = new HashMap<>();
		map.put("one", 1L);
		map.put("two", 2L);
		map.put("three", 3L);
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static LongPojo insertEmpty(MongoDatabase db) {
		MongoCollection<LongPojo> coll = db.getCollection(COLL_NAME, LongPojo.class);
		LongPojo pojo = new LongPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		LongPojo pojo = insert(db);

		MongoCollection<LongPojo> coll = db.getCollection(COLL_NAME, LongPojo.class);
		LongPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);

		LongPojo empty = coll.find().projection(Projections.include("id")).first();
		assertThat(empty.getScalarPrimitive()).isEqualTo(0L);
		assertThat(empty.getScalar()).isNull();
		assertThat(empty.getArray()).isNull();
		assertThat(empty.getArrayPrimitive()).isNull();
		assertThat(empty.getArray2()).isNull();
		assertThat(empty.getArray2Primitive()).isNull();
		assertThat(empty.getList()).isNull();
		assertThat(empty.getSet()).isNull();
		assertThat(empty.getMap()).isNull();
	}

	@Test
	public void testInsertAndFindEmpty() {
		MongoDatabase db = connect();
		LongPojo pojo = insertEmpty(db);

		MongoCollection<LongPojo> coll = db.getCollection(COLL_NAME, LongPojo.class);
		LongPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		LongPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(10);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo(pojo.getScalarPrimitive());
		assertThat(doc.get("scalar")).isEqualTo(pojo.getScalar().longValue());
		assertThat((List<Long>) doc.get("arrayPrimitive")).containsExactly(3L, 4L, 5L);
		assertThat((List<Long>) doc.get("array")).containsExactly(6L, 7L, 8L);
		assertThat((List<List<Long>>) doc.get("array2Primitive"))
				.containsExactly(Arrays.asList(11L, 12L), Arrays.asList(21L, 22L));
		assertThat((List<List<Long>>) doc.get("array2"))
				.containsExactly(Arrays.asList(111L, 112L), Arrays.asList(221L, 222L));
		assertThat((List<Long>) doc.get("list")).containsExactly(10L, 11L);
		assertThat((List<Long>) doc.get("set")).containsOnly(12L, 13L);

		assertThat((Map<String, Long>) doc.get("map")).containsOnly(
				MapEntry.entry("one", 1L), MapEntry.entry("two", 2L),
				MapEntry.entry("three", 3L), MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		LongPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo(0L);
	}

}
