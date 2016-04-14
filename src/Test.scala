
import scalaSQL._
import scalaSQL.MySQLTranslation._


object Test extends App {
    
    val students = SQLSet[Student]("students")
    
    val first_ten = students.filter(_.age > 20)

    println (toMySQL(first_ten.getQuery))
}

class Student() extends SQLTuple {
    val id = Field[Int]("id")
    val name = Field[String]("name")
    val age = Field[Int]("age")

    override def attributes = List(id, name, age)
}



