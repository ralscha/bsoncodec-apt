/*
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
package ch.rasc.bsoncodec.codegen.delegate;

import javax.lang.model.type.TypeMirror;

import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.configuration.CodecRegistry;

import com.squareup.javapoet.ClassName;

import ch.rasc.bsoncodec.codegen.CodeGeneratorContext;
import ch.rasc.bsoncodec.model.ImmutableInstanceField;

public class UnknownCodecDelegate implements CodeGeneratorDelegate {

	@Override
	public void addEncodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		ctx.builder()
				.addStatement("Codec codec = this.registry.get($L.getClass())",
						ctx.getter())
				.addStatement("encoderContext.encodeWithChildContext(codec, writer, $L)",
						ctx.getter());

		// ctx.builder()
		// .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
		// .addMember("value", "$S", "unchecked")
		// .addMember("value", "$S", "rawtypes").build());

		ctx.instanceFields().add(ImmutableInstanceField.builder()
				.type(ClassName.get(CodecRegistry.class)).name("registry").build());

		ctx.instanceFields()
				.add(ImmutableInstanceField.builder()
						.type(ClassName.get(BsonTypeClassMap.class))
						.name("bsonTypeClassMap").build());

	}

	@Override
	public void addDecodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		ctx.builder().addStatement(ctx.setter(
				"this.registry.get(this.bsonTypeClassMap.get(bsonType)).decode(reader, decoderContext)"));
	}

	@Override
	public boolean accepts(TypeMirror type) {
		return true;
	}

}
