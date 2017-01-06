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

import ch.rasc.bsoncodec.model.FieldModel;

public class CustomIdCodecCodeGen implements CodeGen {

	@Override
	public void addEncodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		ctx.builder().addStatement("writer.writeName($S)", field.name()).addStatement(
				"this.$N.encode(writer, $L, encoderContext)", field.idModel().codecName(),
				ctx.getter());
	}

	@Override
	public void addDecodeStatements(CodeGeneratorContext ctx) {
		ctx.builder().addStatement(ctx.setter("this.$N.decode(reader, decoderContext)"),
				ctx.field().idModel().codecName());
	}

}
