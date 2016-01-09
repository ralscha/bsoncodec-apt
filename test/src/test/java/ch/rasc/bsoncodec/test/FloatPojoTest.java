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

import ch.rasc.bsoncodec.test.pojo.FloatPojo;
import ch.rasc.bsoncodec.test.pojo.FloatPojoCodec;

public class FloatPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "floats";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(new FloatPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static FloatPojo insert(MongoDatabase db) {
		MongoCollection<FloatPojo> coll = db.getCollection(COLL_NAME, FloatPojo.class);
		FloatPojo pojo = new FloatPojo();
		pojo.setScalarPrimitive(1.1f);
		pojo.setScalar(2.2f);
		pojo.setArrayPrimitive(new float[] { 3.5f, 4.5f, 5.5f });
		pojo.setArray(new Float[] { 6.5f, 7.5f, 8.5f });
		pojo.setArray2Primitive(new float[][] { { 11f, 12f }, { 21f, 22f } });
		pojo.setArray2(new Float[][] { { 111.5f, 112.5f }, { 221.5f, 222.5f } });
		pojo.setList(Arrays.asList(10.5f, 11.5f));

		Set<Float> set = new HashSet<>();
		set.add(12.5f);
		set.add(13.5f);
		pojo.setSet(set);

		Map<String, Float> map = new HashMap<>();
		map.put("one", 1.5f);
		map.put("two", 2.5f);
		map.put("three", 3.5f);
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static FloatPojo insertEmpty(MongoDatabase db) {
		MongoCollection<FloatPojo> coll = db.getCollection(COLL_NAME, FloatPojo.class);
		FloatPojo pojo = new FloatPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		FloatPojo pojo = insert(db);

		MongoCollection<FloatPojo> coll = db.getCollection(COLL_NAME, FloatPojo.class);
		FloatPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);

		FloatPojo empty = coll.find().projection(Projections.include("id")).first();
		assertThat(empty.getScalarPrimitive()).isEqualTo(0.0f);
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
		FloatPojo pojo = insertEmpty(db);

		MongoCollection<FloatPojo> coll = db.getCollection(COLL_NAME, FloatPojo.class);
		FloatPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		FloatPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(10);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive"))
				.isEqualTo((double) pojo.getScalarPrimitive());
		assertThat(doc.get("scalar")).isEqualTo(pojo.getScalar().doubleValue());
		assertThat((List<Double>) doc.get("arrayPrimitive")).containsExactly(3.5d, 4.5d,
				5.5d);
		assertThat((List<Double>) doc.get("array")).containsExactly(6.5d, 7.5d, 8.5d);
		assertThat((List<List<Double>>) doc.get("array2Primitive"))
				.containsExactly(Arrays.asList(11d, 12d), Arrays.asList(21d, 22d));
		assertThat((List<List<Double>>) doc.get("array2")).containsExactly(
				Arrays.asList(111.5d, 112.5d), Arrays.asList(221.5d, 222.5d));
		assertThat((List<Double>) doc.get("list")).containsExactly(10.5d, 11.5d);
		assertThat((List<Double>) doc.get("set")).containsOnly(12.5d, 13.5d);

		assertThat((Map<String, Double>) doc.get("map")).containsOnly(
				MapEntry.entry("one", 1.5d), MapEntry.entry("two", 2.5d),
				MapEntry.entry("three", 3.5d), MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		FloatPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo(0.0d);
	}

}
