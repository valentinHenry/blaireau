# Blaireau

**Blaireau** is a codec derivation helper and a SQL DSL for the [Skunk library](https://github.com/tpolecat/skunk)

:warning: **Blaireau** is not an official Skunk library.

**Blaireau** is a personal project in addition to being a work in progress, **hence the lack of documentation (and code).** 

:warning" I (Valentin) am not even sure this really works (will test it later)... 

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
**Blaireau** provides two ways of deriving codecs: automatic and semi-automatic using magnolia.

### Automatic
```scala
import blaireau.generic.codec.auto._
import skunk._

case class Address(street: String, forest: String)
case class Blaireau(name: String, age: Int, address: Address)

//val blaireauCodec: Codec[Blaireau] = implicitly
// eqv to Codec[text ~ int4 ~ (text ~ text)]

val insert: Command[Blaireau] = sql"""
  INSERT INTO "blaireaux"
  VALUES(${codec[Blaireau]})
""".command
```
:warning: By default, every `String` typed fields will use Skunk's`text` codec in automatic derivation. If another type is
required, it is preferable to use semi-automatic codec derivation.
### Semi-automatic

```scala
import skunk._
import blaireau.generic.codec.semiauto._
import blaireau.generic.codec.instances.all._

case class Address(street: String, forest: String)

case class Blaireau(name: String, age: Int, address: Address)

implicit val addressCodec: Codec[Address] = (varchar ~ varchar(16)).gimap[Address]
val blaireauCodec: Codec[Blaireau] = deriveCodec[Blaireau]
// eqv to Codec[text ~ int4 ~ (varchar ~ varchar(16))]

val insert: Command[Blaireau] =
  sql"""
  INSERT INTO "blaireaux"
  VALUES($blaireauCodec)
""".command
```

## SQL DSL
WIP