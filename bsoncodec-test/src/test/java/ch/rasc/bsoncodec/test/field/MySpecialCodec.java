/**
 * Copyright 2015-2015 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.rasc.bsoncodec.test.field;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class MySpecialCodec implements Codec<String> {

	@Override
	public void encode(BsonWriter writer, String value, EncoderContext encoderContext) {
		writer.writeStartArray();

		for (String splittedValues : value.split(";")) {
			writer.writeString(splittedValues);
		}

		writer.writeEndArray();
	}

	@Override
	public String decode(BsonReader reader, DecoderContext decoderContext) {
		List<String> strings = new ArrayList<>();
		reader.readStartArray();
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
			strings.add(reader.readString());
		}
		reader.readEndArray();
		return strings.stream().collect(Collectors.joining(";"));
	}

	@Override
	public Class<String> getEncoderClass() {
		return null;
	}

}
