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
package ch.rasc.bsoncodec.test.trans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class TransientTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "transient";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries
				.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries
						.fromCodecs(new TransientPojoCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();

		TransientPojo tp = new TransientPojo();
		tp.setSecret("theSecret");
		tp.setTransientField(11);
		tp.setName("name");

		MongoCollection<TransientPojo> coll = db.getCollection(COLL_NAME,
				TransientPojo.class);
		coll.insertOne(tp);

		TransientPojo readTp = coll.find().first();
		assertThat(readTp).isEqualToIgnoringGivenFields(tp, "secret");
		assertThat(readTp.getSecret()).isNull();

		Document doc = db.getCollection(COLL_NAME).find().first();
		assertThat(doc).hasSize(3);
		assertThat(doc.get("_id"))
				.isEqualTo(new ObjectId(Base64.getUrlDecoder().decode(tp.getId())));
		assertThat(doc.get("name")).isEqualTo("name");
		assertThat(doc.get("transientField")).isEqualTo(11);
	}

}
