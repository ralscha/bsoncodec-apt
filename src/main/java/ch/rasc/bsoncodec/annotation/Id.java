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
package ch.rasc.bsoncodec.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

import org.bson.codecs.Codec;
import org.bson.codecs.IdGenerator;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.types.ObjectId;

import ch.rasc.bsoncodec.annotation.Field.NullCodec;

/**
 * Marks a field to be the primary key of this document. (Optional)
 * <p>
 * The annotation processor automatically marks fields as primary key when the type of the
 * field is {@link ObjectId} or {@link UUID} or when the name of field is <code>id</code>
 * or <code>_id</code>. No need to add this annotation when the defaults are used.
 * <p>
 * A field annotated with {@link Id} has precedence over the automatic id detection.
 * <p>
 * The primary key will always be stored with the name '_id' in the document.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Id {

	/**
	 * An implementation of the {@link IdGenerator} interface. Used during the encode
	 * phase. When the primary key field is null, the {@link Codec} calls the generator
	 * and sets the generated value to the field.
	 * <p>
	 * The annotation processor automatically uses an {@link ObjectIdGenerator} for
	 * primary keys of type {@link ObjectId} when the {@link #generator()} attribute is
	 * not set.
	 */
	Class<? extends IdGenerator> generator() default NullIdGenerator.class;

	/**
	 * Specifies a conversion between String and {@link ObjectId} or {@link UUID}. When
	 * this attribute is specified the string value will be converted to {@link ObjectId}
	 * or {@link UUID} before creating the BSON and converted back to a String during the
	 * decode phase.
	 * <p>
	 * This attribute is valid for fields with type {@link String}.
	 * <p>
	 * <table>
	 * <tr>
	 * <td><strong>Conversion</strong></td>
	 * <td><strong>Java</strong></td>
	 * <td><strong>BSON Document</strong></td>
	 * </tr>
	 * <tr>
	 * <td>{@link IdConversion#BASE64_OBJECTID }</td>
	 * <td>Base64 String</td>
	 * <td>{@link ObjectId}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link IdConversion#HEX_OBJECTID }</td>
	 * <td>Hex String</td>
	 * <td>{@link ObjectId}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link IdConversion#BASE64_UUID }</td>
	 * <td>Base64 String</td>
	 * <td>{@link UUID}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link IdConversion#HEX_UUID }</td>
	 * <td>Hex String</td>
	 * <td>{@link UUID}</td>
	 * </tr>
	 * </table>
	 */
	IdConversion conversion() default IdConversion.NULL;

	/**
	 * A custom {@link Codec} implementation that handles encoding and decoding of the
	 * annotated field.
	 * <p>
	 * When this attribute is specified the annotation processor ignores the
	 * {@link #conversion()} and {@link #generator()} attributes.
	 */
	Class<? extends Codec<?>> codec() default NullCodec.class;

	public static enum IdConversion {
		BASE64_OBJECTID, HEX_OBJECTID, BASE64_UUID, HEX_UUID, NULL
	}

	public static class NullIdGenerator implements IdGenerator {

		@Override
		public Object generate() {
			return null;
		}
	}
}
