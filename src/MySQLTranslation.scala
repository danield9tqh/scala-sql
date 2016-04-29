package scalasql

package object mysql {
    import scala.reflect.runtime.universe._
    
    def toMySQL(q: Query) : String = {
        val attribute_names = q.attributes.map { x => x.n }
        val select = "SELECT " + (attribute_names mkString ",")
        val from = "FROM " + q.table_name
        val where = q.condition match {
            case None => ""
            case Some(c) => "WHERE " + toMySQL(c)
        }
        
        select + "\n" + from + "\n" + where
    }
    
    def toMySQL(c : Condition) : String = c match {
        case c: GTComparison[_] => toMySQL(c.left) + " > " + toMySQL(c.right)
        case c: LTComparison[_] => toMySQL(c.left) + " < " + toMySQL(c.right)
        case c: ETComparison[_] => toMySQL(c.left) + " = " + toMySQL(c.right)
        case c: NotCondition => " NOT " + "(" + toMySQL(c.cond) + ")"
        case c: AndCondition => "(" + toMySQL(c.left) + ")" +  " AND " + "(" + toMySQL(c.right) + ")" 
        case c: OrCondition => "(" + toMySQL(c.left) + ")" +  " OR " + "(" + toMySQL(c.right) + ")" 
        case _ => ""
    }
    
    def toMySQL(f : Field[_]) : String = {
        f match {
            case FieldValue(v, t) if t =:= typeOf[Int] => v.toString
            case FieldValue(v, t) if t =:= typeOf[String] => "\"" + v.toString + "\""
            case FieldName(n, _) => n
        }
    }
    
    
}