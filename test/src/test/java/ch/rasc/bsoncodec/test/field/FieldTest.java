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
package ch.rasc.bsoncodec.test.field;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class FieldTest extends AbstractMongoDBTest {

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(new PojoCodecProvider()));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCustomCodec() {
		MongoDatabase db = connect();

		CustomCodecPojo pojo = new CustomCodecPojo();
		pojo.setSpecial("one;two;three");
		pojo.setSpecial2(null);

		MongoCollection<CustomCodecPojo> coll = db.getCollection("CustomCodecPojo",
				CustomCodecPojo.class);
		coll.insertOne(pojo);

		CustomCodecPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison()
        .isEqualTo(pojo);

		Document doc = db.getCollection("CustomCodecPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat((List<String>) doc.get("special")).containsExactly("one", "two",
				"three");
	}

	@Test
	public void testDifferentFieldName() {
		MongoDatabase db = connect();

		DifferentFieldNamePojo pojo = new DifferentFieldNamePojo();
		pojo.setI(1);
		pojo.setName("name");
		pojo.setSameName("same");

		MongoCollection<DifferentFieldNamePojo> coll = db
				.getCollection("DifferentFieldNamePojo", DifferentFieldNamePojo.class);
		coll.insertOne(pojo);

		DifferentFieldNamePojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison()
        .isEqualTo(pojo);

		Document doc = db.getCollection("DifferentFieldNamePojo").find().first();
		assertThat(doc).hasSize(4);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("thisIsTheNameField")).isEqualTo("name");
		assertThat(doc.get("sameName")).isEqualTo("same");
		assertThat(doc.get("j")).isEqualTo(1);
	}

	@Test
	public void testOrderAsc() {
		MongoDatabase db = connect();

		OrderAscPojo pojo = new OrderAscPojo();
		pojo.setI(1);
		pojo.setName("name");
		pojo.setSameName("same");

		MongoCollection<OrderAscPojo> coll = db.getCollection("OrderAscPojo",
				OrderAscPojo.class);
		coll.insertOne(pojo);

		OrderAscPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison()
        .isEqualTo(pojo);

		Document doc = db.getCollection("OrderAscPojo").find().first();
		assertThat(doc).hasSize(4);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("name")).isEqualTo("name");
		assertThat(doc.get("sameName")).isEqualTo("same");
		assertThat(doc.get("i")).isEqualTo(1);

		Set<String> keys = doc.keySet();
		Iterator<String> it = keys.iterator();
		assertThat(it.next()).isEqualTo("_id");
		assertThat(it.next()).isEqualTo("name");
		assertThat(it.next()).isEqualTo("sameName");
		assertThat(it.next()).isEqualTo("i");
	}

	@Test
	public void testOrderDesc() {
		MongoDatabase db = connect();

		OrderDescPojo pojo = new OrderDescPojo();
		pojo.setI(1);
		pojo.setName("name");
		pojo.setSameName("same");

		MongoCollection<OrderDescPojo> coll = db.getCollection("OrderDescPojo",
				OrderDescPojo.class);
		coll.insertOne(pojo);

		OrderDescPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison()
        .isEqualTo(pojo);

		Document doc = db.getCollection("OrderDescPojo").find().first();
		assertThat(doc).hasSize(4);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("name")).isEqualTo("name");
		assertThat(doc.get("sameName")).isEqualTo("same");
		assertThat(doc.get("i")).isEqualTo(1);

		Set<String> keys = doc.keySet();
		Iterator<String> it = keys.iterator();
		assertThat(it.next()).isEqualTo("_id");
		assertThat(it.next()).isEqualTo("i");
		assertThat(it.next()).isEqualTo("sameName");
		assertThat(it.next()).isEqualTo("name");
	}

}
