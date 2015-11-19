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
package ch.rasc.bsoncodec.codegen;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;

import org.bson.BsonType;

import com.squareup.javapoet.MethodSpec.Builder;

import ch.rasc.bsoncodec.Util;
import ch.rasc.bsoncodec.model.FieldModel;

public class ArrayCodeGen extends CompoundCodeGen {

	public ArrayCodeGen(CompoundCodeGen parent, TypeMirror type) {
		super(parent, type);
	}

	@Override
	public void addEncodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		Builder builder = ctx.builder();

		if (!field.disableEncodeNullCheck() && !hasParent()) {
			builder.beginControlFlow("if ($L != null)", ctx.getter());
		}

		if (!field.storeEmptyCollection()) {
			builder.beginControlFlow("if ($L.length > 0)", ctx.getter());
		}

		if (!hasParent()) {
			builder.addStatement("writer.writeName($S)", field.name());
		}

		builder.addStatement("writer.writeStartArray()")
				.addStatement("$T $LArray = $L", getType(), ctx.getLoopVar(),
						ctx.getter())
				.beginControlFlow("for (int $L = 0; $L < $LArray.length; $L++)",
						ctx.getLoopVar(), ctx.getLoopVar(), ctx.getLoopVar(),
						ctx.getLoopVar());

		TypeMirror childType = this.getChildCodeGen().getType();
		if (!childType.getKind().isPrimitive() && !field.disableEncodeNullCheck()) {
			builder.beginControlFlow("if ($LArray[$L] != null)", ctx.getLoopVar(),
					ctx.getLoopVar());
		}

		this.getChildCodeGen().addEncodeStatements(ctx.createEncodeChildContext(
				ctx.getLoopVar() + "Array[" + ctx.getLoopVar() + "]"));

		if (!childType.getKind().isPrimitive() && !field.disableEncodeNullCheck()) {
			builder.nextControlFlow("else").addStatement("writer.writeNull()");
			builder.endControlFlow();
		}

		builder.endControlFlow().addStatement("writer.writeEndArray()");

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

		CodeGeneratorContext childCtx = ctx.createDecodeChildContext(lv + "List.add(%s)");

		if (!field.disableDecodeNullCheck() && !hasParent()) {
			builder.beginControlFlow("if (bsonType != $T.NULL)", BsonType.class);
		}

		builder.addStatement("reader.readStartArray()");

		TypeMirror childType = this.getChildCodeGen().getType();

		if (childType.getKind().isPrimitive()) {
			builder.addStatement("$T<$T> $LList = new $T<>()", List.class,
					Util.typeUtils.boxedClass((PrimitiveType) childType), lv,
					ArrayList.class);
		}
		else {
			builder.addStatement("$T<$T> $LList = new $T<>()", List.class, childType, lv,
					ArrayList.class);
		}

		builder.beginControlFlow(
				"while ((bsonType = reader.readBsonType()) != $T.END_OF_DOCUMENT)",
				BsonType.class);

		if (!field.disableDecodeNullCheck()) {
			builder.beginControlFlow("if (bsonType != $T.NULL)", BsonType.class);
		}

		this.getChildCodeGen().addDecodeStatements(childCtx);

		if (!field.disableDecodeNullCheck()) {
			builder.nextControlFlow("else").addStatement("reader.readNull()");
			builder.addStatement(lv + "List.add(null)");
			builder.endControlFlow();
		}

		builder.endControlFlow().addStatement("reader.readEndArray()");

		if (childType.getKind().isPrimitive()) {
			builder.addStatement("$T[] $LArray = new $T[$LList.size()]", childType, lv,
					childType, lv)
					.beginControlFlow("for (int $L = 0; $L < $LList.size(); $L++)", lv,
							lv, lv, lv)
					.addStatement("$LArray[$L] = $LList.get($L)", lv, lv, lv, lv)
					.endControlFlow().addStatement(ctx.setter("$LArray"), lv);
		}
		else {
			if (Util.isArray(childType)) {
				builder.addStatement(ctx.setter("$LList.toArray(new $T[]{})"), lv,
						childType);
			}
			else {
				builder.addStatement(ctx.setter("$LList.toArray(new $T[$LList.size()])"),
						lv, childType, lv);
			}
		}

		if (!field.disableDecodeNullCheck() && !hasParent()) {
			builder.nextControlFlow("else").addStatement("reader.readNull()");
			if (!ctx.field().disableSetNullStatement()) {
				this.getChildCodeGen().addSetNullStatements(ctx);
			}
			builder.endControlFlow();
		}

	}

}
