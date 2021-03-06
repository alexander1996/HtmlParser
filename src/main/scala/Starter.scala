import java.util
import java.util.regex.{Matcher, Pattern}
import ParsDateTime.{BaseDateTime, GeekBrainsDateTime}
import WorkWithHtml.{HtmlItem, Logger, ReaderConfiguration}
import org.joda.time.DateTime

object Starter {
  private val postUrl = "http://188.226.178.169:3000/insert-document"
  private val logger = new Logger("article")
  private val reloadDelaySec = 30*60
  private val nameConfigFile = "configuration.json"

  def main(args:Array[String]):Unit = {
    try {
    infiniteLoop
//    val articleWriter = new ArticleWriter
//    articleWriter.WriteToFiles(HashSetToArray(getAllHtmlItems()))

    }catch{
      case e: Exception => logger.write(e.getMessage)
    }
 }

  private def AddArrayToSet(array : Array[HtmlItem],hashSet : util.HashSet[HtmlItem]): Unit ={
    for(item <- array)
    {
      hashSet.add(item)
    }
  }

  private def postItem(item: HtmlItem) {
    try{
      val res = PostRequest.send(postUrl, item.toJson)
      res match {
        case x: DocExists => logger.write("DocExists: "+item.url)
        case x: OK        => logger.write("OK: "+item.url)
      }
    }catch {
      case ex: Exception => logger.Error(ex)
    }
  }
  private def getAllHtmlItems(): util.HashSet[HtmlItem] = {
    val HtmlItems = new util.HashSet[HtmlItem]()
    try {
      val arraySites = new ReaderConfiguration(nameConfigFile).getWebSites()

      for (site <- arraySites) {
        try {
          HtmlItems.addAll( site.getAllHtmlItems )
        }
        catch {
          case ex: Exception => logger.Error(ex)
        }
      }
    }catch{
      case ex : Exception => logger.Error(ex)
    }
    HtmlItems
  }

  private def shouldLoad(lastLoad: Option[DateTime]) : Boolean= {
    lastLoad match {
               case None    => true
               case Some(x) => DateTime.now.isAfter(x.plusSeconds(reloadDelaySec))
            }
  }

  private def infiniteLoop(): Unit = {
    var lastLoad: Option[DateTime] = None
    while(true){
      try{
        if (shouldLoad(lastLoad)){
          val setArticle = this.getAllHtmlItems
          val arrayArticle = HashSetToArray(setArticle)

          arrayArticle.foreach( postItem( _ ) )                                              // отправляем данные на сервер
          lastLoad = Some(DateTime.now)
        }
        Thread.sleep(10000)
      }
      catch {
        case x :Exception  => logger.write("Error: " + x.getMessage)
      }
    }
  }

  private def HashSetToArray(hashSet : util.HashSet[HtmlItem]): Array[HtmlItem] ={
    val array = new Array[HtmlItem](hashSet.size)
    hashSet.toArray(array)

    array
  }


}

