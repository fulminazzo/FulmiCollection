# FulmiCollection
FulmiCollection is a library project created by Fulminazzo that contains various utility methods and classes used across the projects.

## Exceptions

| Contents                                                                                                                 | Description                                                                                                              |
|--------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| [GeneralCannotBeNullException](src/main/java/it/fulminazzo/fulmicollection/exceptions/GeneralCannotBeNullException.java) | An exception with "%object% cannot be null." as message.                                                                 |
| [ClassCannotBeNullException](src/main/java/it/fulminazzo/fulmicollection/exceptions/ClassCannotBeNullException.java)     | [GeneralCannotBeNullException](src/main/java/it/fulminazzo/fulmicollection/exceptions/GeneralCannotBeNullException.java) |
| [NameCannotBeNullException](src/main/java/it/fulminazzo/fulmicollection/exceptions/NameCannotBeNullException.java)       | [GeneralCannotBeNullException](src/main/java/it/fulminazzo/fulmicollection/exceptions/GeneralCannotBeNullException.java) |

## Objects

| Contents                                                                        | Description                                            |
|---------------------------------------------------------------------------------|--------------------------------------------------------|
| [Printable](src/main/java/it/fulminazzo/fulmicollection/objects/Printable.java) | An object that allows printing of its inheritor fields |

## Functions

| Contents                                                                                                               | Description                                |
|------------------------------------------------------------------------------------------------------------------------|--------------------------------------------|
| [ConsumerException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/ConsumerException.java)           | `(f) -> void throws Exception`             |
| [FunctionException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/FunctionException.java)           | `(f) -> r throws Exception`                |
| [BiConsumerException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/BiConsumerException.java)       | `(f, s) -> void throws Exception`          |
| [BiFunctionException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/BiFunctionException.java)       | `(f, s) -> r throws Exception`             |
| [TriConsumer](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/TriConsumer.java)                       | `(f, s, t) -> void`                        |
| [TriConsumerException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/TriConsumerException.java)     | `(f, s, t) -> void throws Exception`       |
| [TriFunction](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/TriFunction.java)                       | `(f, s, t) -> r`                           |
| [TriFunctionException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/TriFunctionException.java)     | `(f, s, t) -> r throws Exception`          |
| [TetraConsumer](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/TetraConsumer.java)                   | `(f, s, t, q) -> void`                     |
| [TetraConsumerException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/TetraConsumerException.java) | `(f, s, t, q) -> void throws Exception`    |
| [TetraFunction](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/TetraFunction.java)                   | `(f, s, t, q) -> r`                        |
| [TetraFunctionException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/TetraFunctionException.java) | `(f, s, t, q) -> r throws Exception`       |
| [PentaConsumer](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/PentaConsumer.java)                   | `(f, s, t, q, p) -> void`                  |
| [PentaConsumerException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/PentaConsumerException.java) | `(f, s, t, q, p) -> void throws Exception` |
| [PentaFunction](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/PentaFunction.java)                   | `(f, s, t, q, p) -> r`                     |
| [PentaFunctionException](src/main/java/it/fulminazzo/fulmicollection/interfaces/functions/PentaFunctionException.java) | `(f, s, t, q, p) -> r throws Exception`    |