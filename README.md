# Blaireau

**Blaireau** is a codec derivation helper and a minimalistic type safe SQL DSL for the [Skunk library](https://github.com/tpolecat/skunk)

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
        + [Commands](#commands)
    * [Delete](#delete)
        + [Delete DSL](#delete-dsl)
        + [Commands](#commands-1)

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

TODO: override field names
TODO: change fields names format (camelcase ...)

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
|        Any        |   ==  |         =         |    ===   |
|        Any        |       |                   |    =~=   |
|        Any        |   !=  |         <>        |    <>    |
|        Any        |       |                   |    =!=   |
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

The operators `===`, `=~=`, `<>` and `=!=` are both compatible with nested objects applying the operator on all fields.

The difference between `<>` and `=!=`: the first one returns true if one of the field is not the same, the later
checks for a full inequality.

The difference between `=~=` and `===`: the first one returns true if one of the field is the same, the latter checks
for full equality.

Examples:
```scala
val findById = users.select.where(_.id === id)...

val rueDuChateauAddress = Address("15 Rue du Château", "33000", "Bordeaux", "France")
val findUsersLivingAtTheAddress = users.select.where(_.address === rueDuChateauAddress)
// SELECT * FROM users WHERE street = $1 && postalCode = $2 && city = $3 && country = $4
// $1 = "15 Rue du Château"
// $2 = "33000"
// $3 = "Bordeaux"
// $4 = "France"

val findAllOtherUsers = users.select.where(_.address <> rueDuChateauAddress)
// SELECT * FROM users WHERE street <> $1 || postalCode <> $2 || city <> $3 || country = $4
// $1 = ...

val finaAllUsersNotLivingAnywhereRessemblingTheAddress = users.select.where(_.address =!= rueDuChateauAddress)
// SELECT * FROM users WHERE street <> $1 && postalCode <> $2 && city <> $3 && country = $4
// $1 = ...


val allLuciesOver30LivingInParis = users.select.where(e => e.address.city === "Paris" && e.age >= 30)
// SELECT * FROM users WHERE city = $1 && age >= $2
// $1 = "Paris"
// $2 = 30
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
    
  def findAllEmailsOfPeopleOverTheAgeOf(age: Int): F[fs2.Stream[F, String]] =
    users.select(_.email).where(_.age > age).stream(s)
  // SELECT email FROM users WHERE age > $1 
  // $1 = age
}
```

TODO: creating own operators

### Update

#### Update DSL

The Table's `update` function can take different arguments:
An accumulation of assignments with the operators below:

|        Type       | Scala |     Postgresql    | Blaireau |
|:-----------------:|:-----:|:-----------------:|:--------:|
|        Any        |   =   |         =         |    :=    |
|      Numeric      |   -=  | field = field - _ |    -=    |
|      Numeric      |   +=  | field = field + _ |    +=    |

These assignments are combined using the `<+>` operator.

```scala
val updateFullName(id: UUID, firstName: String, lastName: String) = 
  users.update(u => (u.firstName := firstName) <+> (u.lastName := lastName)).where(_.id === id).command
```

Full class / embedded class is also supported

```scala
def updateAddress(id: UUID, address: Address) =
  users.update(_.address := address).where(_.id === id)
  
def updateFullUser1(user: User) = 
  users.update(_ := user).where(_.id === user.id)
  
def updateFullUser2(user: User) =
  users.update(user).where(_.id === user)
```

As you can see above, when the full class is updates, you can omit the `:=` operator.

If you want to know more about the `where` function, it has the same dsl as the [Select's where](#where-dsl).

#### Commands
Once your `update` function fits your needs, you have three functions which you can use.

The `toCommand` function returns a **Skunk** `Command[...]`
```scala
def updateUserAddressAndAgeCommand(id: UUID, address: Address, age: Int): Command[Address ~ Int ~ UUID] =
  users
    .update(u => (u.address := address) <+> (u.age := age))
    .where(_.id === id)
    .toCommand
```

The `commandIn` function returns the input parameters of the Command
```scala
val in: String ~ (String ~ String) = users
  .update(_.address.street := "Teerts Street")
  .where(e => e.firstName === "Chloe" && e.lastName === "Fontvi")
  .commandIn
// eq: ("Teerts Street", ("Chloe", "Fontvi"))
```

The `execute` function executed the command with the given `Session`
```scala
def updateUser(u: User): F[Completion] =
  users.update(u).where(_.id === u.id).execute(s)
```

### Delete

#### Delete DSL
Delete uses the same [where](#where-dsl) dsl as Select or Update. 

#### Commands
Once your `delete` function fits your needs, you have three functions which you can use.

The `toCommand` function returns a **Skunk** `Command[...]`
```scala
def deleteSpecificUser(id: UUID): Command[UUID] =
  users.delete.where(_.id === id).toCommand
```

The `commandIn` function returns the input parameters of the Command
```scala
val in: String = users.delete.where(_.firstName === "Valentin").commandId
// eq: "Valentin"
```

The `execute` function executed the command with the given `Session`
```scala
def deleteUser(u: UUID): F[Completion] =
  users.delete.where(_.id === u.id).execute(s)
```

