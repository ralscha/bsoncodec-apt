[![Build Status](https://travis-ci.org/ralscha/bsoncodec-apt.svg?branch=master)](https://travis-ci.org/ralscha/bsoncodec-apt)

## Overview
This project implements a Java 8 Annotation Processor (APT) that
creates [org.bson.codecs.Codec](http://api.mongodb.org/java/3.2/org/bson/codecs/Codec.html) 
implementations for simple Java objects. 

The annotation processor takes classes like this (see the full code [here](https://github.com/ralscha/bsoncodec-apt/blob/master/src/test/resources/Simple.java))
```
@BsonDocument
public class Simple {
	private ObjectId id;
	private String name;
    // get and set methods
}
```

and generates a corresponding ```Codec<Simple>``` implementation (see the full code [here](https://github.com/ralscha/bsoncodec-apt/blob/master/src/test/resources/SimpleCodec.java))

```
public final class SimpleCodec implements Codec<Simple> {
  private final ObjectIdGenerator objectIdGenerator;

  public SimpleCodec(final ObjectIdGenerator objectIdGenerator) {
    this.objectIdGenerator = Assertions.notNull("objectIdGenerator", objectIdGenerator);
  }

  @Override
  public void encode(BsonWriter writer, Simple value, EncoderContext encoderContext) {
      //.. encode code...
  }

  @Override
  public Simple decode(BsonReader reader, DecoderContext decoderContext) {
      //.. codec code ...
  }

  @Override
  public Class<Simple> getEncoderClass() {
    return Simple.class;
  }
}
```
and it creates an additional implementation of the [org.bson.codecs.configuration.CodecProvier](http://api.mongodb.org/java/3.2/org/bson/codecs/configuration/CodecProvider.html) interface (see code [here](https://github.com/ralscha/bsoncodec-apt/blob/master/src/test/resources/PojoCodecProvider.java)).


## Maven

To enable the annotation processor you need to add this dependency with scope provided or as optional.

```
	<dependency>
		<groupId>ch.rasc</groupId>
		<artifactId>bsoncodec-apt</artifactId>
		<version>1.0.1</version>
		<scope>provided</scope>
		<!-- or as optional -->
		<!-- <optional>true</optional> -->
	</dependency>
```

The generated classes have a compile and runtime dependency on the BSON library.
```
	<dependency>
		<groupId>org.mongodb</groupId>
		<artifactId>bson</artifactId>
		<version>3.2.1</version>
	</dependency>
```

When the project already depends on the mongodb-driver there is no need to add the BSON library explicitly.
 ```
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver</artifactId>
        <version>3.2.1</version>
    </dependency>
```


Released versions are available from the Maven Central Repository.
To use SNAPSHOT releases you need to add the Sonatype Maven Repository to the pom.xml
```
	<repositories>
		<repository>
			<id>sonatype</id>
			<name>sonatype</name>
			<url>https://oss.sonatype.org/content/groups/public</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
			<releases>
				<enabled>true</enabled>
			</releases>
		</repository>
	</repositories>
```

On the immutable.io page you find descriptions on how to enable APT in Eclipse and IntelliJ.   
https://immutables.github.io/apt.html

## MongoDB

To utilize the generated codecs for storing and reading Java objects into and from MongoDB, the application
needs to register these classes in the MongoDB Driver. 
The Codecs can be registered in the MongoDatabase or in the MongoCollection.

The following example takes the generated PojoCodecProvider class and registers it together with the default
codec registry in the MongoDatabase.

```
   CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                                 MongoClient.getDefaultCodecRegistry(),
				                 CodecRegistries.fromProviders(new PojoCodecProvider()));

	MongoDatabase db = getMongoClient().getDatabase("my_database")
	                                   .withCodecRegistry(codecRegistry);
```

After this registration the application can use the POJO for storing and reading data.

```
   Simple simple = new Simple();
   simple.setName("the name");

   MongoCollection<Simple> collection = db.getCollection("simples", Simple.class);
   collection.insertOne(simple);

   Simple anotherSimple = coll.find().first();   
```

See more informations about Codecs in the official documentation:    
http://mongodb.github.io/mongo-java-driver/3.2/bson/codecs/


## Annotations
This library provides the following annotations:

#### @BsonDocument
The annotation processor only creates Codec implementation for classes that are annotated with this annotation.

Attribute | Description
--- | --- 
codecProviderClassName | Fully qualified classname of the CodecProvider the processor should create. (Default: ```"PojoCodecProvider"```)
storeNullValues | If false ignores fields that have a null value. If true creates a null entry in the BSON document. (Default: ```false```)
storeEmptyCollections | If false ignores collections that are empty. If true adds an empty array to the BSON document. (Default: ```false```)
ignoreUnknown | If false throws an exception when the decoder encounters a field in mongodb that does not exists in the pojo. true silently ignores these fields. (Default: ```true```)

#### @Field
Overrides the custom field handling behavior. This is an optional annotation.
All instance fields that have a corresponding get-/set method and are not annotated with ```@Transient```
are by default included in the final Codec code. 

Attribute | Description
--- | ---
value | Name of the key in the BSON document. (Default: Name of the Java field)
order | Order in which the entry should appear in the BSON document. (Default: In order they are listed in the Java class, except the primary key field which is always ordered first)
codec | A custom Codec implementation that should be used for this field, instead of the default code.
collectionImplementationClass | For collections. A hint for the decode phase which Collection implementation should be instantiated.


#### @Id
Marks a field as the primary key of the document. This is an optional annotation. 
The annotation processor by default tries to find the primary key by type (```ObjectId``` or ```UUID```) or by name ('id' or '_id').

Attribute | Description
--- | ---
generator | An implementation of the IdGenerator interface. For generating the primary key when no value is provided.
conversion | For primary keys that use the type String in the POJO. Converts the String into an ObjectId or UUID (encode) and back to a String (decode). Possible values are ```BASE64_OBJECTID``` (Base 64 String to ObjectId, ```HEX_OBJECTID``` (Hex String to ObjectId) and ```HEX_UUID``` (Hex String to UUID).
codec | A custom codec implementation. Overrides the default code and ignores the generator and conversion attribute.

#### @Transient
Marks a field as transient. The annotation processor ignores this field and does not 
create encode/decode code.


## Links
If this project does not work for you, here a list of other projects that may work better for your use case.
   * http://immutables.github.io/mongo.html
   * https://github.com/guicamest/bsoneer
   * https://github.com/caeus/vertigo

#### Other interesting links
   * [MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/)
   * [BSON Specification](http://bsonspec.org/)
   * [MongoDB](https://www.mongodb.org/)

## Changelog

### 1.0.1 - January 30, 2016
  * Resolves [#4](https://github.com/ralscha/bsoncodec-apt/issues/4)
  * Resolves [#7](https://github.com/ralscha/bsoncodec-apt/issues/7)
  * Resolves [#8](https://github.com/ralscha/bsoncodec-apt/issues/8)

### 1.0.0 - November 22, 2015
  * Initial release
