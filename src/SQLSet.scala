package scalasql

import java.sql.DriverManager
import java.sql.Connection
import scalasql.mysql._
import scala.reflect.runtime.universe._
import scala.language.dynamics

class SQLSet[A <% SQLTuple](val op : Operation, val fields: List[FieldName[_]], val driver: String,
        val url: String, val username: String, val password: String)(implicit val tag : reflect.ClassTag[A]) {
    
    def foreach[U](f: A => U) = tuples.foreach(f)
    
    def filter(p: A => Condition)(implicit tag : reflect.ClassTag[A]) = {
        val cond = p(tag.runtimeClass.newInstance.asInstanceOf[A])
        new SQLSet[A](Selection(cond, op), fields, driver, url, username, password)
    }

    
    def sortBy[B](f: A => B)(implicit ord: Ordering[B]) = tuples.sortBy(f)
    
    lazy val tuples: List[A] = {

        var connection:Connection = null
     

        Class.forName(driver)
        connection = DriverManager.getConnection(url, username, password)
        
        // create the statement, and run the select query
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(toMySQL(toQuery(op)))
        var results: List[A] = List()
        while ( resultSet.next() ) {
            val fieldresults = fields.map {
                case FieldName(name, t) if t =:= typeOf[Int] => (name, resultSet.getInt(name))
                case FieldName(name, t) if t =:= typeOf[String] => (name, resultSet.getString(name))
                case x => (x.n, resultSet.getInt(x.n))
            }
            var result: A = tag.runtimeClass.newInstance.asInstanceOf[A]
            for(f <- fieldresults){
                result.updateDynamic(f._1)(f._2)
                
            }
            
            results = results.+:(result)
        }
        connection.close()
        results.toList
    }
    
    def getQuery = toQuery(op)
    
}

object SQLSet {
    def apply[A <% SQLTuple](table_name : String, driver: String, url: String, 
            username: String, password: String)(implicit tag : reflect.ClassTag[A]) = {
        val a = tag.runtimeClass.newInstance.asInstanceOf[A]
        new SQLSet[A](Relation(table_name, a.attributes), a.attributes, driver, url, username, password)
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

abstract class SQLTuple extends Dynamic{
    def attributes : List[FieldName[_]]
    
    var map = Map.empty[String, Any]

    def selectDynamic(name: String) =
        map get name getOrElse sys.error("method not found")

    def updateDynamic(name: String)(value: Any) {
        map += name+"_" -> value
    }
    
}






