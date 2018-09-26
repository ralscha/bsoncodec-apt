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
package ch.rasc.bsoncodec.test.embedded;

import ch.rasc.bsoncodec.annotation.BsonDocument;

@BsonDocument
public class OrderItem {
	private String partNo;
	private int quantity;
	private double price;

	public String getPartNo() {
		return this.partNo;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return this.price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.partNo == null ? 0 : this.partNo.hashCode());
		long temp;
		temp = Double.doubleToLongBits(this.price);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + this.quantity;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		OrderItem other = (OrderItem) obj;
		if (this.partNo == null) {
			if (other.partNo != null) {
				return false;
			}
		}
		else if (!this.partNo.equals(other.partNo)) {
			return false;
		}
		if (Double.doubleToLongBits(this.price) != Double.doubleToLongBits(other.price)) {
			return false;
		}
		if (this.quantity != other.quantity) {
			return false;
		}
		return true;
	}

}
