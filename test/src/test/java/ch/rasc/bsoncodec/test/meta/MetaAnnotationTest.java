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
package ch.rasc.bsoncodec.test.meta;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class MetaAnnotationTest extends AbstractMongoDBTest {

	private final static String COLL_NAME = "meta";

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(new PojoCodecProvider()));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@Test
	public void testInsertAndFind() {
		MongoDatabase db = connect();

		MetaPojo mp = new MetaPojo();
		mp.setId("1");
		mp.setName("ralph");

		MongoCollection<MetaPojo> coll = db.getCollection(COLL_NAME, MetaPojo.class);
		coll.insertOne(mp);

		MetaPojo readMp = coll.find().first();
		assertThat(mp).isEqualToComparingFieldByField(readMp);
	}

}
