/**
 * Copyright 2015-2015 Ralph Schaer <ralphschaer@gmail.com>
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.AtomicLongPojo;
import ch.rasc.bsoncodec.test.pojo.AtomicLongPojoCodec;

public class AtomicLongPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "AtomicLongs";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries
						.fromCodecs(new AtomicLongPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static AtomicLongPojo insert(MongoDatabase db) {
		MongoCollection<AtomicLongPojo> coll = db.getCollection(COLL_NAME,
				AtomicLongPojo.class);
		AtomicLongPojo pojo = new AtomicLongPojo();
		pojo.setScalar(new AtomicLong(1L));
		pojo.setArray(new AtomicLong[] { new AtomicLong(2L), new AtomicLong(3L) });
		pojo.setArray2(
				new AtomicLong[][] { { new AtomicLong(4L) }, { new AtomicLong(5L) } });
		pojo.setList(Arrays.asList(new AtomicLong(6L)));

		Set<AtomicLong> set = new HashSet<>();
		set.add(new AtomicLong(7L));
		pojo.setSet(set);
		coll.insertOne(pojo);
		return pojo;
	}

	private static AtomicLongPojo insertEmpty(MongoDatabase db) {
		MongoCollection<AtomicLongPojo> coll = db.getCollection(COLL_NAME,
				AtomicLongPojo.class);
		AtomicLongPojo pojo = new AtomicLongPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		insert(db);

		MongoCollection<AtomicLongPojo> coll = db.getCollection(COLL_NAME,
				AtomicLongPojo.class);
		AtomicLongPojo read = coll.find().first();

		assertThat(read.getScalar().get()).isEqualTo(1);
		assertThat(read.getArray()).hasSize(2);
		assertThat(read.getArray()[0].get()).isEqualTo(2);
		assertThat(read.getArray()[1].get()).isEqualTo(3);

		assertThat(read.getArray2()).hasSize(2);
		assertThat(read.getArray2()[0][0].get()).isEqualTo(4);
		assertThat(read.getArray2()[1][0].get()).isEqualTo(5);

		assertThat(read.getList()).hasSize(1);
		assertThat(read.getSet()).hasSize(1);
		assertThat(read.getList().iterator().next().get()).isEqualTo(6);
		assertThat(read.getSet().iterator().next().get()).isEqualTo(7);

		AtomicLongPojo empty = coll.find().projection(Projections.include("id")).first();
		assertThat(empty.getScalar()).isNull();
		assertThat(empty.getArray()).isNull();
		assertThat(empty.getArray2()).isNull();
		assertThat(empty.getList()).isNull();
		assertThat(empty.getSet()).isNull();
	}

	@Test
	public void testInsertAndFindEmpty() {
		MongoDatabase db = connect();
		AtomicLongPojo pojo = insertEmpty(db);

		MongoCollection<AtomicLongPojo> coll = db.getCollection(COLL_NAME,
				AtomicLongPojo.class);
		AtomicLongPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		AtomicLongPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(6);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalar")).isEqualTo(pojo.getScalar().get());
		assertThat((List<Long>) doc.get("array")).containsExactly(2L, 3L);
		assertThat((List<List<Long>>) doc.get("array2"))
				.containsExactly(Arrays.asList(4L), Arrays.asList(5L));
		assertThat((List<Long>) doc.get("list")).containsExactly(6L);
		assertThat((List<Long>) doc.get("set")).containsExactly(7L);
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		AtomicLongPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(1);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
	}

}
