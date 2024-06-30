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
package ch.rasc.bsoncodec.codegen;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.bson.BsonType;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import ch.rasc.bsoncodec.Util;
import ch.rasc.bsoncodec.model.FieldModel;

public class MapCodeGen extends CompoundCodeGen {

	private TypeMirror implementationType;

	private final TypeMirror keyType;

	public MapCodeGen(CompoundCodeGen parent, TypeMirror type, TypeMirror keyType) {
		super(parent, type);
		this.keyType = keyType;
	}

	public void setImplementationType(TypeMirror implementationType) {
		this.implementationType = implementationType;
	}

	protected TypeName getImplementationType() {
		if (this.implementationType == null) {
			if (((DeclaredType) getType()).asElement()
					.getKind() == ElementKind.INTERFACE) {
				return ClassName.get(LinkedHashMap.class);
			}
			return ClassName.get((TypeElement) Util.typeUtils.asElement(this.getType()));
		}
		return ClassName
				.get((TypeElement) Util.typeUtils.asElement(this.implementationType));
	}

	@Override
	public void addEncodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		Builder builder = ctx.builder();

		if (!field.disableEncodeNullCheck() && !hasParent()) {
			builder.beginControlFlow("if ($L != null)", ctx.getter());
		}

		if (!field.storeEmptyCollection()) {
			builder.beginControlFlow("if (!$L.isEmpty())", ctx.getter());
		}

		if (!hasParent()) {
			builder.addStatement("writer.writeName($S)", field.name());
		}

		TypeMirror childType = this.getChildCodeGen().getType();
		builder.addStatement("writer.writeStartDocument()");

		if (!Util.isSameType(this.keyType, Object.class)) {
			builder.beginControlFlow("for (Map.Entry<$T, $T> $L : $L.entrySet())",
					this.keyType, childType, ctx.getLoopVar(), ctx.getter());
		}
		else {
			builder.beginControlFlow("for (Map.Entry $L : (Set<Map.Entry>)$L.entrySet())",
					ctx.getLoopVar(), ctx.getter());
		}
		if (Util.isSameType(this.keyType, String.class)) {
			builder.addStatement("writer.writeName($L.getKey())", ctx.getLoopVar());
		}
		else {
			if (Util.isSameType(getType(), EnumMap.class)) {
				builder.addStatement("writer.writeName($L.getKey().name())",
						ctx.getLoopVar());
			}
			else {
				builder.addStatement("writer.writeName($T.valueOf($L.getKey()))",
						String.class, ctx.getLoopVar());
			}
		}

		boolean permittNullElements = permitNullElements();
		if (!field.disableEncodeNullCheck() && permittNullElements) {
			builder.beginControlFlow("if ($L != null)", ctx.getLoopVar() + ".getValue()");
		}

		this.getChildCodeGen().addEncodeStatements(
				ctx.createEncodeChildContext(ctx.getLoopVar() + ".getValue()"));

		if (!field.disableEncodeNullCheck() && permittNullElements) {
			builder.nextControlFlow("else").addStatement("writer.writeNull()");
			builder.endControlFlow();
		}

		builder.endControlFlow();
		builder.addStatement("writer.writeEndDocument()");

		if (!field.storeEmptyCollection()) {
			builder.endControlFlow();
		}

		if (!field.disableEncodeNullCheck() && !hasParent()) {
			if (field.storeNullValue()) {
				builder.nextControlFlow("else").addStatement("writer.writeNull($S)",
						field.name());
			}
			builder.endControlFlow();
		}
	}

	@Override
	public void addDecodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		Builder builder = ctx.builder();
		char lv = ctx.getLoopVar();

		if (!field.disableDecodeNullCheck() && !hasParent()) {
			builder.beginControlFlow("if (bsonType != $T.NULL)", BsonType.class);
		}

		builder.addStatement("reader.readStartDocument()");
		if (Util.isSameType(getType(), EnumMap.class)) {
			builder.addStatement("$T $L = new $T<>($T.class)", getType(), lv,
					EnumMap.class, this.keyType);
		}
		else {
			builder.addStatement("$T $L = new $T<>()", getType(), lv,
					this.getImplementationType());
		}

		builder.beginControlFlow(
				"while ((bsonType = reader.readBsonType()) != $T.END_OF_DOCUMENT)",
				BsonType.class);

		if (Util.isSameType(this.keyType, String.class)
				|| Util.isSameType(this.keyType, Object.class)) {
			builder.addStatement("String $LKey = reader.readName()", lv);
		}
		else {
			builder.addStatement("$T $LKey = $T.valueOf(reader.readName())", this.keyType,
					lv, this.keyType);
		}

		boolean permittNullElements = permitNullElements();
		if (permittNullElements) {
			builder.beginControlFlow("if (bsonType != $T.NULL)", BsonType.class);
		}

		CodeGeneratorContext childCtx = ctx
				.createDecodeChildContext(lv + ".put(" + lv + "Key, %s)");
		this.getChildCodeGen().addDecodeStatements(childCtx);

		if (permittNullElements) {
			builder.nextControlFlow("else").addStatement("reader.readNull()");
			builder.addStatement("$L.put($LKey, null)", lv, lv);
			builder.endControlFlow();
		}

		builder.endControlFlow();
		builder.addStatement("reader.readEndDocument()");

		builder.addStatement(ctx.setter("$L"), lv);

		if (!field.disableDecodeNullCheck() && !hasParent()) {
			builder.nextControlFlow("else").addStatement("reader.readNull()");
			if (!ctx.field().disableSetNullStatement()) {
				this.getChildCodeGen().addSetNullStatements(ctx);
			}
			builder.endControlFlow();
		}

	}

	private static Set<String> permitNullCollections = new HashSet<>();

	static {
		permitNullCollections.addAll(Arrays.asList(HashMap.class.getCanonicalName(),
				LinkedHashMap.class.getCanonicalName(), TreeMap.class.getCanonicalName(),
				IdentityHashMap.class.getCanonicalName()));
		// permit null: EnumMap (values)
		// not permit null: EnumMap (keys)
		// not permit null: ConcurrentHashMap, ConcurrentSkipListMap
	}

	private boolean permitNullElements() {
		return permitNullCollections.contains(this.getImplementationType().toString());
	}
}
