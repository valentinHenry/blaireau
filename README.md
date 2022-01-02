# Blaireau
[![CI](https://github.com/valentinHenry/blaireau/actions/workflows/build.yml/badge.svg)](https://github.com/valentinHenry/blaireau/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/valentinHenry/blaireau/branch/master/graph/badge.svg?token=LFQYEC7QD4)](https://codecov.io/gh/valentinHenry/blaireau)

**Blaireau** is a minimalistic type safe SQL DSL for the [Skunk library](https://github.com/tpolecat/skunk)

:warning: **Blaireau** is a personal project in addition to being a work in progress, **hence the lack of documentation,
code and functionalities.**

If this project interests you, feel free to drop a :star: to encourage me working on this.

## Table of Content
- [Quickstart with sbt](#quickstart-with-sbt)
- [Metas](#metas)
- [Configuration](#configuration)
    * [Field formatter](#field-formatter)
    * [String Codec](#string-codec)
    * [Default Json Type](#default-json-type)
    * [Using the configuration](#using-the-configuration)
- [Table](#table)
    * [Overriding sql names](#overriding-sql-names)
- [Field selection](#field-selection)
- [Filtering](#filtering)
- [Queries and Commands builders](#queries-and-commands-builders)
    * [Select](#select)
        + [Where](#where)
        + [Querying](#querying)
        + [Example](#example)
    * [Update](#update)
        + [Commands](#commands)
    * [Delete](#delete)
        + [Commands](#commands-1)
    * [Insert](#insert)
        + [Commands](#commands-2)


## Quickstart with sbt
If you want to test / have fun with it, no artefacts is published at the moment therefore you must publish it locally
```shell
$> git clone git@github.com:valentinHenry/blaireau.git
$> cd blaireau/
$> sbt publishLocal
```

In your `build.sbt` add the following line:

```scala
// For the SQL DSL:
libraryDependencies += "fr.valentinhenry" %% "blaireau-dsl" % Version
// For Timepit's Refined Metas
libraryDependencies += "fr.valentinhenry" %% "blaireau-refined" % Version
// For Estatico's Newtype Metas
libraryDependencies += "fr.valentinhenry" %% "blaireau-estatico" % Version
// For Circe's Json Metas
libraryDependencies += "fr.valentinhenry" %% "blaireau-circe" % Version
```

## Metas
A meta is a representation of the class in the database. It is derived using Shapeless.

At the moment, only simple types are supported.

A `MetaS[T]` is a meta instance of a type `T`. You can create any `MetaS` you want using the `Meta.of(...)` function. It
requires only a `skunk.Codec[T]`.

:warning: It sould only be types, not case class or it will mess with the derivation!

Metas must be implicitly available in the scope where the `Table` and `Meta` generation take places.

## Configuration

The configuration is used for meta derivation and field formatting.

### Field formatter

In order to translate from the scala camelCase to the one used in postgresql, a field formatter should be given in the
configuration, currently there are 6 available options:

- camelCase (same as Scala)
- PascalCase
- ALLCAPS
- lowercase
- snake_case
- UPPER_SNAKE_CASE

By default, **Blaireau** will use snake_case.

### String Codec

The default string codec is configurable, by default it is set as `text` but it is possible to change it as `varchar`
or any one required as long as it is a `skunk.Codec[String]`.

This codec is used everywhere there is a String field.

### Default Json Type

When using `blaireau-circe`, an implicit encoder is required for Json objects. The field `jsonTypeAsJsonb` is used to
chose the format of the Json objects in the DB (json or jsonb).

By default it is set to `jsonb`

### Using the configuration

The default configuration sets the following fields:

- field formatter: camel_case
- string codec: text
- default json type: jsonb

In case these configurations are not the one required, an implicit instance of a Configuration must be provided in the
derivation scope like below:

In the following examples, the default configuration will be used.

## Table

Blaireau maps a case class to a database object. This mapping is done through a Meta-object derivation.

To create a table, you just have to use the `apply` method of the `Table` object like below:

```scala
import blaireau.dsl._

import java.util.UUID

final case class Address(street: String, info: Option[String], postalCode: String, city: String, country: String)
final case class User(id: UUID, email: String, firstName: String, lastName: String, age: Int, address: Option[Address])

val users = Table[User]("users")
```

We will be using the table above for all following examples.

### Overriding sql names

Sometimes it is necessary to override sql fields name in case an object has fields which does not correspond to the
formatted one. For example:

```sql
CREATE TABLE childs (
  id            TEXT PRIMARY KEY,
  name          TEXT NOT EMPTY
  mail          TEXT NOT EMPTY,
  parents_name  TEXT NOT EMPTY,
  parents_email TEXT NOT EMPTY,
)
```

A class mapping directly the fields would look like this:

```scala
case class Child(id: UUID, name: String, mail: String, parentsName: String, parentsEmail: String)
```

However, we might want the information name and email contained in am information class, like below:

```scala
case class Information(name: String, email: String)
case class Child2(id: UUID, info: Information, parentsInfo: Information)
```

Using the classes above without overriding the fields names would not work as expected since there will be two "name"
and two "email" columns.

```scala
val nonViableTable =  Table[Child2]("childs")
nonViableTable.select.where(_.id === UUID.randomUUID())
// SELECT id, name, email, name, email FROM childs WHERE id = $1
```

To fix this issue, we can tell blaireau to use the given name instead of the derived one.

```scala
val childs = Table[Child2]("childs")
  .columns(
    _.parentsInfo.name -> "parents_name",
    _.parentsInfo.email -> "parents_email"
  )

childs.select.where(_.id === UUID.randomUUID())
// SELECT id name, email, parent_name, parent_email FROM childs where id = $1
```

## Field selection

This is used in the `select` function from the Select Query Builder and in the `update` function from the Update Command
Builder.

The fields are accessed using their scala names (regardless of the format used in the db).

It is possible to select multiple columns using the `~` operator just like in **Tapir**.

Examples:

```scala
table.select(e => e.id)...
table.update(e => e.email ~ e.lastName)
```

Embedded objects and embedded fields are supported.

Examples:

```scala
table.update(_.address)
table.select(e => e.id ~ e.address.country)
```

## Filtering

This is used in the `where`, `whereAnd` and `whereOr` functions of the builders.

Just like the select, the classes fields are accessed using their scala names (regardless of the format used in the db).

All boolean operations and boolean fields can be composed with those operators:

| Type Constraint | Scala |     Postgresql    | Blaireau |
|:---------------:|:-----:|:-----------------:|:--------:|
|     Boolean     |  &&   |        AND        |    &&    |
|     Boolean     |   \|\| |        OR         |   \|\|   |
|     Boolean     | !     | NOT | ! |

Equality operator are available for all fields

| Type Constraint |   Scala   | Postgresql  | Blaireau  |
|:---------------:|:---------:|:-----------:|:---------:|
|       Any       |    ==     |      =      |    ===    |
|       Any       |           |             |    =~=    |
|       Any       |    !=     |     <>      |    <>     |
|       Any       |           |             |    =!=    |

The difference between `<>` and `=!=`: the first one returns true if one of the field is not the same, the later checks
for a full inequality.

The difference between `=~=` and `===`: the first one returns true if one of the field is the same, the latter checks
for full equality.

Examples:

```scala
// e is considered as the parameter of a where function of a Table[User]

e.email === "patrick@blaireau-corp.com" 
// email = $1

e.firstName <> "Patrick"
// first_name <> $1

e.address <> someAddress
// street <> $1 OR info <> $2 OR postal_code <> $3 OR city <> $4 OR country <> $5

e.address =!= someAddress
// street <> $1 AND info <> $2 AND postal_code <> $3 AND city <> $4 AND country <> $5

e.address === someAddress
// street = $1 AND info = $2 AND postal_code = $3 AND city = $4 AND country = $5

e.address =~= someAddress
// street = $1 OR info = $2 OR postal_code = $3 OR city = $4 OR country = $5
```

Comparison operators are available for the Numeric (Int, Float etc.), Strings and Temporal (Dates etc.)

| Type Constraint |   Scala   | Postgresql  | Blaireau  |
|:---------------:|:---------:|:-----------:|:---------:|
|    Numeric \| String \|  Temporal   |      <      |     <     |     <    |
|    Numeric \| String \|  Temporal   |     <=      |    <=     |    <=    |
|    Numeric \| String \|  Temporal   |      >      |     >     |     >    |
|    Numeric \| String \|  Temporal   |     >=      |    >=     |    >=    |

Example:

```scala
e.age < 5
// age < $1
```

String fields has the like function which pattern match unsing PostgreSQL syntax

| Type Constraint |   Scala   | Postgresql  | Blaireau  |
|:---------------:|:---------:|:-----------:|:---------:|
|     String      |           |    like     |   like    |

Example:

```scala
e.firstName.like("P%")
// first_name LIKE $1
```

Optional fields and objects have their own functions

| Type Constraint |   Scala   | Postgresql  | Blaireau  |
|:---------------:|:---------:|:-----------:|:---------:|
|    Optional     |  isEmpty  |   IS NULL   |  isEmpty  |
|    Optional     | isDefined | IS NOT NULL | isDefined |

Example:

```scala
e.address.isDefined
// street IS NOT NULL OR info IS NOT NULL OR postal_code IS NOT NULL OR city IS NOT NULL OR country IS NOT NULL
```

`isEmpty` is the negation of isDefined.

:warning: Keep in mind that this function is not completely safe since it is looking at fields instead of the mapped
object itself.

The function below allows you to interact with the object as if it was non-empty just like in scala.

| Type Constraint |   Scala   | Postgresql  | Blaireau  |
|:---------------:|:---------:|:-----------:|:---------:|
|    Optional     | contains  |             | contains  |
|    Optional     |  exists   |             |  exists   |
|    Optional     |  forall   |             |  forall   |

Example:

```scala
//val dummyAddress: Address = ???

e.address.contains(dummyAddress)
// street = $1 AND info = $2 AND postal_code = $3 AND city = $4 AND country = $5

e.address.exists(_.info.isDefined)
// ${Same as e.address.isDefined} AND info IS NOT NULL

e.address.forall(_.city === "Paris")
// ${Same as e.address.isEmpty} OR city = $1
```

TODO: creating own operators

## Queries and Commands builders

### Select

Only simple select queries can be generated using **Blaireau**, for more advance ones, it is better to use **Skunk** sql
interpolator.

There are options regarding the selection of fields:

- Whole object selection
- Specific fields selection

For the whole object selection, the `select` function should not have parameters

```scala
val users: Query[Void, User] = users.select...
```

For a more specific selection of fields, it can be done using the `~` operator as explained in
the [Field selection](#field-selection) section.

```scala
import skunk.~
val namesAndAge: Query[Void, String ~ Int] = users.select(e => e.firstName ~ e.age)...
```

#### Where

A select query has three functions which can be used for composing the where part (no where is considered as `TRUE`).

- `.where(...)` is the first function to use.
- `.whereAnd(...)` is appending the previous where and the one specified as parameter with an `AND`
- `.whereOr(...)` is like the previous one but with `OR`

#### Querying

Once the select query fits your needs, you can chose the function which fits your needs the best.

The `toQuery` function compiles the query into a **Skunk** `Query`.

```scala
import java.util.UUID
val findById: Query[UUID, User] = users.select.where(_.id === UUID.randomUUID()).toQuery
```

The `queryIn` returns the input parameter given to the query builder
```scala
val findChristopheOrRetired = users.select(_.id).where(e => e.firstName === "Christophe" || e.age >= 60)
// val findCORQuery: Query[String ~ Int, UUID] = findChristopheOrRetired.toQuery
val findCORQueryInputParams: String ~ Int = findChristopheOrRetired.queryIn
// Eq to: ("Christophe", 60)
```

Or you can use wrappers on **Skunk** which returns a F[_].

#### Example
```scala
import cats.effect.MonadCancelThrow
import blaireau.dsl._
import skunk.Session

final class UsersSql[F[_]: MonadCancelThrow](s: Resource[F, Session[F]]){
  val users = Table[User]("users")
  
  def findById(id: UUID): F[User] =
    s.use(users.select.where(_.id === id).unique(_))
    
  def findByCountry(country: String): F[fs2.Stream[F, User]] =
    s.use(users.select.where(_.address.country === country).stream(_))
    
  def findByEmail(email: String): F[Option[User]] =
    s.use(users.select.where(_.email === email).option(_))
    
  def findAllEmailsOfPeopleOverTheAgeOf(age: Int): F[fs2.Stream[F, String]] =
    s.use(users.select(_.email).where(_.age > age).stream(_))
}
```

### Update

The Table's `update` function can be either empty or with a combination of assignments:

An assignment is done using the operators below:

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

If you want to know more about the `where` function, it has the same dsl as the [Select's Where](#where).

#### Commands
Once your `update` command fits your needs, you have three functions which you can use.

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

The `execute` function prepares and executes the command with the given `Session`

```scala
def updateUser(u: User): F[Completion] =
  users.update(u).where(_.id === u.id).execute(s)
```

TODO: generate update functions with n parameters to omit using the combine operator

### Delete

Delete uses the same [where](#where) dsl as Select or Update.

#### Commands

Once your `delete` command fits your needs, you have three functions which you can use.

The `toCommand` function returns a **Skunk** `Command[...]`

```scala
def deleteSpecificUser(id: UUID): Command[UUID] =
  users.delete.where(_.id === id).toCommand
```

The `commandIn` function returns the input parameters of the Command
```scala
val in: String = users.delete.where(_.firstName === "Valentin").commandIn
// eq: "Valentin"
```

The `execute` function prepares and executes the command with the given `Session`
```scala
def deleteUser(u: UUID): F[Completion] =
  users.delete.where(_.id === u.id).execute(s)
```

### Insert

There are options regarding the insertion of fields:
- Whole object insertion
- Specific fields insertion

For the whole object insertion, the `insert` function should not have parameters
```scala
val insertUser: Command[User] = users.insert...
```

For a more specific insertion, it can be done using the `~` operator seen in the [Field selection](#field-selection)
section.
```scala
import skunk.~
val createUserWithTheAddress: Command[UUID ~ Address] = users.insert(e => e.id ~ e.address)...
// INSERT INTO users(id, street, postalCode, city, country) VALUES ($1, $2, $3, $4, $5)...
```

You can insert either a single value or a list of values:
```scala
val dummyUser: User = ???
val insertUser = users.insert.value(dummyUser)

val dummyUserList: List[User] = ???
val insertAllUsers = users.insert.values(dummyUserList)
```

:warning: The `values` insertion keeps in memory the size of the list given. The command created by this cannot be used
with a list of a different size.

#### Commands 

Once your `insert` command fits your needs, you have three functions which you can use.

The `toCommand` function returns a **Skunk** `Command[...]`
```scala
def insertUser(user: User): Command[User] =
  users.insert.value(user).toCommand
```

The `commandIn` function returns the input parameters of the Command
```scala
val dummyId = UUID("db373385-29cd-4380-9cf5-5501c94b91a1")
val dummyAddress = Address("Str", "Pc", "City", "Ctr")
val in: UUID ~ Address = users.insert(u => u.id ~ u.address).value(dummyId ~ dummyAddress).commandIn
// eq: (UUID("db373385-29cd-4380-9cf5-5501c94b91a1"), Address("Str", "Pc", "City", "Ctr"))
```

The `execute` function prepares and executes the command with the given `Session`
```scala
def insertUser(users: List[User]): F[Completion] =
  users.insert.values(users).execute(s)
```
