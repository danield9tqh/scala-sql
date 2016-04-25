package scalasql

import java.sql.DriverManager
import java.sql.Connection
import scalasql.mysql._
import scala.reflect.runtime.universe._

class SQLSet[A <% SQLTuple](val op : Operation, val fields: List[FieldName[_]]) {

    def filter(p: A => Condition)(implicit tag : reflect.ClassTag[A]) = {
        val cond = p(tag.runtimeClass.newInstance.asInstanceOf[A])
        new SQLSet[A](Selection(cond, op), fields)
    }

    def tuples = {
        val driver = "com.mysql.jdbc.Driver"
        val url = "jdbc:mysql://localhost/test"
        val username = "danield9tqh"
        val password = "compound"

        var connection:Connection = null
     

        Class.forName(driver)
        connection = DriverManager.getConnection(url, username, password)
        
        // create the statement, and run the select query
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(toMySQL(toQuery(op)))
        var results: List[Any] = List()
        while ( resultSet.next() ) {
            val result = fields.map {
                case FieldName(name, t) if t =:= typeOf[Int] => resultSet.getInt(name)
                case FieldName(name, t) if t =:= typeOf[String] => resultSet.getString(name)
                case x => resultSet.getInt(x.name)
            }
            
            results = results ++ result
        }
        connection.close()
        results
    }
    
    def getQuery = toQuery(op)
    
}

object SQLSet {
    def apply[A <% SQLTuple](table_name : String)(implicit tag : reflect.ClassTag[A]) = {
        val a = tag.runtimeClass.newInstance.asInstanceOf[A]
        new SQLSet[A](Relation(table_name, a.fields), a.fields)
    }
    
//    def listProperties[T: TypeTag]: List[(TermSymbol, Annotation)] = {
//        // a field is a Term that is a Var or a Val
//        val fields = typeOf[T].members.collect{ case s: TermSymbol => s }.
//        filter(s => s.isVal || s.isVar)
//
//        fields.flatMap(f => f.annotations.find(_.tpe =:= typeOf[MyProperty]).
//        map((f, _))).toList
//    }
    
}

abstract class SQLTuple() {
    def fields : List[FieldName[_]]
}






