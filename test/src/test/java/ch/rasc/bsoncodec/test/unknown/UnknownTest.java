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
package ch.rasc.bsoncodec.test.unknown;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.BSONException;
import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class UnknownTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "unknownfield";

	private MongoDatabase connect() {
		ObjectIdGenerator objectIdGenerator = new ObjectIdGenerator();
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
						CodecRegistries.fromCodecs(
								new UnknownIgnorePojoCodec(objectIdGenerator),
								new UnknownFailPojoCodec(objectIdGenerator)));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@Test
	public void testUnkownIgnore() {
		MongoDatabase db = connect();

		UnknownIgnorePojo pojo = new UnknownIgnorePojo();
		pojo.setName("ralph");

		MongoCollection<UnknownIgnorePojo> coll = db.getCollection(COLL_NAME,
				UnknownIgnorePojo.class);
		coll.insertOne(pojo);

		db.getCollection(COLL_NAME).updateOne(Filters.eq("_id", pojo.getId()),
				Updates.set("newField", "new"));

		UnknownIgnorePojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection(COLL_NAME).find().first();
		assertThat(doc).hasSize(3);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("name")).isEqualTo("ralph");
		assertThat(doc.get("newField")).isEqualTo("new");
	}

	@Test
	public void testUnkownFail() {
		MongoDatabase db = connect();

		UnknownFailPojo pojo = new UnknownFailPojo();
		pojo.setName("john");

		MongoCollection<UnknownFailPojo> coll = db.getCollection(COLL_NAME,
				UnknownFailPojo.class);
		coll.insertOne(pojo);

		db.getCollection(COLL_NAME).updateOne(Filters.eq("_id", pojo.getId()),
				Updates.set("anotherNewField", "theNewValue"));

		try {
			@SuppressWarnings("unused")
			UnknownFailPojo readPojo = coll.find().first();
		}
		catch (BSONException e) {
			assertThat(e.getMessage()).isEqualTo(
					"ch.rasc.bsoncodec.test.unknown.UnknownFailPojoCodec does not contain a matching property for the field 'anotherNewField'");
		}

		Document doc = db.getCollection(COLL_NAME).find().first();
		assertThat(doc).hasSize(3);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("name")).isEqualTo("john");
		assertThat(doc.get("anotherNewField")).isEqualTo("theNewValue");
	}
}
