/*
 * Copyright the original author or authors.
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

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

import org.bson.codecs.Codec;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;

import ch.rasc.bsoncodec.annotation.Id.IdConversion;
import ch.rasc.bsoncodec.model.FieldModel;
import ch.rasc.bsoncodec.model.IdModel;
import ch.rasc.bsoncodec.model.ImmutableInstanceField;

public class ConversionUUIDCodeGen implements CodeGen {

	@Override
	public void addEncodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		IdModel idModel = field.idModel();
		Builder builder = ctx.builder();

		if (idModel != null && idModel.generatorName() != null) {
			builder.addStatement("$T id", UUID.class)
					.beginControlFlow("if ($L == null)", ctx.getter())
					.addStatement("id = ($T)this.$N.generate()", UUID.class,
							idModel.generatorName());

			if (idModel.conversion() == IdConversion.HEX_UUID) {
				builder.addStatement(ctx.setter("id.toString()"));
			}
			else if (idModel.conversion() == IdConversion.BASE64_UUID) {
				builder.addStatement("$T bb = $T.wrap(new byte[16])", ByteBuffer.class,
						ByteBuffer.class);
				builder.addStatement("bb.putLong(id.getMostSignificantBits())");
				builder.addStatement("bb.putLong(id.getLeastSignificantBits())");
				builder.addStatement(
						ctx.setter("$T.getUrlEncoder().encodeToString(bb.array())"),
						Base64.class);
			}
			builder.nextControlFlow("else");

			if (idModel.conversion() == IdConversion.HEX_UUID) {
				builder.addStatement("id = $T.fromString($L)", UUID.class, ctx.getter());
			}
			else if (idModel.conversion() == IdConversion.BASE64_UUID) {
				builder.addStatement("$T bb = $T.wrap($T.getUrlDecoder().decode($L))",
						ByteBuffer.class, ByteBuffer.class, Base64.class, ctx.getter());
				builder.addStatement("id = new $T(bb.getLong(), bb.getLong())",
						UUID.class);
			}
			builder.endControlFlow();
		}
		else {
			if (idModel != null) {
				if (idModel.conversion() == IdConversion.HEX_UUID) {
					builder.addStatement("$T id = $T.fromString($L)", UUID.class,
							UUID.class, ctx.getter());
				}
				else if (idModel.conversion() == IdConversion.BASE64_UUID) {
					builder.addStatement("$T bb = $T.wrap($T.getUrlDecoder().decode($L))",
							ByteBuffer.class, ByteBuffer.class, Base64.class,
							ctx.getter());
					builder.addStatement("$T id = new $T(bb.getLong(), bb.getLong())",
							UUID.class, UUID.class);
				}
			}
		}

		ctx.builder().addStatement("writer.writeName($S)", field.name())
				.addStatement("this.uUIDCodec.encode(writer, id, encoderContext)");

		ctx.instanceFields()
				.add(ImmutableInstanceField.builder().type(ClassName.get(Codec.class))
						.name("uUIDCodec").codecForClass(ClassName.get(UUID.class))
						.build());
	}

	@Override
	public void addDecodeStatements(CodeGeneratorContext ctx) {
		Builder builder = ctx.builder();
		IdModel idModel = ctx.field().idModel();

		builder.addStatement("$T id = this.uUIDCodec.decode(reader, decoderContext)",
				UUID.class);

		if (idModel != null) {
			if (idModel.conversion() == IdConversion.HEX_UUID) {
				builder.addStatement(ctx.setter("id.toString()"));
			}
			else if (idModel.conversion() == IdConversion.BASE64_UUID) {
				builder.addStatement("$T bb = $T.wrap(new byte[16])", ByteBuffer.class,
						ByteBuffer.class);
				builder.addStatement("bb.putLong(id.getMostSignificantBits())");
				builder.addStatement("bb.putLong(id.getLeastSignificantBits())");
				builder.addStatement(
						ctx.setter("$T.getUrlEncoder().encodeToString(bb.array())"),
						Base64.class);
			}
		}
	}

}
