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
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.rasc.bsoncodec.test.pojo.BooleanPojo;
import ch.rasc.bsoncodec.test.pojo.BooleanPojoCodec;

public class BooleanPojoTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "booleans";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries
						.fromCodecs(new BooleanPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	private static BooleanPojo insert(MongoDatabase db) {
		MongoCollection<BooleanPojo> coll = db.getCollection(COLL_NAME,
				BooleanPojo.class);
		BooleanPojo pojo = new BooleanPojo();
		pojo.setScalarPrimitive(true);
		pojo.setScalar(false);
		pojo.setArrayPrimitive(new boolean[] { true });
		pojo.setArray(new Boolean[] { false });
		pojo.setArray2Primitive(new boolean[][] { { true }, { false } });
		pojo.setArray2(new Boolean[][] { { false }, { true } });
		pojo.setList(Arrays.asList(true, false));

		Set<Boolean> set = new HashSet<>();
		set.add(true);
		pojo.setSet(set);
		coll.insertOne(pojo);
		return pojo;
	}

	private static BooleanPojo insertEmpty(MongoDatabase db) {
		MongoCollection<BooleanPojo> coll = db.getCollection(COLL_NAME,
				BooleanPojo.class);
		BooleanPojo pojo = new BooleanPojo();
		coll.insertOne(pojo);
		return pojo;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();
		BooleanPojo pojo = insert(db);

		MongoCollection<BooleanPojo> coll = db.getCollection(COLL_NAME,
				BooleanPojo.class);
		BooleanPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);

		BooleanPojo empty = coll.find().projection(Projections.include("id")).first();
		assertThat(empty.getScalarPrimitive()).isEqualTo(false);
		assertThat(empty.getScalar()).isNull();
		assertThat(empty.getArray()).isNull();
		assertThat(empty.getArrayPrimitive()).isNull();
		assertThat(empty.getArray2()).isNull();
		assertThat(empty.getArray2Primitive()).isNull();
		assertThat(empty.getList()).isNull();
		assertThat(empty.getSet()).isNull();
	}

	@Test
	public void testInsertAndFindEmpty() {
		MongoDatabase db = connect();
		BooleanPojo pojo = insertEmpty(db);

		MongoCollection<BooleanPojo> coll = db.getCollection(COLL_NAME,
				BooleanPojo.class);
		BooleanPojo read = coll.find().first();
		assertThat(read).isEqualToComparingFieldByField(pojo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithDocument() {
		MongoDatabase db = connect();
		BooleanPojo pojo = insert(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(9);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo(true);
		assertThat(doc.get("scalar")).isEqualTo(false);
		assertThat((List<Boolean>) doc.get("arrayPrimitive")).containsExactly(true);
		assertThat((List<Boolean>) doc.get("array")).containsExactly(false);
		assertThat((List<List<Boolean>>) doc.get("array2Primitive"))
				.containsExactly(Arrays.asList(true), Arrays.asList(false));
		assertThat((List<List<Boolean>>) doc.get("array2"))
				.containsExactly(Arrays.asList(false), Arrays.asList(true));
		assertThat((List<Boolean>) doc.get("list")).containsExactly(true, false);
		assertThat((List<Boolean>) doc.get("set")).containsExactly(true);
	}

	@Test
	public void testEmptyWithDocument() {
		MongoDatabase db = connect();
		BooleanPojo pojo = insertEmpty(db);

		MongoCollection<Document> coll = db.getCollection(COLL_NAME);
		Document doc = coll.find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("scalarPrimitive")).isEqualTo(false);
	}

}
