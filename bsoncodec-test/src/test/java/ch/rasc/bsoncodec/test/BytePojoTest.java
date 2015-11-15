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

import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.Binary;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.BytePojo;
import ch.rasc.bsoncodec.test.pojo.BytePojoCodec;

public class BytePojoTest extends AbstractMongoDBTest {

	private static final String COLL_NAME = "bytes";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(new BytePojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static BytePojo insert(MongoDatabase db) {
		MongoCollection<BytePojo> coll = db.getCollection(COLL_NAME, BytePojo.class);
		BytePojo pojo = new BytePojo();
		pojo.setScalarPrimitive((byte) 1);
		pojo.setScalar((byte) 2);
		pojo.setArrayPrimitive(new byte[] { (byte) 3, (byte) 4, (byte) 5 });
		pojo.setArray(new Byte[] { (byte) 6, (byte) 7, (byte) 8 });
		pojo.setList(Arrays.asList((byte) 10, (byte) 11));

		Set<Byte> set = new HashSet<>();
		set.add((byte) 12);
		set.add((byte) 13);
		pojo.setSet(set);
		coll.insertOne(pojo);
		return pojo;
	}

	private static BytePojo insertEmpty(MongoDatabase db) {
		MongoCollection<BytePojo> coll = db.getCollection(COLL_NAME, BytePojo.class);
		BytePojo pojo = new BytePojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		BytePojo pojo = insert(db);

		MongoCollection<BytePojo> coll = db.getCollection(COLL_NAME, BytePojo.class);
		BytePojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);

		BytePojo empty = coll.find().projection(Projections.include("id")).first();
		assertThat(empty.getScalarPrimitive()).isEqualTo((byte) 0);
		assertThat(empty.getScalar()).isNull();
		assertThat(empty.getArray()).isNull();
		assertThat(empty.getArrayPrimitive()).isNull();
		assertThat(empty.getList()).isNull();
		assertThat(empty.getSet()).isNull();
	}

	@Test
	public void testInsertAndFindEmpty() {
		MongoDatabase db = connect();
		BytePojo pojo = insertEmpty(db);

		MongoCollection<BytePojo> coll = db.getCollection(COLL_NAME, BytePojo.class);
		BytePojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		BytePojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(7);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo((int) pojo.getScalarPrimitive());
		assertThat(doc.get("scalar")).isEqualTo(pojo.getScalar().intValue());
		assertThat(doc.get("arrayPrimitive"))
				.isEqualTo(new Binary(new byte[] { 3, 4, 5 }));
		assertThat(doc.get("array")).isEqualTo(new Binary(new byte[] { 6, 7, 8 }));
		assertThat((List<Integer>) doc.get("list")).containsExactly(10, 11);
		assertThat((List<Integer>) doc.get("set")).containsOnly(12, 13);
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		BytePojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo(0);
	}

}
