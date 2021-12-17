# Blaireau

**Blaireau** is a codec derivation helper and a simplistic type safe SQL DSL for the [Skunk library](https://github.com/tpolecat/skunk)

:warning: **Blaireau** is a personal project in addition to being a work in progress, **hence the lack of documentation, code and functionalities.** 
## Table of Content
- [Quickstart with sbt](#quickstart-with-sbt)
- [Codec derivation](#codec-derivation)
    * [Example](#example)
- [SQL DSL](#sql-dsl)
    * [Metas](#metas)
    * [Table](#table)
    * [Select](#select)
        + [Select DSL](#select-dsl)
        + [Where DSL](#where-dsl)
        + [Querying](#querying)
        + [Example](#example-1)
    * [Update](#update)
        + [Update DSL](#update-dsl)
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
:warning: This is a work in progress, the DSL is very likely to change

To use the DSL, you must import the package `blaireau.dsl` like below :
```scala
import blaireau.dsl._
```

### Metas
A meta is a representation of the class in the database. It is derived using Shapeless.

At the moment, only simple types are supported.

A `MetaS[T]` is a meta instance of a type `T`. You can create any `MetaS` you want using the `Meta.of(...)` function.
It requires only a `skunk.Codec[T]`. 

:warning: It sould only be types, not case class or it will mess with the derivation!

Metas must be implicitly available in the scope where the `Table` and `Meta` generation take places.

### Table
Blaireau maps a case class to a database object. This mapping is done through a Meta-object derivation.

To create a table, you just have to use the `apply` method of the `Table` object like below:
```scala
import blaireau.dsl._

import java.util.UUID

final case class Address(street: String, postalCode: String, city: String, country: String)
final case class User(id: UUID, email: String, firstName: String, lastName: String, age: Int, address: Address)

val users = Table[User]("users")
```

We will be using the table above for all following examples.

### Select
Only simple select queries can be generated using **Blaireau**, for more advance ones, it is better to use **Skunk** sql interpolator.

#### Select DSL

There are options regarding the selection of fields:
- Whole object selection
- Specific fields selection

For the whole object selection, the `where` function should not have parameters
```scala
val users: Query[Void, User] = users.select...
```
For a more specific selection of fields, it can be done using the `~` operator like in the **Skunk** library.
```scala
import skunk.~
val namesAndAge: Query[Void, String ~ Int] = users.select(e => e.firstName ~ e.age)...
```

Specific selection takes into account the embedded types
```scala
import skunk.~
val namesAndAddress: Query[Void, String ~ Address] = users.select(e => e.firstName ~ e.address)...
// Instead of Query[Void, String ~ String ~ String ~ String ~ String]
```

Embedded fields selection is also allowed:
```scala
val cities: Query[Void, String] = users.select(_.address.city)
```

#### Where DSL
A Where clause can be created using boolean operators:

|  Type Constraint  | Scala |     Postgresql    | Blaireau |
|:-----------------:|:-----:|:-----------------:|:--------:|
|        Any        |   ==  |         =         |    :=    |
|      Numeric      |   -=  | field = field - _ |    -=    |
|      Numeric      |   +=  | field = field + _ |    +=    |
|        Any        |   ==  |         =         |    ===   |
|        Any        |   !=  |         <>        |    <>    |
| Numeric \| String |   <   |         <         |     <    |
| Numeric \| String |   <=  |         <=        |    <=    |
| Numeric \| String |   >   |         >         |     >    |
| Numeric \| String |   >=  |         >=        |    >=    |
|       String      |       |        like       |   like   |

Those can be combined with operators:

|  Type Constraint  | Scala |     Postgresql    | Blaireau |
|:-----------------:|:-----:|:-----------------:|:--------:|
|      Boolean      |   &&  |        AND        |    &&    |
|      Boolean      |  \|\| |        OR         |   \|\|   |

The classes fields are accessed using their scala names (regardless of the format used in the db).

The `Where` clause can be composed using `whereAnd(...)` and `whereOr(...)` functions after the first `where(...)`.

Examples:
```scala
val findById = users.select.where(_.id === id)...

val allLuciesOver30LivingInParis = users.select.where(e => e.address.city === "Paris" && e.age >= 30)
```

#### Querying

Once the select query fits your needs, you can chose the function which fits your needs the best.

The `toQuery` function compiles the query into a **Skunk** `Query`.
```scala
import java.util.UUID
val findById: Query[UUID, User] = users.select.where(_.id === UUID.randomUUID())
```

The `queryIn` returns the input parameter given to the query builder
```scala
val findChristopheOrRetired = users.select(_.id).where(e => e.firstName === "Christophe" || e.age >= 60)
// val findCORQuery: Query[String ~ Int, UUID] = findChristopheOrRetired.toQuery
val findCORQueryInputParams: String ~ Int = findChristopheOrRetired.queryIn
// Eq to: ("Christophe", 60)
```

Or you can use wrappers on **Skunk** which returns a F[...].

#### Example
```scala
import cats.effect.MonadCancelThrow
import blaireau.dsl._
import skunk.Session

final class UsersSql[F[_]: MonadCancelThrow](s: Session[F]){
  val users = Table[User]("users")
  
  def findById(id: UUID): F[User] =
    users.select.where(_.id === id).unique(s)
  // SELECT firstName, lastName, ..., city, country FROM users WHERE id = $1 
  // $1 = id
    
  def findByCountry(country: String): F[fs2.Stream[F, User]] =
    users.select.where(_.address.country === country).stream(s)
    
  def findByEmail(email: String): F[Option[User]] =
    users.select.where(_.email === email).option(s)
    
  def findAllEmailsOfPeopleOverTheAgeOF(age: Int): F[fs2.Stream[F, String]] =
    users.select(_.email).where(_.age > age).stream(s)
  // SELECT email FROM users WHERE age > $1 
  // $1 = age
}
```

### Update
WIP
#### Update DSL
An Update clause can be created using the following assignment operators:

|        Type       | Scala |     Postgresql    | Blaireau |
|:-----------------:|:-----:|:-----------------:|:--------:|
|        Any        |   ==  |         =         |    :=    |
|      Numeric      |   -=  | field = field - _ |    -=    |
|      Numeric      |   +=  | field = field + _ |    +=    |
