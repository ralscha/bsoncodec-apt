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

import javax.lang.model.type.TypeMirror;

import org.bson.types.Decimal128;

import ch.rasc.bsoncodec.Util;
import ch.rasc.bsoncodec.codegen.CodeGeneratorContext;

public class Decimal128Delegate implements CodeGeneratorDelegate {

	@Override
	public void addEncodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		ctx.builder().addStatement("writer.writeDecimal128($L)", ctx.getter());
	}

	@Override
	public void addDecodeStatements(TypeMirror type, CodeGeneratorContext ctx) {
		ctx.builder().addStatement(ctx.setter("reader.readDecimal128()"));
	}

	@Override
	public boolean accepts(TypeMirror type) {
		return Util.isSameType(type, Decimal128.class);
	}

}
