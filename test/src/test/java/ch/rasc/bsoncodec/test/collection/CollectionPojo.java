/**
 * Copyright 2015-2017 the original author or authors.
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

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TransferQueue;

import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class CollectionPojo {

	private ObjectId id;

	private Collection<String> collection;

	private List<String> list;

	private Set<String> set;

	private SortedSet<String> sortedSet;

	private NavigableSet<String> navigableSet;

	private BlockingDeque<String> blockingDeque;

	private BlockingQueue<String> blockingQueue;

	private Deque<String> deque;

	private Queue<String> queue;

	private TransferQueue<String> transferQueue;

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Collection<String> getCollection() {
		return this.collection;
	}

	public void setCollection(Collection<String> collection) {
		this.collection = collection;
	}

	public List<String> getList() {
		return this.list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public Set<String> getSet() {
		return this.set;
	}

	public void setSet(Set<String> set) {
		this.set = set;
	}

	public SortedSet<String> getSortedSet() {
		return this.sortedSet;
	}

	public void setSortedSet(SortedSet<String> sortedSet) {
		this.sortedSet = sortedSet;
	}

	public NavigableSet<String> getNavigableSet() {
		return this.navigableSet;
	}

	public void setNavigableSet(NavigableSet<String> navigableSet) {
		this.navigableSet = navigableSet;
	}

	public BlockingDeque<String> getBlockingDeque() {
		return this.blockingDeque;
	}

	public void setBlockingDeque(BlockingDeque<String> blockingDeque) {
		this.blockingDeque = blockingDeque;
	}

	public BlockingQueue<String> getBlockingQueue() {
		return this.blockingQueue;
	}

	public void setBlockingQueue(BlockingQueue<String> blockingQueue) {
		this.blockingQueue = blockingQueue;
	}

	public Deque<String> getDeque() {
		return this.deque;
	}

	public void setDeque(Deque<String> deque) {
		this.deque = deque;
	}

	public Queue<String> getQueue() {
		return this.queue;
	}

	public void setQueue(Queue<String> queue) {
		this.queue = queue;
	}

	public TransferQueue<String> getTransferQueue() {
		return this.transferQueue;
	}

	public void setTransferQueue(TransferQueue<String> transferQueue) {
		this.transferQueue = transferQueue;
	}

}
