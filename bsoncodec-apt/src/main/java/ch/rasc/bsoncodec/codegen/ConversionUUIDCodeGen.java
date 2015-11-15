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

import java.util.UUID;

import org.omg.IOP.Codec;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;

import ch.rasc.bsoncodec.model.FieldModel;
import ch.rasc.bsoncodec.model.ImmutableInstanceField;

public class ConversionUUIDCodeGen implements CodeGen {

	@Override
	public void addEncodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		Builder builder = ctx.builder();

		if (ctx.field().idModel().generatorName() != null) {
			builder.addStatement("$T id", UUID.class)
					.beginControlFlow("if ($L == null)", ctx.getter())
					.addStatement("id = ($T)this.$N.generate()", UUID.class,
							field.idModel().generatorName());

			builder.addStatement(ctx.setter("id.toString()"));
			builder.nextControlFlow("else");

			builder.addStatement("id = $T.fromString($L)", UUID.class, ctx.getter());
			builder.endControlFlow();
		}
		else {
			builder.addStatement("$T id = $T.fromString($L)", UUID.class, UUID.class,
					ctx.getter());
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

		builder.addStatement("$T id = this.uUIDCodec.decode(reader, decoderContext)",
				UUID.class);
		builder.addStatement(ctx.setter("id.toString()"));
	}

}
