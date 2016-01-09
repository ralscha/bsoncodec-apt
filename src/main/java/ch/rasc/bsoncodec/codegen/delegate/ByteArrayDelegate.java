/**
 * Copyright 2015-2016 Ralph Schaer <ralphschaer@gmail.com>
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

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.bson.BsonBinary;

import com.squareup.javapoet.MethodSpec;

import ch.rasc.bsoncodec.Util;
import ch.rasc.bsoncodec.codegen.CodeGeneratorContext;

public class ByteArrayDelegate implements CodeGeneratorDelegate {

	@Override
	public void addEncodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		MethodSpec.Builder builder = ctx.builder();

		if (type.getKind() == TypeKind.BYTE) {
			builder.addStatement("writer.writeBinaryData(new $T($L))", BsonBinary.class,
					ctx.getter());
		}
		else {
			builder.addStatement("$T[] bw = $L", Byte.class, ctx.getter())
					.addStatement("byte[] b = new byte[bw.length]")
					.beginControlFlow("for (int i = 0; i < bw.length; i++)")
					.addStatement("b[i] = bw[i]").endControlFlow()
					.addStatement("writer.writeBinaryData(new $T(b))", BsonBinary.class);
		}
	}

	@Override
	public void addDecodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		MethodSpec.Builder builder = ctx.builder();

		if (type.getKind() == TypeKind.BYTE) {
			builder.addStatement(ctx.setter("reader.readBinaryData().getData()"));
		}
		else {
			builder.addStatement("byte[] b = reader.readBinaryData().getData()")
					.addStatement("$T[] bw = new $T[b.length]", Byte.class, Byte.class)
					.beginControlFlow("for (int i = 0; i < b.length; i++)")
					.addStatement("bw[i] = $T.valueOf(b[i])", Byte.class).endControlFlow()
					.addStatement(ctx.setter("bw"));
		}
	}

	@Override
	public boolean accepts(TypeMirror type) {
		return Util.isByte(type);
	}

}
