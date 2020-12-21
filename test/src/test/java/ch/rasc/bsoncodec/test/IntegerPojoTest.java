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

import ch.rasc.bsoncodec.test.pojo.IntegerPojo;
import ch.rasc.bsoncodec.test.pojo.IntegerPojoCodec;

public class IntegerPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "ints";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries
						.fromCodecs(new IntegerPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static IntegerPojo insert(MongoDatabase db) {
		MongoCollection<IntegerPojo> coll = db.getCollection(COLL_NAME,
				IntegerPojo.class);
		IntegerPojo pojo = new IntegerPojo();
		pojo.setScalarPrimitive(1);
		pojo.setScalar(2);
		pojo.setArrayPrimitive(new int[] { 3, 3, 3 });
		pojo.setArray(new Integer[] { 6, 7, 8 });
		pojo.setArray2Primitive(new int[][] { { 11, 12 }, { 21, 22 } });
		pojo.setArray2(new Integer[][] { { 111, 112 }, { 221, 222 } });
		pojo.setList(Arrays.asList(10, 11));

		Set<Integer> set = new HashSet<>();
		set.add(12);
		set.add(13);
		pojo.setSet(set);

		Map<String, Integer> map = new HashMap<>();
		map.put("one", 1);
		map.put("two", 2);
		map.put("three", 3);
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static IntegerPojo insertEmpty(MongoDatabase db) {
		MongoCollection<IntegerPojo> coll = db.getCollection(COLL_NAME,
				IntegerPojo.class);
		IntegerPojo pojo = new IntegerPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		IntegerPojo pojo = insert(db);

		MongoCollection<IntegerPojo> coll = db.getCollection(COLL_NAME,
				IntegerPojo.class);
		IntegerPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);

		IntegerPojo empty = coll.find().projection(Projections.include("id")).first();
		assertThat(empty.getScalarPrimitive()).isEqualTo(0);
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
		IntegerPojo pojo = insertEmpty(db);

		MongoCollection<IntegerPojo> coll = db.getCollection(COLL_NAME,
				IntegerPojo.class);
		IntegerPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		IntegerPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(10);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo(pojo.getScalarPrimitive());
		assertThat(doc.get("scalar")).isEqualTo(pojo.getScalar().intValue());
		assertThat((List<Integer>) doc.get("arrayPrimitive")).containsExactly(3, 3, 3);
		assertThat((List<Integer>) doc.get("array")).containsExactly(6, 7, 8);
		assertThat((List<List<Integer>>) doc.get("array2Primitive"))
				.containsExactly(Arrays.asList(11, 12), Arrays.asList(21, 22));
		assertThat((List<List<Integer>>) doc.get("array2"))
				.containsExactly(Arrays.asList(111, 112), Arrays.asList(221, 222));
		assertThat((List<Integer>) doc.get("list")).containsExactly(10, 11);
		assertThat((List<Integer>) doc.get("set")).containsOnly(12, 13);

		assertThat((Map<String, Integer>) doc.get("map")).containsOnly(
				MapEntry.entry("one", 1), MapEntry.entry("two", 2),
				MapEntry.entry("three", 3), MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		IntegerPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo(0);
	}

}
