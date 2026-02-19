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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.embed.process.io.ProcessOutput;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;

public abstract class AbstractMongoDBTest {

	private TransitionWalker.ReachedState<RunningMongodProcess> running;

	private MongoClient _mongo;

	@BeforeEach
	public void setUp() throws Exception {
		Mongod mongod = Mongod.builder()
				.processOutput(Start.to(ProcessOutput.class)
						.initializedWith(ProcessOutput.silent())
						.withTransitionLabel("no output"))
				.build();

		this.running = mongod.start(Version.Main.V8_0);

		com.mongodb.ServerAddress serverAddress = new com.mongodb.ServerAddress(
				this.running.current().getServerAddress().getHost(),
				this.running.current().getServerAddress().getPort());
		this._mongo = MongoClients.create("mongodb://" + serverAddress);

		for (String dbName : this._mongo.listDatabaseNames()) {
			if (!dbName.equals("admin") && !dbName.equals("local")) {
				MongoDatabase database = this._mongo.getDatabase(dbName);
				database.drop();
			}
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
		if (this._mongo != null) {
			this._mongo.close();
		}
		if (this.running != null) {
			this.running.close();
		}
	}

	public MongoClient getMongoClient() {
		return this._mongo;
	}

}