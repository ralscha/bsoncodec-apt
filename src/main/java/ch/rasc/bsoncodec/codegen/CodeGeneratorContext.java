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
package ch.rasc.bsoncodec.codegen;

import java.util.Set;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;

import ch.rasc.bsoncodec.model.FieldModel;
import ch.rasc.bsoncodec.model.InstanceField;

public class CodeGeneratorContext {
	private final FieldModel field;

	private final MethodSpec.Builder builder;

	private final Set<InstanceField> instanceFields;

	private final String getter;

	private final String setter;

	private final char loopVar;

	public CodeGeneratorContext(FieldModel field, Builder builder,
			Set<InstanceField> instanceFields, String getter, String setter) {
		this.field = field;
		this.builder = builder;
		this.instanceFields = instanceFields;
		this.getter = getter;
		this.setter = setter;
		this.loopVar = 'a';
	}

	public CodeGeneratorContext(CodeGeneratorContext parent, String getter,
			String setter) {
		this.field = parent.field;
		this.builder = parent.builder;
		this.instanceFields = parent.instanceFields;
		this.getter = getter;
		this.setter = setter;
		this.loopVar = (char) (parent.loopVar + 1);
	}

	protected FieldModel field() {
		return this.field;
	}

	public MethodSpec.Builder builder() {
		return this.builder;
	}

	public Set<InstanceField> instanceFields() {
		return this.instanceFields;
	}

	public String getter() {
		return this.getter;
	}

	public String setter(String code) {
		return String.format(this.setter, code);
	}

	public char getLoopVar() {
		return this.loopVar;
	}

	public CodeGeneratorContext createEncodeChildContext(String get) {
		return new CodeGeneratorContext(this, get, null);
	}

	public CodeGeneratorContext createDecodeChildContext(String set) {
		return new CodeGeneratorContext(this, null, set);
	}

}
