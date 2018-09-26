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
package ch.rasc.bsoncodec.test.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Test;

public class CodecProviderTest {

	@Test
	public void testOneProvider() {
		OneProvider provider = new OneProvider();
		CodecRegistry codecRegistry = CodecRegistries.fromProviders(provider);
		assertThat(codecRegistry.get(OnePojo.class)).isInstanceOf(OnePojoCodec.class);
		assertThat(codecRegistry.get(ThreePojo.class)).isInstanceOf(ThreePojoCodec.class);
	}

	@Test(expected = CodecConfigurationException.class)
	public void testOneProviderWithNotExistingCodec() {
		OneProvider provider = new OneProvider();
		CodecRegistry codecRegistry = CodecRegistries.fromProviders(provider);
		assertThat(codecRegistry.get(TwoPojo.class)).isNull();
	}

	@Test
	public void testTwoProvider() {
		TwoProvider provider = new TwoProvider();
		CodecRegistry codecRegistry = CodecRegistries.fromProviders(provider);
		assertThat(codecRegistry.get(TwoPojo.class)).isInstanceOf(TwoPojoCodec.class);
	}

}
