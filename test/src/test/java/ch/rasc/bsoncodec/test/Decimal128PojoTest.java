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
import org.bson.codecs.Codec;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.Decimal128;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.Decimal128Pojo;
import ch.rasc.bsoncodec.test.pojo.Decimal128PojoCodec;

public class Decimal128PojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "decimal128s";

	static class Decimal128CodecProvider implements CodecProvider {
		@Override
		@SuppressWarnings("unchecked")
		public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
			if (clazz.equals(Decimal128Pojo.class)) {
				return (Codec<T>) new Decimal128PojoCodec(new ObjectIdGenerator());
			}
			return null;
		}
	}

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(new Decimal128CodecProvider()));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static Decimal128Pojo insert(MongoDatabase db) {
		MongoCollection<Decimal128Pojo> coll = db.getCollection(COLL_NAME,
				Decimal128Pojo.class);
		Decimal128Pojo pojo = new Decimal128Pojo();
		pojo.setScalar(new Decimal128(2001));
		pojo.setArray(new Decimal128[] { new Decimal128(2002), new Decimal128(2003),
				new Decimal128(2004) });
		pojo.setArray2(new Decimal128[][] { { new Decimal128(2005) },
				{ new Decimal128(2006), new Decimal128(2007) } });
		pojo.setList(Arrays.asList(new Decimal128(2008), new Decimal128(2009)));

		Set<Decimal128> set = new HashSet<>();
		set.add(new Decimal128(2012));
		pojo.setSet(set);

		Map<String, Decimal128> map = new HashMap<>();
		map.put("one", new Decimal128(2011));
		map.put("two", new Decimal128(2012));
		map.put("three", new Decimal128(2013));
		map.put("null", null);
		pojo.setMap(map);

		coll.insertOne(pojo);
		return pojo;
	}

	private static Decimal128Pojo insertEmpty(MongoDatabase db) {
		MongoCollection<Decimal128Pojo> coll = db.getCollection(COLL_NAME,
				Decimal128Pojo.class);
		Decimal128Pojo pojo = new Decimal128Pojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		Decimal128Pojo pojo = insert(db);

		MongoCollection<Decimal128Pojo> coll = db.getCollection(COLL_NAME,
				Decimal128Pojo.class);
		Decimal128Pojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);

		Decimal128Pojo empty = coll.find().projection(Projections.include("id")).first();
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
		Decimal128Pojo pojo = insertEmpty(db);

		MongoCollection<Decimal128Pojo> coll = db.getCollection(COLL_NAME,
				Decimal128Pojo.class);
		Decimal128Pojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		Decimal128Pojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo(new Decimal128(2001));
		assertThat((List<Decimal128>) doc.get("array")).containsExactly(
				new Decimal128(2002), new Decimal128(2003), new Decimal128(2004));
		assertThat((List<List<Decimal128>>) doc.get("array2")).containsExactly(
				Arrays.asList(new Decimal128(2005)),
				Arrays.asList(new Decimal128(2006), new Decimal128(2007)));
		assertThat((List<Decimal128>) doc.get("list"))
				.containsExactly(new Decimal128(2008), new Decimal128(2009));
		assertThat((List<Decimal128>) doc.get("set"))
				.containsExactly(new Decimal128(2012));
		assertThat((Map<String, Decimal128>) doc.get("map")).containsOnly(
				MapEntry.entry("one", new Decimal128(2011)),
				MapEntry.entry("two", new Decimal128(2012)),
				MapEntry.entry("three", new Decimal128(2013)),
				MapEntry.entry("null", null));
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		Decimal128Pojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
