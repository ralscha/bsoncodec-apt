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
package ch.rasc.bsoncodec.test.embedded;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class EmbeddedTest extends AbstractMongoDBTest {

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(
						new ch.rasc.bsoncodec.test.embedded.PojoCodecProvider()));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertAndRead() {
		MongoDatabase db = connect();

		Date date = new Date();

		Address address = new Address();
		address.setFirstName("first");
		address.setLastName("last");
		address.setStreet("street");
		address.setCity("city");

		OrderItem orderItem = new OrderItem();
		orderItem.setPartNo("partNo");
		orderItem.setPrice(100.45);
		orderItem.setQuantity(10);

		Order pojo = new Order();
		pojo.setDate(date);
		pojo.setAddress(address);
		pojo.setOrderItems(Arrays.asList(orderItem));

		MongoCollection<Order> coll = db.getCollection("Order", Order.class);
		coll.insertOne(pojo);

		Order readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		Document doc = db.getCollection("Order").find().first();
		assertThat(doc).hasSize(4);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat(doc.get("date")).isEqualTo(date);
		assertThat(doc.get("address")).isInstanceOf(Document.class);
		List<Document> orderItems = (List<Document>) doc.get("orderItems");
		assertThat(orderItems).hasSize(1);

		Document addressDoc = (Document) doc.get("address");
		assertThat(addressDoc).hasSize(4);
		assertThat(addressDoc.get("firstName")).isEqualTo("first");
		assertThat(addressDoc.get("lastName")).isEqualTo("last");
		assertThat(addressDoc.get("street")).isEqualTo("street");
		assertThat(addressDoc.get("city")).isEqualTo("city");

		Document orderItemDoc = orderItems.get(0);
		assertThat(orderItemDoc).hasSize(3);
		assertThat(orderItemDoc.get("partNo")).isEqualTo("partNo");
		assertThat(orderItemDoc.getDouble("price")).isEqualTo(100.45);
		assertThat(orderItemDoc.getInteger("quantity")).isEqualTo(10);
	}

}
