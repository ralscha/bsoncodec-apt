/**
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

import ch.rasc.bsoncodec.codegen.CodeGeneratorContext;

public interface CodeGeneratorDelegate {
	void addEncodeStatements(TypeMirror type, CodeGeneratorContext ctx);

	void addDecodeStatements(TypeMirror type, CodeGeneratorContext ctx);

	default void addSetNullStatements(@SuppressWarnings("unused") TypeMirror type,
			CodeGeneratorContext ctx) {
		ctx.builder().addStatement(ctx.setter("null"));
	}

	boolean accepts(TypeMirror type);
}
