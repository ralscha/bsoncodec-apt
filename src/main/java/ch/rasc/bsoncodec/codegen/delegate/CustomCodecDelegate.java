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
package ch.rasc.bsoncodec.codegen.delegate;

import javax.lang.model.type.TypeMirror;

import ch.rasc.bsoncodec.codegen.CodeGeneratorContext;

public class CustomCodecDelegate implements CodeGeneratorDelegate {

	private final String customCodecName;

	public CustomCodecDelegate(String customCodecName) {
		this.customCodecName = customCodecName;
	}

	@Override
	public void addEncodeStatements(TypeMirror typeMirror, CodeGeneratorContext ctx) {
		ctx.builder().addStatement("this.$N.encode(writer, $L, encoderContext)",
				this.customCodecName, ctx.getter());
	}

	@Override
	public void addDecodeStatements(TypeMirror typeMirror, CodeGeneratorContext ctx) {
		ctx.builder().addStatement(ctx.setter("this.$N.decode(reader, decoderContext)"),
				this.customCodecName);
	}

	@Override
	public boolean accepts(TypeMirror type) {
		return false;
	}
}
