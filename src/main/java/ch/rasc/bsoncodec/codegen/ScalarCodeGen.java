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
package ch.rasc.bsoncodec.codegen;

import java.util.Arrays;
import java.util.List;

import javax.lang.model.type.TypeMirror;

import org.bson.BsonType;

import com.squareup.javapoet.MethodSpec.Builder;

import ch.rasc.bsoncodec.codegen.delegate.CodeGeneratorDelegate;
import ch.rasc.bsoncodec.codegen.delegate.CoreClassesDelegate;
import ch.rasc.bsoncodec.codegen.delegate.DefaultDelegate;
import ch.rasc.bsoncodec.codegen.delegate.EnumDelegate;
import ch.rasc.bsoncodec.codegen.delegate.ObjectIdDelegate;
import ch.rasc.bsoncodec.codegen.delegate.PrimitiveDelegate;
import ch.rasc.bsoncodec.codegen.delegate.PrimitiveWrapperDelegate;
import ch.rasc.bsoncodec.model.FieldModel;
import ch.rasc.bsoncodec.model.IdModel;

public class ScalarCodeGen extends CompoundCodeGen {

	private static final List<CodeGeneratorDelegate> delegates = Arrays.asList(
			new PrimitiveDelegate(), new PrimitiveWrapperDelegate(),
			new CoreClassesDelegate(), new EnumDelegate(), new ObjectIdDelegate());

	public static CodeGeneratorDelegate getDelegate(final TypeMirror type) {
		return delegates.stream().filter(d -> d.accepts(type)).findFirst()
				.orElse(new DefaultDelegate());
	}

	private final CodeGeneratorDelegate delegate;

	public static ScalarCodeGen create(CompoundCodeGen parent, TypeMirror type) {
		return create(parent, type, getDelegate(type));
	}

	public static ScalarCodeGen create(CompoundCodeGen parent, TypeMirror type,
			CodeGeneratorDelegate delegate) {
		return new ScalarCodeGen(parent, type, delegate);
	}

	private ScalarCodeGen(CompoundCodeGen parent, TypeMirror type,
			CodeGeneratorDelegate delegate) {
		super(parent, type);
		this.delegate = delegate;
	}

	@Override
	public void addEncodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		Builder builder = ctx.builder();

		if (!this.hasParent()) {
			if (!field.disableEncodeNullCheck()) {
				builder.beginControlFlow("if ($L != null)", ctx.getter());
			}

			IdModel idModel = field.idModel();
			if (idModel != null && idModel.generatorName() != null) {
				builder.beginControlFlow("if ($L == null)", ctx.getter())
						.addStatement(ctx.setter("($T)this.$N.generate()"), getType(),
								idModel.generatorName())
						.endControlFlow();
			}

			builder.addStatement("writer.writeName($S)", field.name());
		}

		this.delegate.addEncodeStatements(getType(), ctx);

		if (!this.hasParent()) {
			if (!field.disableEncodeNullCheck()) {
				if (field.storeNullValue()) {
					builder.nextControlFlow("else").addStatement("writer.writeNull($S)",
							field.name());
				}
				builder.endControlFlow();
			}
		}
	}

	@Override
	public void addDecodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		if (!hasParent()) {
			if (!field.disableDecodeNullCheck()) {
				ctx.builder().beginControlFlow("if (bsonType != $T.NULL)",
						BsonType.class);
			}
		}

		this.delegate.addDecodeStatements(getType(), ctx);

		if (!hasParent()) {
			if (!field.disableDecodeNullCheck()) {
				ctx.builder().nextControlFlow("else").addStatement("reader.readNull()");
				if (!ctx.field().disableSetNullStatement()) {
					this.delegate.addSetNullStatements(getType(), ctx);
				}
				ctx.builder().endControlFlow();
			}
		}

	}

}
