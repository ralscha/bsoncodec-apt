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
package ch.rasc.bsoncodec.model;

import java.util.LinkedHashSet;

import javax.lang.model.element.TypeElement;

import org.immutables.value.Value;

import com.squareup.javapoet.TypeName;

@Value.Immutable(builder = false, copy = false)
public abstract class CodecInfo {

	@Value.Parameter
	public abstract TypeElement valueType();

	@Value.Parameter
	public abstract TypeName codecType();

	@Value.Parameter
	public abstract LinkedHashSet<InstanceField> instanceFields();

	@Value.Parameter
	public abstract boolean needRegistryField();

}
