/**
 * Copyright 2015-2017 the original author or authors.
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

import ch.rasc.bsoncodec.test.pojo.CharacterPojo;
import ch.rasc.bsoncodec.test.pojo.CharacterPojoCodec;

public class CharacterPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "characters";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries
						.fromCodecs(new CharacterPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static CharacterPojo insert(MongoDatabase db) {
		MongoCollection<CharacterPojo> coll = db.getCollection(COLL_NAME,
				CharacterPojo.class);
		CharacterPojo pojo = new CharacterPojo();
		pojo.setScalarPrimitive('a');
		pojo.setScalar('b');
		pojo.setArrayPrimitive(new char[] { '3', '4', '5' });
		pojo.setArray(new Character[] { '6', '7', '8' });
		pojo.setArray2Primitive(new char[][] { { 'b', 'b' }, { 'c', 'c' } });
		pojo.setArray2(new Character[][] { { 'd', 'e' }, { 'f' } });
		pojo.setList(Arrays.asList('z', 'a'));

		Set<Character> set = new HashSet<>();
		set.add('x');
		pojo.setSet(set);

		Map<String, Character> map = new HashMap<>();
		map.put("one", 'a');
		map.put("two", 'b');
		map.put("three", 'c');
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static CharacterPojo insertEmpty(MongoDatabase db) {
		MongoCollection<CharacterPojo> coll = db.getCollection(COLL_NAME,
				CharacterPojo.class);
		CharacterPojo pojo = new CharacterPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		CharacterPojo pojo = insert(db);

		MongoCollection<CharacterPojo> coll = db.getCollection(COLL_NAME,
				CharacterPojo.class);
		CharacterPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);

		CharacterPojo empty = coll.find().projection(Projections.include("id")).first();
		assertThat(empty.getScalarPrimitive()).isEqualTo((char) 0);
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
		CharacterPojo pojo = insertEmpty(db);

		MongoCollection<CharacterPojo> coll = db.getCollection(COLL_NAME,
				CharacterPojo.class);
		CharacterPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		CharacterPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(10);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive"))
				.isEqualTo(String.valueOf(pojo.getScalarPrimitive()));
		assertThat(doc.get("scalar")).isEqualTo(pojo.getScalar().toString());
		assertThat((List<String>) doc.get("arrayPrimitive")).containsExactly("3", "4",
				"5");
		assertThat((List<String>) doc.get("array")).containsExactly("6", "7", "8");
		assertThat((List<List<String>>) doc.get("array2Primitive"))
				.containsExactly(Arrays.asList("b", "b"), Arrays.asList("c", "c"));
		assertThat((List<List<String>>) doc.get("array2"))
				.containsExactly(Arrays.asList("d", "e"), Arrays.asList("f"));
		assertThat((List<String>) doc.get("list")).containsExactly("z", "a");
		assertThat((List<String>) doc.get("set")).containsExactly("x");

		assertThat((Map<String, String>) doc.get("map")).containsOnly(
				MapEntry.entry("one", "a"), MapEntry.entry("two", "b"),
				MapEntry.entry("three", "c"), MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		CharacterPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive"))
				.isEqualTo(String.valueOf(Character.MIN_VALUE));
	}

}
