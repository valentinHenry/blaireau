# Blaireau

**Blaireau** is a codec derivation helper and a type safe SQL DSL for the [Skunk library](https://github.com/tpolecat/skunk)

:warning: **Blaireau** is not an official Skunk library.

**Blaireau** is a personal project in addition to being a work in progress, **hence the lack of documentation (and code).** 

## Quickstart with sbt
If you want to test / have fun with it, no artefacts is published at the moment therefore you must publish it locally
```shell
$> git clone git@github.com:valentinHenry/blaireau.git
$> cd blaireau/
$> sbt publishLocal
```

In your `build.sbt` add the following line:
```scala
// For automatic and semi-automatic skunk-codec derivation
libraryDependencies += "fr.valentinhenry" %% "blaireau-derivation-codec" % Version
// For the SQL DSL:
libraryDependencies += "fr.valentinhenry" %% "blaireau-dsl" % Version
```

## Codec derivation
**Blaireau** provides a generic codec derivation based on shapeless. It derives nested types contained in the codec class.

### Example

```scala
import blaireau.generic._
import skunk._

case class Address(street: String, forest: String)

case class Blaireau(name: String, age: Int, address: Address)

val blaireauCodec: Codec[Blaireau] = implicitly
// eqv to Codec[text ~ int4 ~ (text ~ text)]

val insert: Command[Blaireau] =
  sql"""
  INSERT INTO "blaireaux"
  VALUES($blaireauCodec)
""".command
```

:warning: By default, every `String` typed fields will use Skunk's`text` codec in automatic derivation. If another type is
required, it is preferable to make this codec implicit.

```scala
import blaireau.generic._
import skunk._

case class Address(street: String, forest: String)
implicit val addressCodec: Codec[Address] = (varchar ~ varchar(32)).gimap

case class Blaireau(name: String, age: Int, address: Address)

val blaireauCodec: Codec[Blaireau] = implicitly
// eqv to Codec[text ~ int4 ~ (varchar ~ varchar(32))]

val insert: Command[Blaireau] =
  sql"""
  INSERT INTO "blaireaux"
  VALUES($blaireauCodec)
""".command
```

## SQL DSL
WIP