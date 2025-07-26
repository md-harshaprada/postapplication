package secondwebapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
public class HtmlUtils {
    public static String truncateHtml(String html, int maxLength) {
        Document doc = Jsoup.parse(html);
        Element body = doc.body();
        StringBuilder truncated = new StringBuilder();
        int length = 0;

        for (Element element : body.children()) {
            String text = element.text();
            
            if (length + text.length() > maxLength) {
                truncated.append(text, 0, maxLength - length);
                break;
            } else {
                length += text.length();
                truncated.append(element.outerHtml());
            }

            if (length >= maxLength) {
                break;
            }
        }

        return truncated.toString();
    }
}
