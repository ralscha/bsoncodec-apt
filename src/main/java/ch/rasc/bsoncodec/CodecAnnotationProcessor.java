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
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import ch.rasc.bsoncodec.annotation.BsonDocument;
import ch.rasc.bsoncodec.model.CodecInfo;
import ch.rasc.bsoncodec.model.ImmutableCodecInfo;

@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({ "generateCodecProvider" })
public class CodecAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		String option = this.processingEnv.getOptions().get("generateCodecProvider");
		boolean generateCodecProvider = true;
		if (option != null && !option.trim().isEmpty()) {
			generateCodecProvider = Boolean.valueOf(option);
		}

		if (roundEnv.processingOver() || annotations.size() == 0) {
			return false;
		}

		if (roundEnv.getRootElements() == null || roundEnv.getRootElements().isEmpty()) {
			this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
					"No sources to process");
			return false;
		}

		Util.elementUtils = this.processingEnv.getElementUtils();
		Util.typeUtils = this.processingEnv.getTypeUtils();
		Util.messager = this.processingEnv.getMessager();

		Map<String, List<CodecInfo>> codecInfosPerProvider = new HashMap<>();

		AnnotationHierarchyUtil hierarchyUtil = new AnnotationHierarchyUtil(
				this.processingEnv.getTypeUtils());
		Set<TypeElement> triggeringAnnotations = hierarchyUtil
				.filterTriggeringAnnotations(annotations,
						this.processingEnv.getElementUtils()
								.getTypeElement(BsonDocument.class.getCanonicalName()));

		for (TypeElement annotation : triggeringAnnotations) {
			Set<? extends Element> elements = roundEnv
					.getElementsAnnotatedWith(annotation).stream()
					.filter(el -> el.getKind() == ElementKind.CLASS
							&& !el.getModifiers().contains(Modifier.ABSTRACT))
					.collect(Collectors.toSet());
			for (Element element : elements) {
				try {
					TypeElement typeElement = (TypeElement) element;
					CodecCodeGenerator codeGen = new CodecCodeGenerator(typeElement);

					JavaFileObject jfo = this.processingEnv.getFiler()
							.createSourceFile(codeGen.getFullyQualifiedName());
					try (Writer writer = jfo.openWriter()) {
						codeGen.generate(writer);
					}

					String providerClassName = codeGen.getProviderClassName();
					codecInfosPerProvider
							.computeIfAbsent(providerClassName, key -> new ArrayList<>())
							.add(ImmutableCodecInfo.of(typeElement,
									ClassName.get(codeGen.getPackageName(),
											codeGen.getClassName()),
									codeGen.getInstanceFields().stream()
											.filter(i -> !i.isRegistryCodec()
													&& !i.isRegistry())
											.sorted().collect(Collectors.toSet()),
									codeGen.getInstanceFields().stream()
											.filter(i -> i.isRegistryCodec()
													|| i.isRegistry())
											.findFirst().isPresent()));
				}
				catch (Exception e) {
					this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							e.getMessage());
				}
			}
		}

		// Create Codec Providers
		if (generateCodecProvider) {
			try {
				for (Map.Entry<String, List<CodecInfo>> providerInfo : codecInfosPerProvider
						.entrySet()) {
					ProviderCodeGenerator codeGenerator = new ProviderCodeGenerator(
							providerInfo.getKey(), providerInfo.getValue());
					JavaFileObject jfo = this.processingEnv.getFiler()
							.createSourceFile(codeGenerator.getFullyQualifiedName());
					try (Writer writer = jfo.openWriter()) {
						codeGenerator.generate(writer);
					}
				}
			}
			catch (IOException e) {
				this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
						e.getMessage());
			}
		}

		return false;
	}

}
