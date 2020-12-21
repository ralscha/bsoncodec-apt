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
package ch.rasc.bsoncodec.test.id;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.Test;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class IdTest extends AbstractMongoDBTest {

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				CodecRegistries.fromProviders(
						new UuidCodecProvider(UuidRepresentation.STANDARD)),
				MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(
						new ch.rasc.bsoncodec.test.id.PojoCodecProvider()));

		MongoDatabase db = getMongoClient().getDatabase("id")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@Test
	public void testCustomCodecId() {
		MongoDatabase db = connect();

		CustomIdCodecPojo pojo = new CustomIdCodecPojo();
		pojo.setKey(123L);
		pojo.setData(321);

		MongoCollection<CustomIdCodecPojo> coll = db.getCollection("CustomIdCodecPojo",
				CustomIdCodecPojo.class);
		coll.insertOne(pojo);

		CustomIdCodecPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("CustomIdCodecPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(new Document("key", 123L));
		assertThat(doc.get("data")).isEqualTo(321);
	}

	@Test
	public void testIntegerIdWithName_id() {
		MongoDatabase db = connect();

		Integer_IdPojo pojo = new Integer_IdPojo();
		pojo.set_id(1);
		pojo.setData(23);

		MongoCollection<Integer_IdPojo> coll = db.getCollection("Integer_IdPojo",
				Integer_IdPojo.class);
		coll.insertOne(pojo);

		Integer_IdPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("Integer_IdPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(1);
		assertThat(doc.get("data")).isEqualTo(23);
	}

	@Test
	public void testIntegerIdWithOtherName() {
		MongoDatabase db = connect();

		IntegerIdOtherNamePojo pojo = new IntegerIdOtherNamePojo();
		pojo.setPrimaryId(111);
		pojo.setData(400);

		MongoCollection<IntegerIdOtherNamePojo> coll = db
				.getCollection("IntegerIdOtherNamePojo", IntegerIdOtherNamePojo.class);
		coll.insertOne(pojo);

		IntegerIdOtherNamePojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("IntegerIdOtherNamePojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(111);
		assertThat(doc.get("data")).isEqualTo(400);
	}

	@Test
	public void testIntegerIdWithNameId() {
		MongoDatabase db = connect();

		IntegerIdPojo pojo = new IntegerIdPojo();
		pojo.setId(23);
		pojo.setData(1);

		MongoCollection<IntegerIdPojo> coll = db.getCollection("IntegerIdPojo",
				IntegerIdPojo.class);
		coll.insertOne(pojo);

		IntegerIdPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("IntegerIdPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(23);
		assertThat(doc.get("data")).isEqualTo(1);
	}

	@Test
	public void testObjectIdWithOtherName() {
		MongoDatabase db = connect();

		ObjectIdOtherNamePojo pojo = new ObjectIdOtherNamePojo();
		ObjectId theObjectId = new ObjectId();
		pojo.setTheObjectId(theObjectId);
		pojo.setData(2);

		MongoCollection<ObjectIdOtherNamePojo> coll = db
				.getCollection("ObjectIdOtherNamePojo", ObjectIdOtherNamePojo.class);
		coll.insertOne(pojo);

		ObjectIdOtherNamePojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("ObjectIdOtherNamePojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(theObjectId);
		assertThat(doc.get("data")).isEqualTo(2);
	}

	@Test
	public void testObjectIdWithNameId() {
		MongoDatabase db = connect();

		ObjectIdPojo pojo = new ObjectIdPojo();
		ObjectId theObjectId = new ObjectId();
		pojo.setId(theObjectId);
		pojo.setData(3);

		MongoCollection<ObjectIdPojo> coll = db.getCollection("ObjectIdPojo",
				ObjectIdPojo.class);
		coll.insertOne(pojo);

		ObjectIdPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("ObjectIdPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(theObjectId);
		assertThat(doc.get("data")).isEqualTo(3);
	}

	@Test
	public void testBase64StringIdToObjectId() {
		MongoDatabase db = connect();

		StringBase64ToObjectIdPojo pojo = new StringBase64ToObjectIdPojo();
		pojo.setB64(null);
		pojo.setData(4);

		MongoCollection<StringBase64ToObjectIdPojo> coll = db.getCollection(
				"StringBase64ToObjectIdPojo", StringBase64ToObjectIdPojo.class);
		coll.insertOne(pojo);
		assertThat(pojo.getB64()).isNotNull();

		StringBase64ToObjectIdPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringBase64ToObjectIdPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id"))
				.isEqualTo(new ObjectId(Base64.getUrlDecoder().decode(pojo.getB64())));
		assertThat(doc.get("data")).isEqualTo(4);
	}

	@Test
	public void testBase64StringIdToObjectIdPreset() {
		MongoDatabase db = connect();

		ObjectId theObjectId = new ObjectId();
		StringBase64ToObjectIdPojo pojo = new StringBase64ToObjectIdPojo();
		pojo.setB64(Base64.getUrlEncoder().encodeToString(theObjectId.toByteArray()));
		pojo.setData(5);

		MongoCollection<StringBase64ToObjectIdPojo> coll = db.getCollection(
				"StringBase64ToObjectIdPojo", StringBase64ToObjectIdPojo.class);
		coll.insertOne(pojo);

		StringBase64ToObjectIdPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringBase64ToObjectIdPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(theObjectId);
		assertThat(doc.get("data")).isEqualTo(5);
	}

	@Test
	public void testHexStringIdToObjectId() {
		MongoDatabase db = connect();

		StringHexToObjectIdPojo pojo = new StringHexToObjectIdPojo();
		pojo.setHex(null);
		pojo.setData(6);

		MongoCollection<StringHexToObjectIdPojo> coll = db
				.getCollection("StringHexToObjectIdPojo", StringHexToObjectIdPojo.class);
		coll.insertOne(pojo);
		assertThat(pojo.getHex()).isNotNull();

		StringHexToObjectIdPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringHexToObjectIdPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(new ObjectId(pojo.getHex()));
		assertThat(doc.get("data")).isEqualTo(6);
	}

	@Test
	public void testHexStringIdToObjectIdPreset() {
		MongoDatabase db = connect();

		ObjectId theObjectId = new ObjectId();
		StringHexToObjectIdPojo pojo = new StringHexToObjectIdPojo();
		pojo.setHex(theObjectId.toHexString());
		pojo.setData(7);

		MongoCollection<StringHexToObjectIdPojo> coll = db
				.getCollection("StringHexToObjectIdPojo", StringHexToObjectIdPojo.class);
		coll.insertOne(pojo);

		StringHexToObjectIdPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringHexToObjectIdPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(theObjectId);
		assertThat(doc.get("data")).isEqualTo(7);
	}

	@Test
	public void testStringIdWithOtherName() {
		MongoDatabase db = connect();

		StringIdOtherNamePojo pojo = new StringIdOtherNamePojo();
		pojo.setStr("ralph");
		pojo.setData(33);

		MongoCollection<StringIdOtherNamePojo> coll = db
				.getCollection("StringIdOtherNamePojo", StringIdOtherNamePojo.class);
		coll.insertOne(pojo);

		StringIdOtherNamePojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringIdOtherNamePojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo("ralph");
		assertThat(doc.get("data")).isEqualTo(33);
	}

	@Test
	public void testStringIdWithGenerator() {
		MongoDatabase db = connect();

		StringIdPojo pojo = new StringIdPojo();
		pojo.setId(null);
		pojo.setData(44);

		MongoCollection<StringIdPojo> coll = db.getCollection("StringIdPojo",
				StringIdPojo.class);
		coll.insertOne(pojo);
		assertThat(pojo.getId()).isNotNull();

		StringIdPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringIdPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("data")).isEqualTo(44);
	}

	@Test
	public void testStringIdWithGeneratorPreset() {
		MongoDatabase db = connect();

		StringIdPojo pojo = new StringIdPojo();
		pojo.setId("rasc");
		pojo.setData(55);

		MongoCollection<StringIdPojo> coll = db.getCollection("StringIdPojo",
				StringIdPojo.class);
		coll.insertOne(pojo);

		StringIdPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringIdPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(doc.get("_id")).isEqualTo("rasc");
		assertThat(doc.get("data")).isEqualTo(55);
	}

	@Test
	public void testHexStringIdToUUID() {
		MongoDatabase db = connect();

		StringHexToUuidPojo pojo = new StringHexToUuidPojo();
		pojo.setUuid(null);
		pojo.setData(7);

		MongoCollection<StringHexToUuidPojo> coll = db
				.getCollection("StringHexToUuidPojo", StringHexToUuidPojo.class);
		coll.insertOne(pojo);
		assertThat(pojo.getUuid()).isNotNull();

		StringHexToUuidPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringHexToUuidPojo").find().first();
		assertThat(doc).hasSize(2);

		assertThat(binaryToUUID((Binary) doc.get("_id")))
				.isEqualTo(UUID.fromString(pojo.getUuid()));
		assertThat(doc.get("data")).isEqualTo(7);
	}

	private static UUID binaryToUUID(Binary id) {
		ByteBuffer bb = ByteBuffer.wrap(id.getData());
		long high = bb.getLong();
		long low = bb.getLong();
		UUID uuid = new UUID(high, low);
		return uuid;
	}

	@Test
	public void testHexStringIdToUUIDPreset() {
		MongoDatabase db = connect();

		UUID uuid = UUID.randomUUID();
		StringHexToUuidPojo pojo = new StringHexToUuidPojo();
		pojo.setUuid(uuid.toString());
		pojo.setData(8);

		MongoCollection<StringHexToUuidPojo> coll = db
				.getCollection("StringHexToUuidPojo", StringHexToUuidPojo.class);
		coll.insertOne(pojo);

		StringHexToUuidPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringHexToUuidPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(binaryToUUID((Binary) doc.get("_id"))).isEqualTo(uuid);
		assertThat(doc.get("data")).isEqualTo(8);
	}

	@Test
	public void testHexStringIdToUUIDWoGenerator() {
		MongoDatabase db = connect();

		UUID uuid = UUID.randomUUID();
		StringHexToUuidWoGeneratorPojo pojo = new StringHexToUuidWoGeneratorPojo();
		pojo.setUuid(uuid.toString());
		pojo.setData(9);

		MongoCollection<StringHexToUuidWoGeneratorPojo> coll = db.getCollection(
				"StringHexToUuidWoGeneratorPojo", StringHexToUuidWoGeneratorPojo.class);
		coll.insertOne(pojo);

		StringHexToUuidWoGeneratorPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringHexToUuidWoGeneratorPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(binaryToUUID((Binary) doc.get("_id"))).isEqualTo(uuid);
		assertThat(doc.get("data")).isEqualTo(9);
	}

	@Test
	public void testBase64StringIdToUUID() {
		MongoDatabase db = connect();

		StringBase64ToUuidPojo pojo = new StringBase64ToUuidPojo();
		pojo.setUuid(null);
		pojo.setData(7);

		MongoCollection<StringBase64ToUuidPojo> coll = db
				.getCollection("StringBase64ToUuidPojo", StringBase64ToUuidPojo.class);
		coll.insertOne(pojo);
		assertThat(pojo.getUuid()).isNotNull();

		StringBase64ToUuidPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringBase64ToUuidPojo").find().first();
		assertThat(doc).hasSize(2);

		ByteBuffer bb = ByteBuffer.wrap(Base64.getUrlDecoder().decode(pojo.getUuid()));
		UUID id = new UUID(bb.getLong(), bb.getLong());
		assertThat(binaryToUUID((Binary) doc.get("_id"))).isEqualTo(id);
		assertThat(doc.get("data")).isEqualTo(7);
	}

	@Test
	public void testBase64StringIdToUUIDPreset() {
		MongoDatabase db = connect();

		UUID uuid = UUID.randomUUID();
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());

		StringBase64ToUuidPojo pojo = new StringBase64ToUuidPojo();
		pojo.setUuid(Base64.getUrlEncoder().encodeToString(bb.array()));
		pojo.setData(8);

		MongoCollection<StringBase64ToUuidPojo> coll = db
				.getCollection("StringBase64ToUuidPojo", StringBase64ToUuidPojo.class);
		coll.insertOne(pojo);

		StringBase64ToUuidPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringBase64ToUuidPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(binaryToUUID((Binary) doc.get("_id"))).isEqualTo(uuid);
		assertThat(doc.get("data")).isEqualTo(8);
	}

	@Test
	public void testBase64StringIdToUUIDWoGenerator() {
		MongoDatabase db = connect();

		UUID uuid = UUID.randomUUID();
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());

		StringBase64ToUuidWoGeneratorPojo pojo = new StringBase64ToUuidWoGeneratorPojo();
		pojo.setUuid(Base64.getUrlEncoder().encodeToString(bb.array()));
		pojo.setData(9);

		MongoCollection<StringBase64ToUuidWoGeneratorPojo> coll = db.getCollection(
				"StringBase64ToUuidWoGeneratorPojo",
				StringBase64ToUuidWoGeneratorPojo.class);
		coll.insertOne(pojo);

		StringBase64ToUuidWoGeneratorPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("StringBase64ToUuidWoGeneratorPojo").find()
				.first();
		assertThat(doc).hasSize(2);
		assertThat(binaryToUUID((Binary) doc.get("_id"))).isEqualTo(uuid);
		assertThat(doc.get("data")).isEqualTo(9);
	}

	@Test(expected = NullPointerException.class)
	public void testHexStringIdToUUIDWoGeneratorIdNotSet() {
		MongoDatabase db = connect();

		StringHexToUuidWoGeneratorPojo pojo = new StringHexToUuidWoGeneratorPojo();
		pojo.setUuid(null);
		pojo.setData(9);

		MongoCollection<StringHexToUuidWoGeneratorPojo> coll = db.getCollection(
				"StringHexToUuidWoGeneratorPojo", StringHexToUuidWoGeneratorPojo.class);
		coll.insertOne(pojo);
	}

	@Test
	public void testUUIDWithOtherName() {
		MongoDatabase db = connect();

		UuidOtherNamePojo pojo = new UuidOtherNamePojo();
		UUID uuid = UUID.randomUUID();
		pojo.setPrimaryKey(uuid);
		pojo.setData(22);

		MongoCollection<UuidOtherNamePojo> coll = db.getCollection("UuidOtherNamePojo",
				UuidOtherNamePojo.class);
		coll.insertOne(pojo);

		UuidOtherNamePojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("UuidOtherNamePojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(binaryToUUID((Binary) doc.get("_id"))).isEqualTo(uuid);
		assertThat(doc.get("data")).isEqualTo(22);
	}

	@Test
	public void testUUIDWithGenerator() {
		MongoDatabase db = connect();

		UuidPojo pojo = new UuidPojo();
		pojo.setId(null);
		pojo.setData(44);

		MongoCollection<UuidPojo> coll = db.getCollection("UuidPojo", UuidPojo.class);
		coll.insertOne(pojo);
		assertThat(pojo.getId()).isNotNull();

		UuidPojo readPojo = coll.find().first();
		assertThat(readPojo).usingRecursiveComparison().isEqualTo(pojo);

		Document doc = db.getCollection("UuidPojo").find().first();
		assertThat(doc).hasSize(2);
		assertThat(binaryToUUID((Binary) doc.get("_id"))).isEqualTo(pojo.getId());
		assertThat(doc.get("data")).isEqualTo(44);
	}
}
