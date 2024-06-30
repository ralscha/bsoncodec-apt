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

import org.bson.BsonInvalidOperationException;

import com.squareup.javapoet.MethodSpec;

import ch.rasc.bsoncodec.Util;
import ch.rasc.bsoncodec.codegen.CodeGeneratorContext;

public class PrimitiveWrapperDelegate implements CodeGeneratorDelegate {

	@Override
	public void addEncodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		MethodSpec.Builder builder = ctx.builder();

		if (Util.isSameType(type, Boolean.class)) {
			builder.addStatement("writer.writeBoolean($L.booleanValue())", ctx.getter());
		}
		else if (Util.isSameType(type, Byte.class)) {
			builder.addStatement("writer.writeInt32($L.intValue())", ctx.getter());
		}
		else if (Util.isSameType(type, Short.class)) {
			builder.addStatement("writer.writeInt32($L.intValue())", ctx.getter());
		}
		else if (Util.isSameType(type, Integer.class)) {
			builder.addStatement("writer.writeInt32($L.intValue())", ctx.getter());
		}
		else if (Util.isSameType(type, Long.class)) {
			builder.addStatement("writer.writeInt64($L.longValue())", ctx.getter());
		}
		else if (Util.isSameType(type, Character.class)) {
			builder.addStatement("writer.writeString($L.toString())", ctx.getter());
		}
		else if (Util.isSameType(type, Float.class)) {
			builder.addStatement("writer.writeDouble($L.doubleValue())", ctx.getter());
		}
		else if (Util.isSameType(type, Double.class)) {
			builder.addStatement("writer.writeDouble($L.doubleValue())", ctx.getter());
		}
	}

	@Override
	public void addDecodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		MethodSpec.Builder builder = ctx.builder();

		if (Util.isSameType(type, Boolean.class)) {
			builder.addStatement(ctx.setter("$T.valueOf(reader.readBoolean())"),
					Boolean.class);
		}
		else if (Util.isSameType(type, Byte.class)) {
			builder.addStatement(ctx.setter("$T.valueOf((byte)reader.readInt32())"),
					Byte.class);
		}
		else if (Util.isSameType(type, Short.class)) {
			builder.addStatement(ctx.setter("$T.valueOf((short)reader.readInt32())"),
					Short.class);
		}
		else if (Util.isSameType(type, Integer.class)) {
			builder.addStatement(ctx.setter("$T.valueOf(reader.readInt32())"),
					Integer.class);
		}
		else if (Util.isSameType(type, Long.class)) {
			builder.addStatement(ctx.setter("$T.valueOf(reader.readInt64())"),
					Long.class);
		}
		else if (Util.isSameType(type, Character.class)) {
			builder.addStatement("String string = reader.readString()");
			builder.beginControlFlow("if (string.length() != 1)");
			builder.addStatement(
					"throw new $T(String.format(\"Attempting to builder the string '%s' to a character, but its length is not equal to one\", string))",
					BsonInvalidOperationException.class);
			builder.endControlFlow();
			builder.addStatement(ctx.setter("$T.valueOf(string.charAt(0))"),
					Character.class);
		}
		else if (Util.isSameType(type, Float.class)) {
			builder.addStatement(ctx.setter("$T.valueOf((float)reader.readDouble())"),
					Float.class);
		}
		else if (Util.isSameType(type, Double.class)) {
			builder.addStatement(ctx.setter("$T.valueOf(reader.readDouble())"),
					Double.class);
		}
	}

	@Override
	public boolean accepts(TypeMirror type) {
		return Util.isAnyType(type, Boolean.class, Byte.class, Short.class, Integer.class,
				Long.class, Character.class, Float.class, Double.class);
	}

}
