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

import java.math.BigInteger;
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
import org.junit.jupiter.api.Test;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.BigIntegerPojo;
import ch.rasc.bsoncodec.test.pojo.BigIntegerPojoCodec;

public class BigIntegerPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "BigIntegers";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries
						.fromCodecs(new BigIntegerPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static BigIntegerPojo insert(MongoDatabase db) {
		MongoCollection<BigIntegerPojo> coll = db.getCollection(COLL_NAME,
				BigIntegerPojo.class);
		BigIntegerPojo pojo = new BigIntegerPojo();
		pojo.setScalar(new BigInteger("1"));
		pojo.setArray(new BigInteger[] { new BigInteger("2"), new BigInteger("3") });
		pojo.setArray2(new BigInteger[][] { { new BigInteger("4") },
				{ new BigInteger("5") } });
		pojo.setList(Arrays.asList(new BigInteger("6")));

		Set<BigInteger> set = new HashSet<>();
		set.add(new BigInteger("7"));
		pojo.setSet(set);

		Map<String, BigInteger> map = new HashMap<>();
		map.put("one", new BigInteger("1"));
		map.put("two", new BigInteger("2"));
		map.put("three", new BigInteger("3"));
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static BigIntegerPojo insertEmpty(MongoDatabase db) {
		MongoCollection<BigIntegerPojo> coll = db.getCollection(COLL_NAME,
				BigIntegerPojo.class);
		BigIntegerPojo pojo = new BigIntegerPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		insert(db);

		MongoCollection<BigIntegerPojo> coll = db.getCollection(COLL_NAME,
				BigIntegerPojo.class);
		BigIntegerPojo read = coll.find().first();

		assertThat(read.getScalar()).isEqualTo(new BigInteger("1"));
		assertThat(read.getArray()).hasSize(2);
		assertThat(read.getArray()[0]).isEqualTo(new BigInteger("2"));
		assertThat(read.getArray()[1]).isEqualTo(new BigInteger("3"));

		assertThat(read.getArray2()).hasDimensions(2, 1);
		assertThat(read.getArray2()[0][0]).isEqualTo(new BigInteger("4"));
		assertThat(read.getArray2()[1][0]).isEqualTo(new BigInteger("5"));

		assertThat(read.getList()).hasSize(1);
		assertThat(read.getSet()).hasSize(1);
		assertThat(read.getList().iterator().next()).isEqualTo(new BigInteger("6"));
		assertThat(read.getSet().iterator().next()).isEqualTo(new BigInteger("7"));

		BigIntegerPojo empty = coll.find().projection(Projections.include("id")).first();
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
		BigIntegerPojo pojo = insertEmpty(db);

		MongoCollection<BigIntegerPojo> coll = db.getCollection(COLL_NAME,
				BigIntegerPojo.class);
		BigIntegerPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		BigIntegerPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo("1");
		assertThat((List<String>) doc.get("array")).containsExactly("2", "3");
		assertThat((List<List<String>>) doc.get("array2"))
				.containsExactly(Arrays.asList("4"), Arrays.asList("5"));
		assertThat((List<String>) doc.get("list")).containsExactly("6");
		assertThat((List<String>) doc.get("set")).containsExactly("7");

		assertThat((Map<String, String>) doc.get("map")).containsOnly(
				MapEntry.entry("one", "1"), MapEntry.entry("two", "2"),
				MapEntry.entry("three", "3"), MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		BigIntegerPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
