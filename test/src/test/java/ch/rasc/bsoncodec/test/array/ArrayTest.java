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
package ch.rasc.bsoncodec.test.array;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class ArrayTest extends AbstractMongoDBTest {

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(new Array1PojoCodec(new ObjectIdGenerator()),
						new Array1PojoFixedCodec(new ObjectIdGenerator()),
						new Array2PojoCodec(new ObjectIdGenerator()),
						new Array2PojoFixedCodec(new ObjectIdGenerator()),
						new Array3PojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertAndFindPrimitive() {
		MongoDatabase db = connect();

		for (int i = 0; i < 500; i++) {
			MongoCollection<Array1Pojo> coll = db.getCollection("array",
					Array1Pojo.class);
			coll.drop();

			int[] array = new int[i];
			Integer[] expected = new Integer[i];
			for (int j = 0; j < i; j++) {
				array[j] = j;
				expected[j] = j;
			}

			Array1Pojo pojo = new Array1Pojo();
			pojo.setArray(array);

			coll.insertOne(pojo);

			Array1Pojo readPojo = coll.find().first();
			assertThat(readPojo).isEqualToComparingFieldByField(pojo);

			Document doc = db.getCollection("array").find().first();
			assertThat(doc).hasSize(2);
			assertThat(doc.get("_id")).isEqualTo(pojo.get_id());
			List<Integer> actual = (List<Integer>) doc.get("array");
			assertThat(actual).hasSize(i);
			assertThat(actual).containsExactly(expected);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertAndFindPrimitiveFixedArray() {
		MongoDatabase db = connect();

		MongoCollection<Array1PojoFixed> coll = db.getCollection("array",
				Array1PojoFixed.class);
		coll.drop();

		int[] array = new int[2];
		Integer[] expected = new Integer[2];
		for (int j = 0; j < 2; j++) {
			array[j] = j;
			expected[j] = j;
		}

		Array1PojoFixed pojo = new Array1PojoFixed();
		pojo.setArray(array);

		coll.insertOne(pojo);

		Array1PojoFixed readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		Document doc = db.getCollection("array").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.get_id());
		List<Integer> actual = (List<Integer>) doc.get("array");
		assertThat(actual).hasSize(2);
		assertThat(actual).containsExactly(expected);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();

		for (int i = 0; i < 500; i++) {
			MongoCollection<Array2Pojo> coll = db.getCollection("array",
					Array2Pojo.class);
			coll.drop();

			Integer[] array = new Integer[i];
			for (int j = 0; j < i; j++) {
				array[j] = j;
			}

			Array2Pojo pojo = new Array2Pojo();
			pojo.setArray(array);

			coll.insertOne(pojo);

			Array2Pojo readPojo = coll.find().first();
			assertThat(readPojo).isEqualToComparingFieldByField(pojo);

			Document doc = db.getCollection("array").find().first();
			assertThat(doc).hasSize(2);
			assertThat(doc.get("_id")).isEqualTo(pojo.get_id());
			List<Integer> actual = (List<Integer>) doc.get("array");
			assertThat(actual).hasSize(i);
			assertThat(actual).containsExactly(array);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertAndFindFixedArray() {
		MongoDatabase db = connect();

		MongoCollection<Array2PojoFixed> coll = db.getCollection("array",
				Array2PojoFixed.class);
		coll.drop();

		Integer[] array = new Integer[2];
		for (int j = 0; j < 2; j++) {
			array[j] = j;
		}

		Array2PojoFixed pojo = new Array2PojoFixed();
		pojo.setArray(array);

		coll.insertOne(pojo);

		Array2PojoFixed readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		Document doc = db.getCollection("array").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.get_id());
		List<Integer> actual = (List<Integer>) doc.get("array");
		assertThat(actual).hasSize(2);
		assertThat(actual).containsExactly(array);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertAndFindMultiArray() {
		MongoDatabase db = connect();

		for (int i = 0; i < 20; i++) {
			MongoCollection<Array3Pojo> coll = db.getCollection("array",
					Array3Pojo.class);
			coll.drop();

			int[][] array = new int[i][i];
			for (int j = 0; j < i; j++) {
				for (int n = 0; n < i; n++) {
					array[j][n] = j * n;
				}
			}

			Array3Pojo pojo = new Array3Pojo();
			pojo.setArray(array);

			coll.insertOne(pojo);

			Array3Pojo readPojo = coll.find().first();
			assertThat(readPojo).isEqualToComparingFieldByField(pojo);

			Document doc = db.getCollection("array").find().first();
			System.out.println(doc);
			assertThat(doc).hasSize(2);
			assertThat(doc.get("_id")).isEqualTo(pojo.get_id());
			List<List<Integer>> actual = (List<List<Integer>>) doc.get("array");
			assertThat(actual).hasSize(i);

			int ix = 0;
			for (List<Integer> list : actual) {
				assertThat(list).hasSize(i);
				Integer[] expected = new Integer[i];
				for (int j = 0; j < expected.length; j++) {
					expected[j] = ix * j;
				}
				assertThat(list).containsExactly(expected);
				ix++;
			}
			// assertThat(actual).containsExactly(array);
		}
	}
}
