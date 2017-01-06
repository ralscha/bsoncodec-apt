/**
 * Copyright 2015-2017 Ralph Schaer <ralphschaer@gmail.com>
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

import java.util.Base64;

import org.bson.types.ObjectId;

import com.squareup.javapoet.MethodSpec.Builder;

import ch.rasc.bsoncodec.annotation.Id.IdConversion;
import ch.rasc.bsoncodec.model.IdModel;

public class ConversionObjectIdCodeGen implements CodeGen {

	@Override
	public void addEncodeStatements(CodeGeneratorContext ctx) {
		IdModel idModel = ctx.field().idModel();
		Builder builder = ctx.builder();

		builder.addStatement("$T id", ObjectId.class)
				.beginControlFlow("if ($L == null)", ctx.getter())
				.addStatement("id = ($T)this.$N.generate()", ObjectId.class,
						idModel.generatorName());

		if (idModel.conversion() == IdConversion.HEX_OBJECTID) {
			builder.addStatement(ctx.setter("id.toHexString()"));
		}
		else if (idModel.conversion() == IdConversion.BASE64_OBJECTID) {
			builder.addStatement(
					ctx.setter("$T.getUrlEncoder().encodeToString(id.toByteArray())"),
					Base64.class);
		}

		builder.nextControlFlow("else");

		if (idModel.conversion() == IdConversion.HEX_OBJECTID) {
			builder.addStatement("id = new $T($L)", ObjectId.class, ctx.getter());
		}
		else if (idModel.conversion() == IdConversion.BASE64_OBJECTID) {
			builder.addStatement("id = new $T($T.getUrlDecoder().decode($L))",
					ObjectId.class, Base64.class, ctx.getter());
		}
		builder.endControlFlow().addStatement("writer.writeObjectId($S, id)",
				ctx.field().name());
	}

	@Override
	public void addDecodeStatements(CodeGeneratorContext ctx) {
		Builder builder = ctx.builder();
		IdModel idModel = ctx.field().idModel();

		builder.addStatement("$T id = reader.readObjectId()", ObjectId.class);

		if (idModel.conversion() == IdConversion.HEX_OBJECTID) {
			builder.addStatement(ctx.setter("id.toHexString()"));
		}
		else if (idModel.conversion() == IdConversion.BASE64_OBJECTID) {
			builder.addStatement(
					ctx.setter("$T.getUrlEncoder().encodeToString(id.toByteArray())"),
					Base64.class);
		}
	}

}
