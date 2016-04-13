package scalaSQL

class SQLSet[SQLTuple](val op : Operation) {
    
    
    def filter(cond : Condition) = new SQLSet[SQLTuple](Selection(cond, op))
    
    
//    def filter(p: SQLTuple => Condition) : Condition = {
//        
//        p(new SQLTuple())
//    }
//    
    def getQuery = toQuery(op)
}

object SQLSet {
    def apply[A <% SQLTuple](table_name : String, attributes : List[FieldName[_]]) = {
        new SQLSet[A](Relation(table_name, attributes))
    }
}

class SQLTuple() {
    val attributes : List[FieldName[_]]

}

