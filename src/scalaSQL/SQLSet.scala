package scalaSQL

class SQLSet[A <% SQLTuple](val op : Operation) {

    def filter(p: A => Condition)(implicit tag : reflect.ClassTag[A] ) = {
        val cond = p(tag.runtimeClass.newInstance.asInstanceOf[A])
        new SQLSet[A](Selection(cond, op))
    }

    def getQuery = toQuery(op)
}

object SQLSet {
    def apply[A <% SQLTuple](table_name : String)(implicit tag : reflect.ClassTag[A] ) = {
        val a = tag.runtimeClass.newInstance.asInstanceOf[A]
        new SQLSet[A](Relation(table_name, a.attributes))
    }
}

abstract class SQLTuple() {
    def attributes : List[FieldName[_]]
}





