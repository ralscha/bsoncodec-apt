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
package ch.rasc.bsoncodec.test.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.rasc.bsoncodec.test.AbstractMongoDBTest;

public class CollectionTest extends AbstractMongoDBTest {

	private MongoDatabase connect() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(new PojoCodecProvider()));

		MongoDatabase db = getMongoClient().getDatabase("pojo")
				.withCodecRegistry(codecRegistry);
		return db;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertAndFindDefault() {
		MongoDatabase db = connect();

		CollectionPojo pojo = new CollectionPojo();
		pojo.setCollection(Collections.singletonList("one"));

		List<String> list = new ArrayList<>();
		list.add("two");
		list.add("2");
		pojo.setList(list);

		pojo.setSet(Collections.singleton("three"));

		TreeSet<String> sortedSet = new TreeSet<>();
		sortedSet.add("four");
		pojo.setSortedSet(sortedSet);

		TreeSet<String> navigableSet = new TreeSet<>();
		navigableSet.add("five");
		pojo.setNavigableSet(navigableSet);

		LinkedBlockingDeque<String> lbd = new LinkedBlockingDeque<>();
		lbd.add("six");
		pojo.setBlockingDeque(lbd);

		LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<>();
		lbq.add("seven");
		pojo.setBlockingQueue(lbq);

		lbd = new LinkedBlockingDeque<>();
		lbd.add("eight");
		pojo.setDeque(lbd);

		lbq = new LinkedBlockingQueue<>();
		lbq.add("nine");
		pojo.setQueue(lbq);

		LinkedTransferQueue<String> ltq = new LinkedTransferQueue<>();
		ltq.add("ten");
		ltq.add("10");
		pojo.setTransferQueue(ltq);

		MongoCollection<CollectionPojo> coll = db.getCollection("collections",
				CollectionPojo.class);
		coll.insertOne(pojo);

		CollectionPojo readPojo = coll.find().first();

		assertThat(readPojo.getCollection()).isInstanceOf(ArrayList.class);
		assertThat(readPojo.getList()).isInstanceOf(ArrayList.class);
		assertThat(readPojo.getSet()).isInstanceOf(LinkedHashSet.class);
		assertThat(readPojo.getSortedSet()).isInstanceOf(TreeSet.class);
		assertThat(readPojo.getNavigableSet()).isInstanceOf(TreeSet.class);
		assertThat(readPojo.getBlockingDeque()).isInstanceOf(LinkedBlockingDeque.class);
		assertThat(readPojo.getBlockingQueue()).isInstanceOf(LinkedBlockingQueue.class);
		assertThat(readPojo.getDeque()).isInstanceOf(LinkedBlockingDeque.class);
		assertThat(readPojo.getQueue()).isInstanceOf(LinkedBlockingQueue.class);
		assertThat(readPojo.getTransferQueue()).isInstanceOf(LinkedTransferQueue.class);

		assertThat(readPojo).isEqualToComparingOnlyGivenFields(pojo, "id", "collection",
				"list", "set", "sortedSet", "navigableSet");

		assertThat(readPojo.getBlockingDeque()).containsExactly("six");
		assertThat(readPojo.getBlockingQueue()).containsExactly("seven");
		assertThat(readPojo.getDeque()).containsExactly("eight");
		assertThat(readPojo.getQueue()).containsExactly("nine");
		assertThat(readPojo.getTransferQueue()).containsExactly("ten", "10");

		Document doc = db.getCollection("collections").find().first();
		assertThat(doc).hasSize(11);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat((List<String>) doc.get("collection")).containsExactly("one");
		assertThat((List<String>) doc.get("list")).containsExactly("two", "2");
		assertThat((List<String>) doc.get("set")).containsExactly("three");
		assertThat((List<String>) doc.get("sortedSet")).containsExactly("four");
		assertThat((List<String>) doc.get("navigableSet")).containsExactly("five");
		assertThat((List<String>) doc.get("blockingDeque")).containsExactly("six");
		assertThat((List<String>) doc.get("blockingQueue")).containsExactly("seven");
		assertThat((List<String>) doc.get("deque")).containsExactly("eight");
		assertThat((List<String>) doc.get("queue")).containsExactly("nine");
		assertThat((List<String>) doc.get("transferQueue")).containsExactly("ten", "10");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertAndFindSpecific() {
		MongoDatabase db = connect();

		CollectionWithImplPojo pojo = new CollectionWithImplPojo();

		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		pojo.setList1(list);

		list = new ArrayList<>();
		list.add(11);
		pojo.setList2(list);

		list = new ArrayList<>();
		list.add(111);
		list.add(112);
		pojo.setList3(list);

		MongoCollection<CollectionWithImplPojo> coll = db.getCollection("collections",
				CollectionWithImplPojo.class);
		coll.insertOne(pojo);

		CollectionWithImplPojo readPojo = coll.find().first();
		assertThat(readPojo).isEqualToComparingFieldByField(pojo);

		assertThat(readPojo.getList1()).isInstanceOf(ArrayList.class);
		assertThat(readPojo.getList2()).isInstanceOf(ArrayList.class);
		assertThat(readPojo.getList3()).isInstanceOf(LinkedList.class);

		Document doc = db.getCollection("collections").find().first();
		assertThat(doc).hasSize(4);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat((List<Integer>) doc.get("list1")).containsExactly(1, 2, 3);
		assertThat((List<Integer>) doc.get("list2")).containsExactly(11);
		assertThat((List<Integer>) doc.get("list3")).containsExactly(111, 112);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testUnknownTypes() {
		MongoDatabase db = connect();

		CollectionWithUnknownTypes pojo = new CollectionWithUnknownTypes();

		List list = new ArrayList();
		list.add(1);
		list.add("2");
		list.add(3.3d);
		pojo.setList(list);

		Set set = new HashSet();
		set.add("1");
		set.add(2);
		set.add(3L);
		set.add(true);
		pojo.setSet(set);

		Map map = new HashMap();
		map.put(1, "one");
		map.put("two", 2);
		map.put("flag", true);
		pojo.setMap(map);

		Map stringObjectMap = new LinkedHashMap<>();
		stringObjectMap.put("a", true);
		stringObjectMap.put("b", 2);
		stringObjectMap.put("c", 3.3d);
		stringObjectMap.put("d", "string");
		pojo.setStringObjectMap(stringObjectMap);

		MongoCollection<CollectionWithUnknownTypes> coll = db.getCollection("unknown",
				CollectionWithUnknownTypes.class);
		coll.insertOne(pojo);

		CollectionWithUnknownTypes readPojo = coll.find().first();
		assertThat(readPojo).isEqualToIgnoringGivenFields(pojo, "map");
		assertThat(readPojo.getMap()).hasSize(3).containsOnly(
				Assertions.entry("1", "one"), Assertions.entry("two", 2),
				Assertions.entry("flag", true));

		assertThat(readPojo.getList()).isInstanceOf(ArrayList.class);
		assertThat(readPojo.getSet()).isInstanceOf(LinkedHashSet.class);
		assertThat(readPojo.getMap()).isInstanceOf(LinkedHashMap.class);
		assertThat(readPojo.getStringObjectMap()).isInstanceOf(LinkedHashMap.class);

		Document doc = db.getCollection("unknown").find().first();
		assertThat(doc).hasSize(5);
		assertThat(doc.get("_id")).isEqualTo(pojo.getId());
		assertThat((List) doc.get("list")).containsExactly(1, "2", 3.3d);
		assertThat((List) doc.get("set")).containsExactly("1", 2, 3L, true);
		assertThat((Map) doc.get("map")).containsOnly(Assertions.entry("1", "one"),
				Assertions.entry("two", 2), Assertions.entry("flag", true));
		assertThat((Map) doc.get("stringObjectMap")).containsOnly(
				Assertions.entry("a", true), Assertions.entry("b", 2),
				Assertions.entry("c", 3.3d), Assertions.entry("d", "string"));
	}
}
