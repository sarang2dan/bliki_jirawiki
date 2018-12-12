package info.bliki.html.wikipedia;
 
import java.util.HashMap;
import java.util.Map;
 
public class ToWikipedia extends AbstractHTMLToWiki implements IHTMLToWiki {
    static private final Map<String, HTMLTag> TAG_MAP = new HashMap<>();
    static {
        TAG_MAP.put("body", new OpenCloseTag("", ""));
        TAG_MAP.put("br", new OpenCloseTag("", "\\n"));
        TAG_MAP.put("a", new ATag());
        TAG_MAP.put("b", new OpenCloseTag("*", "*"));
        TAG_MAP.put("strong", new OpenCloseTag("*", "*"));
        TAG_MAP.put("i", new OpenCloseTag("_", "_"));
        TAG_MAP.put("em", new OpenCloseTag("_", "_"));
        TAG_MAP.put("table", new OpenCloseTag("", ""));
        // TAG_MAP.put("caption", new OpenCloseTag("\n", "\n"));
        TAG_MAP.put("tr", new TrTag());
        TAG_MAP.put("td", new TdTag());
        TAG_MAP.put("th", new TdTag()); // wiki¿¡ŽÂ header °­Á¶±âŽÉÀÌ ŸøŽÙ.
        TAG_MAP.put("img", new ImgTag());
        TAG_MAP.put("p", new OpenCloseTag("\\n", "\\n\\n", false, true));
        TAG_MAP.put("code", new OpenCloseTag("{code}", "{code}"));
        TAG_MAP.put("blockquote", new OpenCloseTag("{quote}", "{quote}"));
        TAG_MAP.put("u", new OpenCloseTag("+", "+"));
        TAG_MAP.put("del", new OpenCloseTag("-", "-"));
        TAG_MAP.put("s", new OpenCloseTag("-", "-"));
        TAG_MAP.put("sub", new OpenCloseTag("~", "~"));
        TAG_MAP.put("sup", new OpenCloseTag("^", "^"));
        TAG_MAP.put("div", new OpenCloseTag("", "\\n"));
        TAG_MAP.put("pre", new OpenCloseTag("\\n", "\\n\\n", false, true));
        TAG_MAP.put("h1", new OpenCloseTag("h1. ", "\\n"));
        TAG_MAP.put("h2", new OpenCloseTag("h2. ", "\\n"));
        TAG_MAP.put("h3", new OpenCloseTag("h3. ", "\\n"));
        TAG_MAP.put("h4", new OpenCloseTag("h4. ", "\\n"));
        TAG_MAP.put("h5", new OpenCloseTag("h5. ", "\\n"));
        TAG_MAP.put("h6", new OpenCloseTag("h6. ", "\\n"));
        TAG_MAP.put("font", new FontTag("", ""));
        TAG_MAP.put("ul", new ListTag("*"));
        TAG_MAP.put("ol", new ListTag("#"));
        TAG_MAP.put("script", new NoOutputTag());
    }
 
    public ToWikipedia(boolean noDiv, boolean noFont, boolean noMSWordTags) {
        super(TAG_MAP, noDiv, noFont, noMSWordTags);
    }
    public ToWikipedia(boolean noDiv, boolean noFont) {
        this(noDiv, noFont, false);
    }
    public ToWikipedia() {
        this(false, false, false);
    }
}
