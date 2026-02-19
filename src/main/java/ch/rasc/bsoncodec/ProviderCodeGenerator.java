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
package ch.rasc.bsoncodec;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.TypeVariableName;

import ch.rasc.bsoncodec.model.CodecInfo;
import ch.rasc.bsoncodec.model.InstanceField;

public class ProviderCodeGenerator {

	private final String fullyQualifiedName;

	private final List<CodecInfo> codecs;

	private final Set<InstanceField> instanceFields;

	ProviderCodeGenerator(String fullyQualifiedName, List<CodecInfo> codecs) {
		this.fullyQualifiedName = fullyQualifiedName;
		this.codecs = codecs;

		this.instanceFields = codecs.stream().flatMap(c -> c.instanceFields().stream())
				.collect(Collectors.toCollection(TreeSet::new));
	}

	public String getFullyQualifiedName() {
		return this.fullyQualifiedName;
	}

	public void generate(Appendable appendable) throws IOException {
		int pos = this.fullyQualifiedName.lastIndexOf(".");
		String className;
		String packageName;
		if (pos > 0) {
			packageName = this.fullyQualifiedName.substring(0, pos);
			className = this.fullyQualifiedName.substring(pos + 1);
		}
		else {
			packageName = "";
			className = this.fullyQualifiedName;
		}
		Builder classBuilder = TypeSpec.classBuilder(className);
		classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
		classBuilder.addSuperinterface(CodecProvider.class);

		addInstanceFields(classBuilder);
		addConstructor(classBuilder);
		addGetMethod(classBuilder);

		JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build()).build();
		javaFile.writeTo(appendable);
	}

	private void addConstructor(Builder classBuilder) {

		if (this.instanceFields.isEmpty()) {
			return;
		}

		MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC);
		constructor.addStatement(
				this.instanceFields.stream().map(s -> "new $T()")
						.collect(Collectors.joining(", ", "this(", ")")),
				this.instanceFields.stream().map(InstanceField::type)
						.collect(Collectors.toList()).toArray());
		classBuilder.addMethod(constructor.build());

		constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);

		for (InstanceField instanceField : this.instanceFields) {
			constructor.addParameter(instanceField.type(), instanceField.name(),
					Modifier.FINAL);
			constructor.addStatement("this.$N = $N", instanceField.name(),
					instanceField.name());
		}

		classBuilder.addMethod(constructor.build());

	}

	private void addInstanceFields(Builder classBuilder) {
		for (InstanceField instanceField : this.instanceFields) {
			FieldSpec field = FieldSpec
					.builder(instanceField.type(), instanceField.name())
					.addModifiers(Modifier.PRIVATE, Modifier.FINAL).build();
			classBuilder.addField(field);
		}
	}

	private void addGetMethod(Builder classBuilder) {
		TypeVariableName typeVariable = TypeVariableName.get("T");
		MethodSpec.Builder builder = MethodSpec.methodBuilder("get")
				.addTypeVariable(typeVariable)
				.returns(ParameterizedTypeName.get(ClassName.get(Codec.class),
						typeVariable))
				.addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
				.addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
						.addMember("value", "$S", "unchecked").build())
				.addParameter(ParameterizedTypeName.get(ClassName.get(Class.class),
						typeVariable), "clazz", Modifier.FINAL)
				.addParameter(CodecRegistry.class, "registry", Modifier.FINAL);

		for (CodecInfo codec : this.codecs) {
			builder.beginControlFlow("if (clazz.equals($T.class))", codec.valueType());

			if (codec.needRegistryField()) {
				if (codec.instanceFields().isEmpty()) {
					builder.addStatement("return (Codec<T>) new $T(registry)",
							codec.codecType());
				}
				else {
					builder.addStatement("return (Codec<T>) new $T(registry, $N)",
							codec.codecType(),
							codec.instanceFields().stream().map(i -> "this." + i.name())
									.collect(Collectors.joining(", ")));
				}
			}
			else {
				if (codec.instanceFields().isEmpty()) {
					builder.addStatement("return (Codec<T>) new $T()", codec.codecType());
				}
				else {
					builder.addStatement("return (Codec<T>) new $T($N)",
							codec.codecType(),
							codec.instanceFields().stream().map(i -> "this." + i.name())
									.collect(Collectors.joining(", ")));
				}
			}

			builder.endControlFlow();
		}

		builder.addStatement("return null");

		classBuilder.addMethod(builder.build());
	}

}
