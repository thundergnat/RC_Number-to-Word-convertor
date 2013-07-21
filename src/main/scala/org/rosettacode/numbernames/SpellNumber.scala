package org.rosettacode
package numbernames

object SpellNumber {
  /** Spells a number longhand
   *
   *  @example longhand( 1234 )  // results in: one thousand two hundred thirty-four
   */
  def longhand(v: Long, showAnd: Boolean = true, showHyphen: Boolean = true): String = {
    // Based on a solution of Rex Kerr's

    val onesAndTeens = {
      val a1 = Array[String]("")
      val a2 = "one two three four five six seven eight nine ten eleven twelve".split(' ').map(_ + " ")
      val a3 = "thir four fif six seven eigh nine".split(' ').map(_ + "teen ")
      a1 ++ a2 ++ a3
    }

    val tens = {
      val a1 = Array[String]("", "")
      val a2 = "twen thir for fif six seven eigh nine".split(' ').map(_ + "ty")
      a1 ++ a2
    }

    val hundredString = "hundred "
    val andString = if (showAnd) "and " else ""
    val hyphenString = if (showHyphen) "-" else " "

    val powersOfThousands = {
      val a1 = Array[String]("", "thousand ")
      val a2 = "m b tr quadr quint sext sept oct non dec".split(' ').map(_ + "illion ")
      val a3 = "un duo tre quattuor quin sex septen octo novem ".split(' ').map(_ + "decillion ")
      val a4 = "vigint cent".split(' ').map(_ + "illion ")
      a1 ++ a2 ++ a3 ++ a4
    }

    // 234 becomes "two hundred [and] thirty-four"
    def composeScale(v: String, isFirst: Boolean): String = {

      val e1 = (v.map(_.toString.toInt).reverse zip List("", "-", "hundred")).reverse

      e1.map {
        case (d, "hundred") if d > 0 => onesAndTeens(d) + hundredString + andString
        case (d, "-") if d > 1 && !e1.contains((0, "")) && isFirst => tens(d) + hyphenString
        case (d, "-") if d > 1 && !e1.contains((0, "")) => tens(d) + " "
        case (d, "-") if d > 1 => tens(d)
        case (d, "") if e1.contains((1, "-")) => onesAndTeens(d + 10)
        case (d, "") => onesAndTeens(d)
        case _ => ""
      }.mkString
    }

    def compose(s: String): String = {

      // "1234" becomes List(((1,false),"thousand"), ((234,true),""))
      val thousands = {
        def first(s: String) = (true) #:: Stream.continually(false) // First element true others false
        val e1 = s.reverse.grouped(3).map(_.reverse).toList // Group into powers of thousands
        val e2 = e1 zip first(s) // Mark the first element
        val e3 = e2 zip powersOfThousands // Name the powers of Thousands
        e3.reverse // Put it back to most-significant first
      }

      thousands.map { case ((v, isFirst), s) => composeScale(v, isFirst) + s }.mkString.trim
    }

    if (v < 0) "minus " + compose((-v).toString) else compose(v.toString)
  }

  // A little test...		1000000000 and 0 fails!
  def main(args: Array[String]): Unit = {
    //TODO: pure powers of 10 fails
    //TODO: 0 fails
    //TODO: showAnd fails at 100

    { // Anonymous ordered list with test values
      def testVal1 = 1000000000000000000L
      def testVal9 = 999999999999999999L
      def inner(counter: Int, acc: Long, testList: List[Long]): List[Long] = {
        if (counter < 20)
          inner(counter + 1, acc * 10 + (9 - (counter % 10)), testList ++ List(acc))
        else testList
      }
      inner(0, 0L, List(-testVal1, 12, 19, 100, testVal1, testVal9, Long.MaxValue - 13))
    }.sorted.
      foreach(num => println(f"""$num%,26d is ${longhand(num)}"""))
  }
} // object SpellNumber