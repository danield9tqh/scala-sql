
import scalaSQL._
import scalaSQL.MySQLTranslation._


object Test extends App {

//    val t1 = new SQLTuple();
//            
//    val intField : Field[String] = "his"
//    
    val student = new Student()
    
    val students = SQLSet[Student]("students", student.attributes)
    
    val first_ten = students.filter(student.id > 10)
    
    println (toMySQL(first_ten.getQuery))
}

class Student() extends SQLTuple {
    val id = new FieldName[Int]("id")
    val name = new FieldName[String]("name")
    val age = new FieldName[Int]("age")

    val attributes = List(id, name, age)
}



