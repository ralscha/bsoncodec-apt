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
package ch.rasc.bsoncodec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.SimpleTypeVisitor8;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import ch.rasc.bsoncodec.annotation.BsonDocument;
import ch.rasc.bsoncodec.annotation.Field;
import ch.rasc.bsoncodec.annotation.Field.NullCodec;
import ch.rasc.bsoncodec.annotation.Field.NullCollection;
import ch.rasc.bsoncodec.annotation.Id;
import ch.rasc.bsoncodec.annotation.Id.IdConversion;
import ch.rasc.bsoncodec.annotation.Id.NullIdGenerator;
import ch.rasc.bsoncodec.annotation.Transient;
import ch.rasc.bsoncodec.codegen.ArrayCodeGen;
import ch.rasc.bsoncodec.codegen.CodeGeneratorContext;
import ch.rasc.bsoncodec.codegen.CollectionCodeGen;
import ch.rasc.bsoncodec.codegen.CompoundCodeGen;
import ch.rasc.bsoncodec.codegen.ConversionObjectIdCodeGen;
import ch.rasc.bsoncodec.codegen.ConversionUUIDCodeGen;
import ch.rasc.bsoncodec.codegen.CustomIdCodecCodeGen;
import ch.rasc.bsoncodec.codegen.MapCodeGen;
import ch.rasc.bsoncodec.codegen.ScalarCodeGen;
import ch.rasc.bsoncodec.codegen.delegate.ByteArrayDelegate;
import ch.rasc.bsoncodec.codegen.delegate.CustomCodecDelegate;
import ch.rasc.bsoncodec.codegen.delegate.UnknownCodecDelegate;
import ch.rasc.bsoncodec.model.FieldModel;
import ch.rasc.bsoncodec.model.IdModel;
import ch.rasc.bsoncodec.model.ImmutableFieldModel;
import ch.rasc.bsoncodec.model.ImmutableIdModel;
import ch.rasc.bsoncodec.model.ImmutableInstanceField;
import ch.rasc.bsoncodec.model.InstanceField;

public class CodecCodeGenerator {

	private static final String ID_FIELD_NAME = "_id";

	private final TypeElement typeElement;

	private final String packageName;

	private final String className;

	private final ClassName thisType;

	private final Set<InstanceField> instanceFields = new TreeSet<>();

	private String providerClassName;

	public CodecCodeGenerator(TypeElement typeElement) {
		this.typeElement = typeElement;
		this.packageName = Util.elementUtils.getPackageOf(typeElement).getQualifiedName()
				.toString();
		this.className = typeElement.getSimpleName() + "Codec";
		this.thisType = ClassName.get(typeElement);
		if (this.packageName != null && !this.packageName.trim().isEmpty()) {
			this.providerClassName = this.packageName + ".PojoCodecProvider";
		}
		else {
			this.providerClassName = "PojoCodecProvider";
		}
	}

	public Set<InstanceField> getInstanceFields() {
		return this.instanceFields;
	}

	public String getClassName() {
		return this.className;
	}

	public String getProviderClassName() {
		return this.providerClassName;
	}

	public CharSequence getFullyQualifiedName() {
		if (this.packageName != null && this.packageName.trim().length() > 0) {
			return this.packageName + "." + this.className;
		}
		return this.className;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void generate(Appendable appendable) throws IOException {
		Builder classBuilder = TypeSpec.classBuilder(this.className);
		classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);

		ClassName codec = ClassName.get(Codec.class);
		classBuilder.addSuperinterface(ParameterizedTypeName.get(codec, this.thisType));

		List<FieldModel> fieldInfos = collectFields();
		addEncodeMethod(classBuilder, fieldInfos);
		addDecodeMethod(classBuilder, fieldInfos);
		addGetEncoderClassMethod(classBuilder);

		addInstanceFields(classBuilder);
		addConstructor(classBuilder);

		JavaFile javaFile = JavaFile.builder(this.packageName, classBuilder.build())
				.build();
		javaFile.writeTo(appendable);
	}

	private void addConstructor(Builder classBuilder) {
		if (this.instanceFields.isEmpty()) {
			return;
		}

		MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC);

		if (this.instanceFields.stream().filter(InstanceField::isRegistryCodec).findAny()
				.isPresent()) {
			constructor.addParameter(ClassName.get(CodecRegistry.class), "registry",
					Modifier.FINAL);
		}

		for (InstanceField instanceField : this.instanceFields) {
			if (instanceField.isRegistryCodec()) {
				constructor.addStatement(
						"this.$N = $T.notNull($S, registry.get($T.class))",
						instanceField.name(), Assertions.class, instanceField.name(),
						instanceField.codecForClass());
			}
			else {
				constructor.addParameter(instanceField.type(), instanceField.name(),
						Modifier.FINAL);
				constructor.addStatement("this.$N = $T.notNull($S, $N)",
						instanceField.name(), Assertions.class, instanceField.name(),
						instanceField.name());
			}
		}

		classBuilder.addMethod(constructor.build());

	}

	private void addInstanceFields(Builder classBuilder) {
		for (InstanceField instanceField : this.instanceFields) {
			FieldSpec field;
			if (instanceField.isRegistryCodec()) {
				field = FieldSpec
						.builder(
								ParameterizedTypeName.get(ClassName.get(Codec.class),
										instanceField.codecForClass()),
						instanceField.name())
						.addModifiers(Modifier.PRIVATE, Modifier.FINAL).build();
			}
			else {
				field = FieldSpec.builder(instanceField.type(), instanceField.name())
						.addModifiers(Modifier.PRIVATE, Modifier.FINAL).build();
			}
			classBuilder.addField(field);
		}
	}

	private void addGetEncoderClassMethod(Builder classBuilder) {
		MethodSpec getEncoderClassMethod = MethodSpec.methodBuilder("getEncoderClass")
				.returns(ParameterizedTypeName.get(ClassName.get(Class.class),
						this.thisType))
				.addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
				.addStatement("return $N.class", this.thisType.simpleName()).build();
		classBuilder.addMethod(getEncoderClassMethod);
	}

	private void addDecodeMethod(Builder classBuilder, List<FieldModel> fieldModel) {

		MethodSpec.Builder decode = MethodSpec.methodBuilder("decode")
				.returns(this.thisType).addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class).addParameter(BsonReader.class, "reader")
				.addParameter(DecoderContext.class, "decoderContext")

				.addStatement("$T value = new $T()", this.thisType, this.thisType)
				.addStatement("reader.readStartDocument()")
				.addStatement("$T bsonType", BsonType.class)
				.beginControlFlow(
						"while ((bsonType = reader.readBsonType()) != $T.END_OF_DOCUMENT)",
						BsonType.class)
				.addStatement("String name = reader.readName()");

		boolean allDisableSetNullStatement = fieldModel.stream()
				.allMatch(FieldModel::disableSetNullStatement);
		boolean someDecodeNullCheck = fieldModel.stream()
				.filter(f -> !f.disableDecodeNullCheck()).findAny().isPresent();
		boolean handleNull = allDisableSetNullStatement && someDecodeNullCheck;

		if (handleNull) {
			decode.beginControlFlow("if (bsonType != $T.NULL)", BsonType.class);
		}

		decode.beginControlFlow("switch (name)");

		for (FieldModel field : fieldModel) {
			decode.beginControlFlow("case $S:", field.name());

			String getter = "value." + field.methodNameGet() + "()";
			String setter = "value." + field.methodNameSet() + "(%s)";

			CodeGeneratorContext ctx = new CodeGeneratorContext(ImmutableFieldModel
					.copyOf(field).withDisableDecodeNullCheck(handleNull), decode,
					this.instanceFields, getter, setter);
			field.codeGen().addDecodeStatements(ctx);

			decode.addStatement("break");
			decode.endControlFlow();
		}

		decode.addStatement("default:\nreader.skipValue()").endControlFlow();

		if (handleNull) {
			decode.nextControlFlow("else").addStatement("reader.readNull()");
			decode.endControlFlow();
		}

		decode.endControlFlow().addStatement("reader.readEndDocument()")
				.addStatement("return value");
		classBuilder.addMethod(decode.build());
	}

	private void addEncodeMethod(Builder classBuilder, List<FieldModel> fieldModel) {
		MethodSpec.Builder encode = MethodSpec.methodBuilder("encode")
				.returns(TypeName.VOID).addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class).addParameter(BsonWriter.class, "writer")
				.addParameter(this.thisType, "value")
				.addParameter(EncoderContext.class, "encoderContext")
				.addStatement("writer.writeStartDocument()");

		for (FieldModel field : fieldModel) {

			String getter = "value." + field.methodNameGet() + "()";
			String setter = "value." + field.methodNameSet() + "(%s)";

			CodeGeneratorContext ctx = new CodeGeneratorContext(field, encode,
					this.instanceFields, getter, setter);
			field.codeGen().addEncodeStatements(ctx);
		}

		encode.addStatement("writer.writeEndDocument()");

		classBuilder.addMethod(encode.build());
	}

	private boolean filterEnclosedElements(Element el) {
		return !el.getModifiers().contains(Modifier.STATIC)
				&& el.getKind() == ElementKind.METHOD
				&& !el.getModifiers().contains(Modifier.PRIVATE)
				|| el.getKind() == ElementKind.FIELD
						&& el.getAnnotation(Transient.class) == null;
	}

	private List<FieldModel> collectFields() {
		List<FieldModel> fields = new ArrayList<>();

		BsonDocument bsonDocumentAnnotation = this.typeElement
				.getAnnotation(BsonDocument.class);

		boolean globalStoreNullValues = false;
		boolean globalStoreEmptyCollections = false;

		if (bsonDocumentAnnotation != null) {
			globalStoreNullValues = bsonDocumentAnnotation.storeNullValues();
			globalStoreEmptyCollections = bsonDocumentAnnotation.storeEmptyCollections();

			String annotationProviderClassName = bsonDocumentAnnotation
					.codecProviderClassName();
			if (!annotationProviderClassName.trim().isEmpty()) {
				this.providerClassName = annotationProviderClassName;
			}
		}
		else {
			List<? extends AnnotationMirror> allAnnotationMirrors = Util.elementUtils
					.getAllAnnotationMirrors(this.typeElement);
			for (AnnotationMirror am : allAnnotationMirrors) {
				List<? extends AnnotationMirror> allAnnotationMirrors2 = Util.elementUtils
						.getAllAnnotationMirrors(am.getAnnotationType().asElement());
				for (AnnotationMirror am2 : allAnnotationMirrors2) {
					if (BsonDocument.class.getCanonicalName()
							.equals(am2.getAnnotationType().toString())) {

						for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am2
								.getElementValues().entrySet()) {
							if ("storeNullValues"
									.equals(entry.getKey().getSimpleName().toString())) {
								globalStoreNullValues = (boolean) entry.getValue()
										.getValue();
							}
							else if ("storeEmptyCollections"
									.equals(entry.getKey().getSimpleName().toString())) {
								globalStoreEmptyCollections = (boolean) entry.getValue()
										.getValue();
							}
							else if ("codecProviderClassName"
									.equals(entry.getKey().getSimpleName().toString())) {
								String cn = (String) entry.getValue().getValue();
								if (!cn.trim().isEmpty()) {
									this.providerClassName = cn;
								}
							}
						}

					}
				}
			}

		}

		TypeElement currentElement = this.typeElement;
		Set<String> alreadyConsumed = new HashSet<>();

		while (currentElement != null) {
			Map<ElementKind, List<Element>> enclosedElements = currentElement
					.getEnclosedElements().stream().filter(this::filterEnclosedElements)
					.collect(
							Collectors.groupingBy(Element::getKind, Collectors.toList()));

			Map<String, String> getMethods = new HashMap<>();
			Map<String, String> setMethods = new HashMap<>();
			for (Element el : enclosedElements.get(ElementKind.METHOD)) {
				ExecutableElement method = (ExecutableElement) el;
				String methodName = method.getSimpleName().toString();
				List<? extends VariableElement> params = method.getParameters();
				TypeMirror returnType = method.getReturnType();

				if (params.size() == 0) {
					if (methodName.startsWith("get")) {
						getMethods.put(Util.uncapitalize(methodName.substring(3)),
								methodName);
					}
					else if (methodName.startsWith("is")
							&& returnType.getKind() == TypeKind.BOOLEAN) {
						getMethods.put(Util.uncapitalize(methodName.substring(2)),
								methodName);
					}
				}
				else if (params.size() == 1 && returnType.getKind() == TypeKind.VOID
						&& methodName.startsWith("set")) {
					setMethods.put(Util.uncapitalize(methodName.substring(3)),
							methodName);
				}
			}

			Element idElement = lookupId(enclosedElements.get(ElementKind.FIELD));

			int index = 1;
			for (Element el : enclosedElements.get(ElementKind.FIELD)) {
				VariableElement varEl = (VariableElement) el;
				String varName = varEl.getSimpleName().toString();
				if (!alreadyConsumed.contains(varName)) {
					alreadyConsumed.add(varName);

					ImmutableFieldModel.Builder builder = ImmutableFieldModel.builder();
					builder.storeEmptyCollection(globalStoreEmptyCollections);
					builder.storeNullValue(globalStoreNullValues);
					builder.varEl(varEl);

					builder.methodNameSet(setMethods.get(varName));
					builder.methodNameGet(getMethods.get(varName));

					if (el == idElement) {
						handleIdElement(builder, varEl);
					}
					else {
						handleNormalElement(index, builder, varEl);
					}

					fields.add(builder.build());
					index++;
				}
			}

			TypeMirror superclass = currentElement.getSuperclass();
			if (Util.isSameType(superclass, Object.class)) {
				currentElement = null;
			}
			else {
				currentElement = (TypeElement) Util.typeUtils.asElement(superclass);
			}

		}

		Collections.sort(fields);
		return fields;
	}

	private void handleNormalElement(int index, ImmutableFieldModel.Builder builder,
			VariableElement varEl) {

		TypeMirror collImplType = null;
		Field fieldAnnotation = varEl.getAnnotation(Field.class);
		if (fieldAnnotation == null) {
			builder.order(index);
		}
		else {
			String name = fieldAnnotation.value();
			if (!name.trim().isEmpty()) {
				builder.name(name);
			}
			if (fieldAnnotation.order() != Integer.MAX_VALUE) {
				builder.order(fieldAnnotation.order());
			}
			else {
				builder.order(index);
			}

			TypeName customCodecName = null;
			try {
				customCodecName = ClassName.get(fieldAnnotation.codec());
			}
			catch (MirroredTypeException e) {
				customCodecName = TypeName.get(e.getTypeMirror());
			}

			if (customCodecName != null && !customCodecName.toString()
					.equals(NullCodec.class.getCanonicalName())) {

				String customCodecVarName = Util.varName(customCodecName.toString());
				builder.customCodecName(customCodecVarName);
				this.instanceFields
						.add(ImmutableInstanceField.builder().type(customCodecName)
								.name(customCodecVarName).customCodec(true).build());

				builder.codeGen(ScalarCodeGen.create(null, varEl.asType(),
						new CustomCodecDelegate(customCodecVarName)));
				return;
			}

			try {
				collImplType = Util.elementUtils.getTypeElement(fieldAnnotation
						.collectionImplementationClass().getCanonicalName()).asType();
			}
			catch (MirroredTypeException e) {
				collImplType = e.getTypeMirror();
			}
		}

		CompoundCodeGen acg = collectCodeGen(varEl.asType(), null);
		if (acg instanceof CollectionCodeGen) {
			if (collImplType != null && !collImplType.toString()
					.equals(NullCollection.class.getCanonicalName())) {
				((CollectionCodeGen) acg).setImplementationType(collImplType);
			}
		}
		builder.codeGen(acg);

	}

	private static CompoundCodeGen collectCodeGen(final TypeMirror type,
			CompoundCodeGen prev) {
		return type.accept(new SimpleTypeVisitor8<CompoundCodeGen, CompoundCodeGen>() {
			@Override
			public CompoundCodeGen visitDeclared(DeclaredType declaredType,
					CompoundCodeGen parent) {

				if (Util.isCollection(declaredType)) {
					CompoundCodeGen cg = new CollectionCodeGen(parent, declaredType);
					List<? extends TypeMirror> typeArguments = declaredType
							.getTypeArguments();
					if (!typeArguments.isEmpty()) {
						collectCodeGen(typeArguments.get(0), cg);
					}
					else {
						ScalarCodeGen.create(cg,
								Util.elementUtils
										.getTypeElement(Object.class.getCanonicalName())
										.asType(),
								new UnknownCodecDelegate());
					}
					return cg;
				}
				else if (Util.isMap(declaredType)) {
					List<? extends TypeMirror> typeArguments = declaredType
							.getTypeArguments();

					if (typeArguments.size() == 2) {
						MapCodeGen cg = new MapCodeGen(parent, declaredType,
								typeArguments.get(0));
						collectCodeGen(typeArguments.get(1), cg);
						return cg;
					}

				}
				return ScalarCodeGen.create(parent, declaredType);
			}

			@Override
			public CompoundCodeGen visitPrimitive(PrimitiveType primitiveType,
					CompoundCodeGen parent) {
				return ScalarCodeGen.create(parent, primitiveType);
			}

			@Override
			public CompoundCodeGen visitArray(ArrayType arrayType,
					CompoundCodeGen parent) {
				TypeMirror componentType = arrayType.getComponentType();

				if (Util.isByte(componentType)) {
					return ScalarCodeGen.create(parent, componentType,
							new ByteArrayDelegate());
				}

				ArrayCodeGen cg = new ArrayCodeGen(parent, arrayType);
				if (Util.isArray(componentType)) {
					collectCodeGen(componentType, cg);
				}
				else {
					ScalarCodeGen.create(cg, componentType);
				}
				return cg;
			}

			@Override
			public CompoundCodeGen visitTypeVariable(TypeVariable typeVariable,
					CompoundCodeGen parent) {
				return this.DEFAULT_VALUE;
			}

			@Override
			public CompoundCodeGen visitError(ErrorType errorType,
					CompoundCodeGen parent) {
				return this.DEFAULT_VALUE;
			}

			@Override
			protected CompoundCodeGen defaultAction(TypeMirror typeMirror,
					CompoundCodeGen parent) {
				throw new UnsupportedOperationException("Unexpected TypeKind "
						+ typeMirror.getKind() + " for " + typeMirror);
			}
		}, prev);
	}

	private void handleIdElement(ImmutableFieldModel.Builder builder,
			VariableElement varEl) {
		ImmutableIdModel.Builder idModelBuilder = ImmutableIdModel.builder();
		builder.name(ID_FIELD_NAME).order(0);

		Id idAnnotation = varEl.getAnnotation(Id.class);
		boolean idGeneratorSet = false;

		if (idAnnotation != null) {
			TypeName idCodecTypeName = null;
			try {
				idCodecTypeName = ClassName.get(idAnnotation.codec());
			}
			catch (MirroredTypeException e) {
				idCodecTypeName = TypeName.get(e.getTypeMirror());
			}

			if (idCodecTypeName != null && !idCodecTypeName.toString()
					.equals(NullCodec.class.getCanonicalName())) {
				String idCodecVarName = Util.varName(idCodecTypeName.toString());
				this.instanceFields.add(ImmutableInstanceField.builder()
						.type(idCodecTypeName).name(idCodecVarName).build());
				idModelBuilder.codecName(idCodecVarName);

				IdModel idModel = idModelBuilder.build();
				builder.codeGen(new CustomIdCodecCodeGen());
				builder.idModel(idModel);

				// skip conversion and idGenerator
				return;
			}

			TypeName idGeneratorTypeName = null;
			try {
				idGeneratorTypeName = ClassName.get(idAnnotation.generator());
			}
			catch (MirroredTypeException e) {
				idGeneratorTypeName = TypeName.get(e.getTypeMirror());
			}

			if (Util.isSameType(varEl.asType(), String.class)) {
				idModelBuilder.conversion(idAnnotation.conversion());
			}

			if (idGeneratorTypeName != null && !idGeneratorTypeName.toString()
					.equals(NullIdGenerator.class.getCanonicalName())) {
				String idGeneratorName = Util.varName(idGeneratorTypeName.toString());
				this.instanceFields.add(ImmutableInstanceField.builder()
						.type(idGeneratorTypeName).name(idGeneratorName).build());
				idModelBuilder.generatorName(idGeneratorName);
				idGeneratorSet = true;
			}
			else if (idAnnotation.conversion() == IdConversion.BASE64_OBJECTID
					|| idAnnotation.conversion() == IdConversion.HEX_OBJECTID) {
				String idGeneratorName = "objectIdGenerator";
				this.instanceFields.add(ImmutableInstanceField.builder()
						.type(ClassName.get(ObjectIdGenerator.class))
						.name(idGeneratorName).build());

				idModelBuilder.generatorName(idGeneratorName);
				idGeneratorSet = true;
			}

		}

		if (!idGeneratorSet && isObjectId(varEl)) {
			String idGeneratorName = "objectIdGenerator";
			this.instanceFields.add(ImmutableInstanceField.builder()
					.type(ClassName.get(ObjectIdGenerator.class)).name(idGeneratorName)
					.build());

			idModelBuilder.generatorName(idGeneratorName);
		}

		IdModel idModel = idModelBuilder.build();
		if (idModel.conversion() == IdConversion.BASE64_OBJECTID
				|| idModel.conversion() == IdConversion.HEX_OBJECTID) {
			builder.codeGen(new ConversionObjectIdCodeGen());
		}
		else if (idModel.conversion() == IdConversion.BASE64_UUID
				|| idModel.conversion() == IdConversion.HEX_UUID) {
			builder.codeGen(new ConversionUUIDCodeGen());
		}
		else {
			builder.codeGen(ScalarCodeGen.create(null, varEl.asType()));
		}

		builder.idModel(idModel);
	}

	/**
	 * Find a field with the @Id annotation OR with type ObjectId OR with type UUID OR
	 * with name _id OR with name id
	 */
	private static Element lookupId(List<Element> fields) {
		Element objectIdField = null;
		Element uuidField = null;
		Element _idField = null;
		Element idField = null;
		for (Element field : fields) {
			if (field.getAnnotation(Id.class) != null) {
				return field;
			}
			if (isObjectId(field)) {
				objectIdField = field;
			}
			else if (isUUID(field)) {
				uuidField = field;
			}
			else if (field.getSimpleName().contentEquals(ID_FIELD_NAME)) {
				_idField = field;
			}
			else if (field.getSimpleName().contentEquals("id")) {
				idField = field;
			}
		}

		if (objectIdField != null) {
			return objectIdField;
		}
		if (uuidField != null) {
			return uuidField;
		}
		if (_idField != null) {
			return _idField;
		}
		if (idField != null) {
			return idField;
		}

		return null;
	}

	private static boolean isUUID(Element field) {
		return field.asType().toString().equals(UUID.class.getCanonicalName());
	}

	private static boolean isObjectId(Element field) {
		return field.asType().toString().equals(ObjectId.class.getCanonicalName());
	}

}
