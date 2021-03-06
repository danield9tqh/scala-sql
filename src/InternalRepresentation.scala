
package object scalasql {
    import scala.reflect.runtime.universe._
    
    abstract class Field[T <% Comparable[T]]
    
    object Field {
        def apply[T <% Comparable[T]: TypeTag](x : String) = new FieldName[T](x, typeOf[T])
    }
    
    case class FieldName[T <% Comparable[T]](n: String, t: Type) extends Field[T] {
        def == (that : Field[T]) = ETComparison(this, that)
        def != (that : Field[T]) = NotCondition (this == that)
        def >  (that : Field[T]) = GTComparison(this, that)
        def <  (that : Field[T]) = LTComparison(this, that)
        def >= (that : Field[T]) = OrCondition(this > that, this == that)
        def <= (that : Field[T]) = OrCondition(this < that, this == that)
    }

    
    case class FieldValue[T <% Comparable[T]](value: T, t: Type) extends Field[T]
    implicit def fromInt(i : Int) = new FieldValue[Int](i, typeOf[Int])
    implicit def fromString(s : String) = new FieldValue[String](s, typeOf[String])
    
    abstract class Operation
    case class Relation(val name : String, val fields : List[FieldName[_]]) extends Operation


    abstract class Condition
    case class GTComparison[T](left : Field[T], right : Field[T]) extends Condition
    case class LTComparison[T](left : Field[T], right : Field[T]) extends Condition
    case class ETComparison[T](left : Field[T], right : Field[T]) extends Condition
    case class NotCondition(cond : Condition) extends Condition
    case class AndCondition(left : Condition, right : Condition) extends Condition
    case class OrCondition(left : Condition, right : Condition) extends Condition

    
    abstract class UnaryOperation extends Operation
    case class Projection(val attributes : List[FieldName[_]], val op : Operation) extends UnaryOperation
    case class Selection(val cond : Condition, val op : Operation) extends UnaryOperation
//    case class Rename(val start : Attribute[Any], val end : Attribute[Any], val op : Operation) extends UnaryOperation
    

//    abstract class BinaryOperation extends Operation;
//    case class Product() extends BinaryOperation;
//    case class Union() extends BinaryOperation;
//    case class Difference() extends BinaryOperation;
    
    
    
    
    case class Query(val attributes : List[FieldName[_]], table_name : String, condition : Option[Condition] = None)

    def toQuery(op: Operation) = op match {
        case r : Relation => relationToQuery(r)
        case p : Projection => projectionToQuery(p)
        case s : Selection => selectionToQuery(s)
//        case r : Rename => renameToQuery(r)
        case _ => throw new Exception("Operation not recognized")
    }
    
    def relationToQuery(r : Relation) : Query = Query(r.fields, r.name, None)
    
    def projectionToQuery(p : Projection) : Query = {
        val original : Query = toQuery(p.op)
        val new_attributes = p.attributes.intersect(original.attributes)
        Query(new_attributes, original.table_name, original.condition)
    }
    
    def selectionToQuery(s : Selection) : Query = {
        val original : Query = toQuery(s.op)
        val new_condition = original.condition match {
            case None => Some(s.cond)
            case Some(c) => Some(AndCondition(c, s.cond))
        }
        Query(original.attributes, original.table_name, new_condition)
    }   
}
