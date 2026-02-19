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
import org.junit.jupiter.api.Test;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.StringPojo;
import ch.rasc.bsoncodec.test.pojo.StringPojoCodec;

public class StringPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "strings";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(new StringPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static StringPojo insert(MongoDatabase db) {
		MongoCollection<StringPojo> coll = db.getCollection(COLL_NAME, StringPojo.class);
		StringPojo pojo = new StringPojo();
		pojo.setScalar("a");
		pojo.setArray(new String[] { "b", "cc", "ddd" });
		pojo.setArray2(new String[][] { { "111", "112" }, { "221", "222" } });
		pojo.setList(Arrays.asList("zxy", "yxz"));

		Set<String> set = new HashSet<>();
		set.add("eee");
		set.add("fff");
		pojo.setSet(set);

		Map<Integer, String> map = new HashMap<>();
		map.put(1, "one");
		map.put(2, "two");
		map.put(3, "three");
		map.put(4, null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static StringPojo insertEmpty(MongoDatabase db) {
		MongoCollection<StringPojo> coll = db.getCollection(COLL_NAME, StringPojo.class);
		StringPojo pojo = new StringPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		StringPojo pojo = insert(db);

		MongoCollection<StringPojo> coll = db.getCollection(COLL_NAME, StringPojo.class);
		StringPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);

		StringPojo empty = coll.find().projection(Projections.include("id")).first();
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
		StringPojo pojo = insertEmpty(db);

		MongoCollection<StringPojo> coll = db.getCollection(COLL_NAME, StringPojo.class);
		StringPojo read = coll.find().first();
		assertThat(read).usingRecursiveComparison().isEqualTo(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		StringPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo("a");
		assertThat((List<String>) doc.get("array")).containsExactly("b", "cc", "ddd");
		assertThat((List<List<String>>) doc.get("array2")).containsExactly(
				Arrays.asList("111", "112"), Arrays.asList("221", "222"));
		assertThat((List<String>) doc.get("list")).containsExactly("zxy", "yxz");
		assertThat((List<String>) doc.get("set")).containsOnly("eee", "fff");

		assertThat((Map<String, String>) doc.get("map")).containsOnly(
				MapEntry.entry("1", "one"), MapEntry.entry("2", "two"),
				MapEntry.entry("3", "three"), MapEntry.entry("4", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		StringPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
