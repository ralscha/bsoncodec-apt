/*
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
package ch.rasc.bsoncodec;

import java.util.Collection;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class Util {

	public static Elements elementUtils;

	public static Types typeUtils;

	public static Messager messager;

	public static boolean isEnum(TypeMirror typeMirror) {
		TypeMirror enumType = typeUtils.erasure(
				elementUtils.getTypeElement(Enum.class.getCanonicalName()).asType());
		return typeUtils.isAssignable(typeUtils.erasure(typeMirror), enumType);
	}

	public static boolean isCollection(TypeMirror typeMirror) {
		TypeMirror collectionType = typeUtils.erasure(elementUtils
				.getTypeElement(Collection.class.getCanonicalName()).asType());
		return typeUtils.isAssignable(typeUtils.erasure(typeMirror), collectionType);
	}

	public static boolean isMap(TypeMirror typeMirror) {
		TypeMirror mapType = typeUtils.erasure(
				elementUtils.getTypeElement(Map.class.getCanonicalName()).asType());
		return typeUtils.isAssignable(typeUtils.erasure(typeMirror), mapType);
	}

	public static boolean isSameType(TypeMirror typeMirror, Class<?> clazz) {
		return typeUtils.isSameType(erasure(typeMirror),
				erasure(elementUtils.getTypeElement(clazz.getCanonicalName()).asType()));
	}

	public static boolean isByte(TypeMirror typeMirror) {
		return typeMirror.getKind() == TypeKind.BYTE
				|| isSameType(typeMirror, Byte.class);
	}

	public static boolean isArray(TypeMirror type) {
		return type.getKind() == TypeKind.ARRAY;
	}

	public static String uncapitalize(String string) {
		char c[] = string.toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		return new String(c);
	}

	public static String className(String fullyQualifiedName) {
		int pos = fullyQualifiedName.lastIndexOf(".");
		if (pos != -1) {
			return fullyQualifiedName.substring(pos + 1);
		}
		return fullyQualifiedName;
	}

	public static String varName(String fullyQualifiedName) {
		return uncapitalize(className(fullyQualifiedName));
	}

	public static boolean isAnyType(TypeMirror typeMirror, Class<?>... clazzes) {
		if (clazzes == null) {
			return false;
		}

		for (Class<?> clazz : clazzes) {
			if (isSameType(typeMirror, clazz)) {
				return true;
			}
		}
		return false;
	}

	public static TypeMirror erasure(TypeMirror type) {
		return typeUtils.erasure(type);
	}
}
