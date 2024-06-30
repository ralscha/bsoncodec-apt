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

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.MethodSpec;

import ch.rasc.bsoncodec.Util;
import ch.rasc.bsoncodec.codegen.CodeGeneratorContext;

public class CoreClassesDelegate implements CodeGeneratorDelegate {

	@Override
	public void addEncodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		MethodSpec.Builder builder = ctx.builder();

		if (Util.isSameType(type, String.class)) {
			builder.addStatement("writer.writeString($L)", ctx.getter());
		}
		else if (Util.isSameType(type, Date.class)) {
			builder.addStatement("writer.writeDateTime($L.getTime())", ctx.getter());
		}
		else if (Util.isSameType(type, AtomicBoolean.class)) {
			builder.addStatement("writer.writeBoolean($L.get())", ctx.getter());
		}
		else if (Util.isSameType(type, AtomicInteger.class)) {
			builder.addStatement("writer.writeInt32($L.intValue())", ctx.getter());
		}
		else if (Util.isSameType(type, AtomicLong.class)) {
			builder.addStatement("writer.writeInt64($L.longValue())", ctx.getter());
		}

	}

	@Override
	public void addDecodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		MethodSpec.Builder builder = ctx.builder();

		if (Util.isSameType(type, String.class)) {
			builder.addStatement(ctx.setter("reader.readString()"));
		}
		else if (Util.isSameType(type, Date.class)) {
			builder.addStatement(ctx.setter("new $T(reader.readDateTime())"), Date.class);
		}
		else if (Util.isSameType(type, AtomicBoolean.class)) {
			builder.addStatement(ctx.setter("new $T(reader.readBoolean())"),
					AtomicBoolean.class);
		}
		else if (Util.isSameType(type, AtomicInteger.class)) {
			builder.addStatement(ctx.setter("new $T(reader.readInt32())"),
					AtomicInteger.class);
		}
		else if (Util.isSameType(type, AtomicLong.class)) {
			builder.addStatement(ctx.setter("new $T(reader.readInt64())"),
					AtomicLong.class);
		}
	}

	@Override
	public boolean accepts(TypeMirror type) {
		return Util.isAnyType(type, String.class, Date.class, AtomicBoolean.class,
				AtomicInteger.class, AtomicLong.class);
	}

}
