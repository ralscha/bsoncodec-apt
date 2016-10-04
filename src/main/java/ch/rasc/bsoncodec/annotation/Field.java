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
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Marks a instance field to be part of the {@link Codec}. (Optional)
 * <p>
 * By default the annotation processor takes every instance field into account that is not
 * annotated with {@link Transient} and has a corresponding get and set method. The
 * {@link Field} annotation is only needed when the default behavior should be overridden.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Field {

	/**
	 * The key to be used to store the field inside the document.
	 * <p>
	 * By default the name of the field is used.
	 */
	String value() default "";

	/**
	 * The order in which the field should be stored.
	 * <p>
	 * By default the fields are stored in the order they appear in the class. Except the
	 * id field which is always stored first.
	 */
	int order() default Integer.MAX_VALUE;

	/**
	 * A custom {@link Codec} implementation that handles encoding and decoding of the
	 * annotated field
	 */
	Class<? extends Codec<?>> codec() default NullCodec.class;

	/**
	 * Denotes an array as a fixed length array. Such an array is null or contains always
	 * the specified number of elements. This is a micro-optimisation hint for the decoder
	 * code generation.
	 * <p>
	 * This attribute is ignored for non-array fields or for fields that have a custom
	 * codec specified.
	 * <p>
	 * i.e. <code>
	 * &#64;Field(fixedArray=2)
	 * private double[] lngLat;
	 * </code>
	 */
	int fixedArray() default 0;

	/**
	 * Specifies the {@link Collection} implementation to be used in the decoding phase.
	 * <p>
	 * There is no need to specify this attribute when the default mapping is sufficient
	 * or when the type of the field is already a {@link Collection} implementation
	 * <p>
	 * Default mappings
	 * <p>
	 * <table>
	 * <tr>
	 * <td><strong>Interface</strong></td>
	 * <td><strong>Implementation</strong></td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.Collection}</td>
	 * <td>{@link java.util.ArrayList}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.List}</td>
	 * <td>{@link java.util.ArrayList}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.Set}</td>
	 * <td>{@link java.util.LinkedHashSet}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.SortedSet}</td>
	 * <td>{@link java.util.TreeSet}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.NavigableSet}</td>
	 * <td>{@link java.util.TreeSet}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.concurrent.BlockingDeque}</td>
	 * <td>{@link java.util.concurrent.LinkedBlockingDeque}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.concurrent.BlockingQueue}</td>
	 * <td>{@link java.util.concurrent.LinkedBlockingQueue}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.Deque}</td>
	 * <td>{@link java.util.concurrent.LinkedBlockingDeque}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.Queue}</td>
	 * <td>{@link java.util.concurrent.LinkedBlockingQueue}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link java.util.concurrent.TransferQueue}</td>
	 * <td>{@link java.util.concurrent.LinkedTransferQueue}</td>
	 * </tr>
	 * </table>
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends Collection> collectionImplementationClass() default NullCollection.class;

	public static class NullCodec implements Codec<Object> {

		@Override
		public void encode(BsonWriter writer, Object value,
				EncoderContext encoderContext) {
			// nothing here
		}

		@Override
		public Class<Object> getEncoderClass() {
			return null;
		}

		@Override
		public Object decode(BsonReader reader, DecoderContext decoderContext) {
			return null;
		}

	}

	public static class NullCollection extends AbstractCollection<Object> {

		@Override
		public Iterator<Object> iterator() {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}

	}

}