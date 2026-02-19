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

import java.math.BigDecimal;
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

import ch.rasc.bsoncodec.test.pojo.BigDecimalPojo;
import ch.rasc.bsoncodec.test.pojo.BigDecimalPojoCodec;

public class BigDecimalPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "BigDecimals";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries
						.fromCodecs(new BigDecimalPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static BigDecimalPojo insert(MongoDatabase db) {
		MongoCollection<BigDecimalPojo> coll = db.getCollection(COLL_NAME,
				BigDecimalPojo.class);
		BigDecimalPojo pojo = new BigDecimalPojo();
		pojo.setScalar(new BigDecimal("1.5"));
		pojo.setArray(new BigDecimal[] { new BigDecimal("2.1"), new BigDecimal("3.2") });
		pojo.setArray2(new BigDecimal[][] { { new BigDecimal("4.3") },
				{ new BigDecimal("5.4") } });
		pojo.setList(Arrays.asList(new BigDecimal("6.5")));

		Set<BigDecimal> set = new HashSet<>();
		set.add(new BigDecimal("7.6"));
		pojo.setSet(set);

		Map<String, BigDecimal> map = new HashMap<>();
		map.put("one", new BigDecimal("1.1"));
		map.put("two", new BigDecimal("2.2"));
		map.put("three", new BigDecimal("3.3"));
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static BigDecimalPojo insertEmpty(MongoDatabase db) {
		MongoCollection<BigDecimalPojo> coll = db.getCollection(COLL_NAME,
				BigDecimalPojo.class);
		BigDecimalPojo pojo = new BigDecimalPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		insert(db);

		MongoCollection<BigDecimalPojo> coll = db.getCollection(COLL_NAME,
				BigDecimalPojo.class);
		BigDecimalPojo read = coll.find().first();

		assertThat(read.getScalar()).isEqualByComparingTo(new BigDecimal("1.5"));
		assertThat(read.getArray()).hasSize(2);
		assertThat(read.getArray()[0]).isEqualByComparingTo(new BigDecimal("2.1"));
		assertThat(read.getArray()[1]).isEqualByComparingTo(new BigDecimal("3.2"));

		assertThat(read.getArray2()).hasDimensions(2, 1);
		assertThat(read.getArray2()[0][0]).isEqualByComparingTo(new BigDecimal("4.3"));
		assertThat(read.getArray2()[1][0]).isEqualByComparingTo(new BigDecimal("5.4"));

		assertThat(read.getList()).hasSize(1);
		assertThat(read.getSet()).hasSize(1);
		assertThat(read.getList().iterator().next()).isEqualByComparingTo(new BigDecimal("6.5"));
		assertThat(read.getSet().iterator().next()).isEqualByComparingTo(new BigDecimal("7.6"));

		BigDecimalPojo empty = coll.find().projection(Projections.include("id")).first();
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
		BigDecimalPojo pojo = insertEmpty(db);

		MongoCollection<BigDecimalPojo> coll = db.getCollection(COLL_NAME,
				BigDecimalPojo.class);
		BigDecimalPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		BigDecimalPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo("1.5");
		assertThat((List<String>) doc.get("array")).containsExactly("2.1", "3.2");
		assertThat((List<List<String>>) doc.get("array2"))
				.containsExactly(Arrays.asList("4.3"), Arrays.asList("5.4"));
		assertThat((List<String>) doc.get("list")).containsExactly("6.5");
		assertThat((List<String>) doc.get("set")).containsExactly("7.6");

		assertThat((Map<String, String>) doc.get("map")).containsOnly(
				MapEntry.entry("one", "1.1"), MapEntry.entry("two", "2.2"),
				MapEntry.entry("three", "3.3"), MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		BigDecimalPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
