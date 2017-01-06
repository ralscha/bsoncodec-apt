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
package ch.rasc.bsoncodec;

import org.junit.Test;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

/**
 * Not a really useful test. Just a helper for debugging
 */
public class AptTest {

	@Test
	public void startApt() {
		Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
				.that(JavaFileObjects.forResource("Simple.java"))
				.processedWith(new CodecAnnotationProcessor()).compilesWithoutError()
				.and().generatesSources(JavaFileObjects.forResource("SimpleCodec.java"),
						JavaFileObjects.forResource("PojoCodecProvider.java"));
	}

}
