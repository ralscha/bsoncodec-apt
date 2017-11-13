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
import java.lang.SuppressWarnings;
import org.bson.codecs.Codec;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public final class PojoCodecProvider implements CodecProvider {
  private final ObjectIdGenerator objectIdGenerator;

  public PojoCodecProvider() {
    this(new ObjectIdGenerator());
  }

  public PojoCodecProvider(final ObjectIdGenerator objectIdGenerator) {
    this.objectIdGenerator = objectIdGenerator;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
    if (clazz.equals(Simple.class)) {
      return (Codec<T>) new SimpleCodec(this.objectIdGenerator);
    }
    return null;
  }
}
