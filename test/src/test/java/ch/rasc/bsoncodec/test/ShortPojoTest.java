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

import ch.rasc.bsoncodec.test.pojo.ShortPojo;
import ch.rasc.bsoncodec.test.pojo.ShortPojoCodec;

public class ShortPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "shorts";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(new ShortPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static ShortPojo insert(MongoDatabase db) {
		MongoCollection<ShortPojo> coll = db.getCollection(COLL_NAME, ShortPojo.class);
		ShortPojo pojo = new ShortPojo();
		pojo.setScalarPrimitive((short) 1);
		pojo.setScalar((short) 2);
		pojo.setArrayPrimitive(new short[] { (short) 3, (short) 4, (short) 5 });
		pojo.setArray(new Short[] { (short) 6, (short) 7, (short) 8 });
		pojo.setArray2Primitive(
				new short[][] { { (short) 11, (short) 12 }, { (short) 21, (short) 22 } });
		pojo.setArray2(new Short[][] { { (short) 111, (short) 112 },
				{ (short) 221, (short) 222 } });
		pojo.setList(Arrays.asList((short) 10, (short) 11));

		Set<Short> set = new HashSet<>();
		set.add((short) 12);
		set.add((short) 13);
		pojo.setSet(set);

		Map<String, Short> map = new HashMap<>();
		map.put("one", (short) 1);
		map.put("two", (short) 2);
		map.put("three", (short) 3);
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static ShortPojo insertEmpty(MongoDatabase db) {
		MongoCollection<ShortPojo> coll = db.getCollection(COLL_NAME, ShortPojo.class);
		ShortPojo pojo = new ShortPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		ShortPojo pojo = insert(db);

		MongoCollection<ShortPojo> coll = db.getCollection(COLL_NAME, ShortPojo.class);
		ShortPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);

		ShortPojo empty = coll.find().projection(Projections.include("id")).first();
		assertThat(empty.getScalarPrimitive()).isEqualTo((short) 0);
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
		ShortPojo pojo = insertEmpty(db);

		MongoCollection<ShortPojo> coll = db.getCollection(COLL_NAME, ShortPojo.class);
		ShortPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		ShortPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(10);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo((int) pojo.getScalarPrimitive());
		assertThat(doc.get("scalar")).isEqualTo(pojo.getScalar().intValue());
		assertThat((List<Integer>) doc.get("arrayPrimitive")).containsExactly(3, 4, 5);
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
		ShortPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo(0);
	}

}
