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

import org.junit.After;
import org.junit.Before;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public abstract class AbstractMongoDBTest {

	/**
	 * please store Starter or RuntimeConfig in a static final field if you want to use
	 * artifact store caching (or else disable caching)
	 */
	private static final MongodStarter starter = MongodStarter.getDefaultInstance();

	private MongodExecutable _mongodExe;
	private MongodProcess _mongod;

	private MongoClient _mongo;

	@Before
	public void setUp() throws Exception {
		this._mongodExe = starter.prepare(MongodConfig.builder()
				.version(Version.Main.PRODUCTION)
				.net(new Net("127.0.0.1", 12345, Network.localhostIsIPv6())).build());
		this._mongod = this._mongodExe.start();

		this._mongo = MongoClients.create("mongodb://localhost:12345");

		// this._mongo = new MongoClient("localhost", 27017);
		for (String dbName : this._mongo.listDatabaseNames()) {
			if (!dbName.equals("admin") && !dbName.equals("local")) {
				MongoDatabase database = this._mongo.getDatabase(dbName);
				database.drop();
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		this._mongod.stop();
		this._mongodExe.stop();
	}

	public MongoClient getMongoClient() {
		return this._mongo;
	}

}