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
package ch.rasc.bsoncodec.test.id;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class CustomIdCodec implements Codec<Long> {

	@Override
	public void encode(BsonWriter writer, Long value, EncoderContext encoderContext) {
		writer.writeStartDocument();
		writer.writeInt64("key", value);
		writer.writeEndDocument();
	}

	@Override
	public Long decode(BsonReader reader, DecoderContext decoderContext) {
		reader.readStartDocument();
		long key = reader.readInt64("key");
		reader.readEndDocument();

		return key;
	}

	@Override
	public Class<Long> getEncoderClass() {
		return null;
	}

}
