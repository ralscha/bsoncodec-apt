/**
 * Copyright 2015-2017 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.rasc.bsoncodec.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.squareup.javapoet.TypeName;

@Value.Immutable
public abstract class InstanceField implements Comparable<InstanceField> {

	public abstract TypeName type();

	public abstract String name();

	public @Nullable abstract TypeName codecForClass();

	public boolean isRegistryCodec() {
		return this.codecForClass() != null;
	}

	public boolean isRegistry() {
		return name().equals("registry");
	}

	@Value.Default
	public boolean customCodec() {
		return false;
	}

	@Override
	public int compareTo(InstanceField o) {
		return this.name().compareTo(o.name());
	}

}
