/**
 * Copyright 2015-2016 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.rasc.bsoncodec.test.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class HierarchyTest extends AbstractMongoDBTest {

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(new UserCodec(new ObjectIdGenerator())));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();

		User user = new User();
		user.setEmail("email");
		user.setName("name");

		MongoCollection<User> coll = db.getCollection("users", User.class);
		coll.insertOne(user);

		User readUser = coll.find().first();
		assertThat(readUser).isEqualToComparingFieldByField(user);

		Document doc = db.getCollection("users").find().first();
		assertThat(doc).hasSize(3);
		assertThat(doc.get("_id")).isEqualTo(user.getId());
		assertThat(doc.get("name")).isEqualTo("name");
		assertThat(doc.get("email")).isEqualTo("email");
	}
}
