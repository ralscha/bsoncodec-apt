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
package ch.rasc.bsoncodec.codegen.delegate;

import javax.lang.model.type.TypeMirror;

import org.bson.BsonInvalidOperationException;

import com.squareup.javapoet.MethodSpec.Builder;

import ch.rasc.bsoncodec.codegen.CodeGeneratorContext;

public class PrimitiveDelegate implements CodeGeneratorDelegate {

	@Override
	public void addEncodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		Builder builder = ctx.builder();
		switch (type.getKind()) {
		case BOOLEAN:
			builder.addStatement("writer.writeBoolean($L)", ctx.getter());
			break;
		case BYTE:
			builder.addStatement("writer.writeInt32($L)", ctx.getter());
			break;
		case SHORT:
			builder.addStatement("writer.writeInt32($L)", ctx.getter());
			break;
		case INT:
			builder.addStatement("writer.writeInt32($L)", ctx.getter());
			break;
		case LONG:
			builder.addStatement("writer.writeInt64($L)", ctx.getter());
			break;
		case CHAR:
			builder.addStatement("writer.writeString(String.valueOf($L))", ctx.getter());
			break;
		case FLOAT:
			builder.addStatement("writer.writeDouble($L)", ctx.getter());
			break;
		case DOUBLE:
			builder.addStatement("writer.writeDouble($L)", ctx.getter());
			break;
		default:
			break;
		}

	}

	@Override
	public void addDecodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		Builder builder = ctx.builder();
		switch (type.getKind()) {
		case BOOLEAN:
			builder.addStatement(ctx.setter("reader.readBoolean()"));
			break;
		case BYTE:
			builder.addStatement(ctx.setter("(byte)reader.readInt32()"));
			break;
		case SHORT:
			builder.addStatement(ctx.setter("(short)reader.readInt32()"));
			break;
		case INT:
			builder.addStatement(ctx.setter("reader.readInt32()"));
			break;
		case LONG:
			builder.addStatement(ctx.setter("reader.readInt64()"));
			break;
		case CHAR:
			builder.addStatement("String string = reader.readString()");
			builder.beginControlFlow("if (string.length() != 1)");
			builder.addStatement(
					"throw new $T(String.format(\"Attempting to builder the string '%s' to a character, but its length is not equal to one\", string))",
					BsonInvalidOperationException.class);
			builder.endControlFlow();
			builder.addStatement(ctx.setter("string.charAt(0)"));
			break;
		case FLOAT:
			builder.addStatement(ctx.setter("(float)reader.readDouble()"));
			break;
		case DOUBLE:
			builder.addStatement(ctx.setter("reader.readDouble()"));
			break;
		default:
			break;
		}

	}

	@Override
	public void addSetNullStatements(TypeMirror type, CodeGeneratorContext ctx) {
		Builder builder = ctx.builder();
		switch (type.getKind()) {
		case BOOLEAN:
			builder.addStatement(ctx.setter("false"));
			break;
		case BYTE:
			builder.addStatement(ctx.setter("(byte)0"));
			break;
		case SHORT:
			builder.addStatement(ctx.setter("(short)0"));
			break;
		case INT:
			builder.addStatement(ctx.setter("0"));
			break;
		case LONG:
			builder.addStatement(ctx.setter("0L"));
			break;
		case CHAR:
			builder.addStatement(ctx.setter("$T.MIN_VALUE"), Character.class);
			break;
		case FLOAT:
			builder.addStatement(ctx.setter("0.0f"));
			break;
		case DOUBLE:
			builder.addStatement(ctx.setter("0.0d"));
			break;
		default:
			break;
		}
	}

	@Override
	public boolean accepts(TypeMirror type) {
		return type.getKind().isPrimitive();
	}

}
