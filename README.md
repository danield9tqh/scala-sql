# scala-sql
scala-sql is a functional-relational mapping library for Scala. SQL tables can be represented in Scala code as a collection code simply by providing the table's name, schema and endpoint. The following is an example definition of a table schema in both SQL syntax and scala-sql syntax. 

	CREATE TABLE students (id INT(13), name VARCHAR(20), age INT(13))

	class Student extends SQLTuple {
    	val id = Field[Int]("id")
    	val name = Field[String]("name")
    	val age = Field[Int]("age")

    	override def attributes = List(id, name, age)
	}

After defining the `Student` class in Scala, a collection for the table can be instantiated with the `SQLSet` object:

	val students = SQLSet[Student]("students", driver, url, username, password)

Actions that are common to Scala collections such as `filter` and `foreach` can also be performed on a `SQLSet` object:

	val first_ten = students.filter(_.id < 10)

    for (s <- first_ten){
        println("name: " + s.name_ + ", age: " + s.age_ + ", id: "+ s.id_)
    }

Currently when referencing an attribute of an element in a `SQLSet`, "_" must be added to the end of the attribute name. For example `s.name_` instead of `s.name`.

This project is still in progress and expects implementation of more Scala collections functions beyond `filter` and `foreach`. An example program using scala-sql can be found [here](https://github.com/danield9tqh/scala-sql-test).