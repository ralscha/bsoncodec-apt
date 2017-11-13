/**
 * Copyright 2015-2017 the original author or authors.
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
import java.lang.Class;
import java.lang.Override;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.types.ObjectId;

public final class SimpleCodec implements Codec<Simple> {
  private final ObjectIdGenerator objectIdGenerator;

  public SimpleCodec(final ObjectIdGenerator objectIdGenerator) {
    this.objectIdGenerator = Assertions.notNull("objectIdGenerator", objectIdGenerator);
  }

  @Override
  public void encode(BsonWriter writer, Simple value, EncoderContext encoderContext) {
    writer.writeStartDocument();
    if (value.getId() == null) {
      value.setId((ObjectId)this.objectIdGenerator.generate());
    }
    writer.writeName("_id");
    writer.writeObjectId(value.getId());
    if (value.getName() != null) {
      writer.writeName("name");
      writer.writeString(value.getName());
    }
    writer.writeEndDocument();
  }

  @Override
  public Simple decode(BsonReader reader, DecoderContext decoderContext) {
    Simple value = new Simple();
    reader.readStartDocument();
    BsonType bsonType;
    while ((bsonType = reader.readBsonType()) != BsonType.END_OF_DOCUMENT) {
      String name = reader.readName();
      if (bsonType != BsonType.NULL) {
        switch (name) {
          case "_id": {
            value.setId(reader.readObjectId());
            break;
          }
          case "name": {
            value.setName(reader.readString());
            break;
          }
          default:
              reader.skipValue();
        }
      } else {
        reader.readNull();
      }
    }
    reader.readEndDocument();
    return value;
  }

  @Override
  public Class<Simple> getEncoderClass() {
    return Simple.class;
  }
}
