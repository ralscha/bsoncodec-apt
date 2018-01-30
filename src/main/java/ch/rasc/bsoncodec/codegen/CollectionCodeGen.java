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
package ch.rasc.bsoncodec.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.bson.BsonType;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import ch.rasc.bsoncodec.Util;
import ch.rasc.bsoncodec.model.FieldModel;

public class CollectionCodeGen extends CompoundCodeGen {

	private TypeMirror implementationType;

	public CollectionCodeGen(CompoundCodeGen parent, TypeMirror type) {
		super(parent, type);
	}

	public void setImplementationType(TypeMirror implementationType) {
		this.implementationType = implementationType;
	}

	protected TypeName getImplementationType() {
		if (this.implementationType == null) {
			Class<?> impl = getDefaultImplementation();
			if (impl != null) {
				return ClassName.get(impl);
			}
			return ClassName.get((TypeElement) Util.typeUtils.asElement(this.getType()));
		}
		return ClassName
				.get((TypeElement) Util.typeUtils.asElement(this.implementationType));
	}

	@Override
	public void addEncodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		Builder builder = ctx.builder();

		if (!field.disableEncodeNullCheck() && !hasParent()) {
			builder.beginControlFlow("if ($L != null)", ctx.getter());
		}

		if (!field.storeEmptyCollection()) {
			builder.beginControlFlow("if (!$L.isEmpty())", ctx.getter());
		}

		if (!hasParent()) {
			builder.addStatement("writer.writeName($S)", field.name());
		}

		TypeMirror childType = this.getChildCodeGen().getType();
		builder.addStatement("writer.writeStartArray()").beginControlFlow(
				"for ($T $L : $L)", childType, ctx.getLoopVar(), ctx.getter());

		boolean permittNullElements = permitNullElements();
		if (!field.disableEncodeNullCheck() && permittNullElements) {
			builder.beginControlFlow("if ($L != null)", ctx.getLoopVar());
		}

		this.getChildCodeGen().addEncodeStatements(
				ctx.createEncodeChildContext(String.valueOf(ctx.getLoopVar())));

		if (!field.disableEncodeNullCheck() && permittNullElements) {
			builder.nextControlFlow("else").addStatement("writer.writeNull()");
			builder.endControlFlow();
		}

		builder.endControlFlow().addStatement("writer.writeEndArray()");

		if (!field.storeEmptyCollection()) {
			builder.endControlFlow();
		}

		if (!field.disableEncodeNullCheck() && !hasParent()) {
			if (field.storeNullValue()) {
				builder.nextControlFlow("else").addStatement("writer.writeNull($S)",
						field.name());
			}
			builder.endControlFlow();
		}
	}

	@Override
	public void addDecodeStatements(CodeGeneratorContext ctx) {
		FieldModel field = ctx.field();
		Builder builder = ctx.builder();
		char lv = ctx.getLoopVar();

		CodeGeneratorContext childCtx = ctx.createDecodeChildContext(lv + ".add(%s)");

		if (!field.disableDecodeNullCheck() && !hasParent()) {
			builder.beginControlFlow("if (bsonType != $T.NULL)", BsonType.class);
		}

		builder.addStatement("reader.readStartArray()");

		TypeMirror childType = this.getChildCodeGen().getType();
		if (Util.isSameType(getType(), EnumSet.class)) {
			builder.addStatement("$T $L = $T.noneOf($T.class)", getType(), lv,
					EnumSet.class, childType);
		}
		else {
			builder.addStatement("$T $L = new $T<>()", getType(), lv,
					this.getImplementationType());
		}

		builder.beginControlFlow(
				"while ((bsonType = reader.readBsonType()) != $T.END_OF_DOCUMENT)",
				BsonType.class);

		boolean permittNullElements = permitNullElements();
		if (permittNullElements) {
			builder.beginControlFlow("if (bsonType != $T.NULL)", BsonType.class);
		}

		this.getChildCodeGen().addDecodeStatements(childCtx);

		if (permittNullElements) {
			builder.nextControlFlow("else").addStatement("reader.readNull()");
			builder.addStatement(lv + ".add(null)");
			builder.endControlFlow();
		}

		builder.endControlFlow().addStatement("reader.readEndArray()");

		builder.addStatement(ctx.setter("$L"), lv);

		if (!field.disableDecodeNullCheck() && !hasParent()) {
			builder.nextControlFlow("else").addStatement("reader.readNull()");
			if (!ctx.field().disableSetNullStatement()) {
				this.getChildCodeGen().addSetNullStatements(ctx);
			}
			builder.endControlFlow();
		}

	}

	private static Map<String, Class<?>> defaultCollImpl = new HashMap<>();
	private static Set<String> permitNullCollections = new HashSet<>();

	static {
		addToDefaultCollImpl(Collection.class, ArrayList.class);
		addToDefaultCollImpl(List.class, ArrayList.class);
		addToDefaultCollImpl(Set.class, LinkedHashSet.class);
		addToDefaultCollImpl(SortedSet.class, TreeSet.class);
		addToDefaultCollImpl(NavigableSet.class, TreeSet.class);
		addToDefaultCollImpl(BlockingDeque.class, LinkedBlockingDeque.class);
		addToDefaultCollImpl(BlockingQueue.class, LinkedBlockingQueue.class);
		addToDefaultCollImpl(Deque.class, LinkedBlockingDeque.class);
		addToDefaultCollImpl(Queue.class, LinkedBlockingQueue.class);
		addToDefaultCollImpl(TransferQueue.class, LinkedTransferQueue.class);

		permitNullCollections.addAll(Arrays.asList(ArrayList.class.getCanonicalName(),
				LinkedList.class.getCanonicalName(), HashSet.class.getCanonicalName(),
				HashMap.class.getCanonicalName(), LinkedHashSet.class.getCanonicalName(),
				LinkedHashMap.class.getCanonicalName(), TreeSet.class.getCanonicalName(),
				TreeMap.class.getCanonicalName(),
				IdentityHashMap.class.getCanonicalName(),
				EnumMap.class.getCanonicalName(),
				CopyOnWriteArrayList.class.getCanonicalName(),
				CopyOnWriteArraySet.class.getCanonicalName()));
	}

	private static void addToDefaultCollImpl(Class<?> interfaceClass,
			Class<?> implClass) {
		defaultCollImpl.put(Util
				.erasure(Util.elementUtils
						.getTypeElement(interfaceClass.getCanonicalName()).asType())
				.toString(), implClass);
	}

	private Class<?> getDefaultImplementation() {
		return defaultCollImpl.get(Util.erasure(this.getType()).toString());
	}

	private boolean permitNullElements() {
		return permitNullCollections.contains(this.getImplementationType().toString());
	}
}
