package JsoupTest
import org.apache.commons.lang3.StringEscapeUtils

/**
 * Created by Александр on 15.03.2015.
 */
case class HtmlItem (val url: String, val description: String) {
  override
  def toString = s"url: $url\ndescription:$description\n"

  def toJson = {
    val sb = new StringBuilder
    sb.append("{\n")
    sb.append("  \"url\": \"" + StringEscapeUtils.escapeJson(url) + "\",\n")
    sb.append("  \"snippet\": \"" + StringEscapeUtils.escapeJson(description) + "\n")
    sb.append("}")

    sb.toString()
  }
}

