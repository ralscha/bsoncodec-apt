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
package ch.rasc.bsoncodec.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.jupiter.api.Test;

import ch.rasc.bsoncodec.test.pojo.AtomicBooleanPojo;
import ch.rasc.bsoncodec.test.pojo.AtomicBooleanPojoCodec;
import ch.rasc.bsoncodec.test.pojo.AtomicIntegerPojo;
import ch.rasc.bsoncodec.test.pojo.AtomicIntegerPojoCodec;
import ch.rasc.bsoncodec.test.pojo.AtomicLongPojo;
import ch.rasc.bsoncodec.test.pojo.AtomicLongPojoCodec;
import ch.rasc.bsoncodec.test.pojo.BooleanPojo;
import ch.rasc.bsoncodec.test.pojo.BooleanPojoCodec;
import ch.rasc.bsoncodec.test.pojo.BytePojo;
import ch.rasc.bsoncodec.test.pojo.BytePojoCodec;
import ch.rasc.bsoncodec.test.pojo.CharacterPojo;
import ch.rasc.bsoncodec.test.pojo.CharacterPojoCodec;
import ch.rasc.bsoncodec.test.pojo.DatePojo;
import ch.rasc.bsoncodec.test.pojo.DatePojoCodec;
import ch.rasc.bsoncodec.test.pojo.Decimal128Pojo;
import ch.rasc.bsoncodec.test.pojo.Decimal128PojoCodec;
import ch.rasc.bsoncodec.test.pojo.DoublePojo;
import ch.rasc.bsoncodec.test.pojo.DoublePojoCodec;
import ch.rasc.bsoncodec.test.pojo.EnumPojo;
import ch.rasc.bsoncodec.test.pojo.EnumPojoCodec;
import ch.rasc.bsoncodec.test.pojo.FloatPojo;
import ch.rasc.bsoncodec.test.pojo.FloatPojoCodec;
import ch.rasc.bsoncodec.test.pojo.InstantPojo;
import ch.rasc.bsoncodec.test.pojo.InstantPojoCodec;
import ch.rasc.bsoncodec.test.pojo.IntegerPojo;
import ch.rasc.bsoncodec.test.pojo.IntegerPojoCodec;
import ch.rasc.bsoncodec.test.pojo.LongPojo;
import ch.rasc.bsoncodec.test.pojo.LongPojoCodec;
import ch.rasc.bsoncodec.test.pojo.MonthPojo;
import ch.rasc.bsoncodec.test.pojo.MonthPojoCodec;
import ch.rasc.bsoncodec.test.pojo.PojoCodecProvider;
import ch.rasc.bsoncodec.test.pojo.ShortPojo;
import ch.rasc.bsoncodec.test.pojo.ShortPojoCodec;
import ch.rasc.bsoncodec.test.pojo.StringPojo;
import ch.rasc.bsoncodec.test.pojo.StringPojoCodec;
import ch.rasc.bsoncodec.test.pojo.YearPojo;
import ch.rasc.bsoncodec.test.pojo.YearPojoCodec;
import ch.rasc.bsoncodec.time.InstantInt64Codec;
import ch.rasc.bsoncodec.time.MonthInt32Codec;
import ch.rasc.bsoncodec.time.YearInt32Codec;

public class CodecProviderTest {

	@Test
	public void testContainsAllCodecs() {
		PojoCodecProvider provider = new PojoCodecProvider();
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				CodecRegistries.fromProviders(provider),
				CodecRegistries.fromCodecs(new YearInt32Codec(), new MonthInt32Codec(),
						new InstantInt64Codec()));

		assertThat(provider.get(BytePojo.class, codecRegistry))
				.isInstanceOf(BytePojoCodec.class);
		assertThat(provider.get(MonthPojo.class, codecRegistry))
				.isInstanceOf(MonthPojoCodec.class);
		assertThat(provider.get(BooleanPojo.class, codecRegistry))
				.isInstanceOf(BooleanPojoCodec.class);
		assertThat(provider.get(EnumPojo.class, codecRegistry))
				.isInstanceOf(EnumPojoCodec.class);
		assertThat(provider.get(AtomicLongPojo.class, codecRegistry))
				.isInstanceOf(AtomicLongPojoCodec.class);
		assertThat(provider.get(ShortPojo.class, codecRegistry))
				.isInstanceOf(ShortPojoCodec.class);
		assertThat(provider.get(StringPojo.class, codecRegistry))
				.isInstanceOf(StringPojoCodec.class);
		assertThat(provider.get(FloatPojo.class, codecRegistry))
				.isInstanceOf(FloatPojoCodec.class);
		assertThat(provider.get(AtomicIntegerPojo.class, codecRegistry))
				.isInstanceOf(AtomicIntegerPojoCodec.class);
		assertThat(provider.get(DatePojo.class, codecRegistry))
				.isInstanceOf(DatePojoCodec.class);
		assertThat(provider.get(IntegerPojo.class, codecRegistry))
				.isInstanceOf(IntegerPojoCodec.class);
		assertThat(provider.get(YearPojo.class, codecRegistry))
				.isInstanceOf(YearPojoCodec.class);
		assertThat(provider.get(Decimal128Pojo.class, codecRegistry))
				.isInstanceOf(Decimal128PojoCodec.class);
		assertThat(provider.get(DoublePojo.class, codecRegistry))
				.isInstanceOf(DoublePojoCodec.class);
		assertThat(provider.get(LongPojo.class, codecRegistry))
				.isInstanceOf(LongPojoCodec.class);
		assertThat(provider.get(CharacterPojo.class, codecRegistry))
				.isInstanceOf(CharacterPojoCodec.class);
		assertThat(provider.get(AtomicBooleanPojo.class, codecRegistry))
				.isInstanceOf(AtomicBooleanPojoCodec.class);
		assertThat(provider.get(InstantPojo.class, codecRegistry))
				.isInstanceOf(InstantPojoCodec.class);

	}

	@Test
	public void testReturnsNullForWrongCodec() {
		PojoCodecProvider provider = new PojoCodecProvider();
		CodecRegistry codecRegistry = CodecRegistries.fromProviders(provider);

		assertThat(provider.get(Object.class, codecRegistry)).isNull();
	}

}
