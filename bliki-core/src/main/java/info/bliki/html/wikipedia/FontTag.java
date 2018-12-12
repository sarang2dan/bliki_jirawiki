package info.bliki.html.wikipedia;

import info.bliki.htmlcleaner.TagNode;

import java.util.List;
import java.util.Map;

public class FontTag extends OpenCloseHTMLTag {
    
	private boolean hasColorAttr = false;
    
	public FontTag(String opener, String closer) {
        super(opener, closer);
    }

    @Override
    public void open(TagNode node, StringBuilder resultBuffer) {
        resultBuffer.append(openStr);

        Map<String, String> tagAttributes = node.getAttributes();
        for (Map.Entry<String, String> currEntry : tagAttributes.entrySet()) {
            String attName = currEntry.getKey();
            if ( attName.equals("color") == true ) {
                String attValue = currEntry.getValue();
                attValue = attValue.replaceAll("\\\\\"", "");
              
                  
                resultBuffer.insert(
                		resultBuffer.length(), 
                		"{color:" + attValue + "}" );
                hasColorAttr = true;
            }
        }
    }

    @Override
    public void close(TagNode node, StringBuilder resultBuffer) {
    	if( hasColorAttr == true )
    	{
    		//resultBuffer.append("{color}");
    		resultBuffer.insert(
            		resultBuffer.length(), 
            		"{color}" );
    	}
    	
//        resultBuffer.append(closeStr);
    }
    
    /*
    @Override
    public void content(AbstractHTMLToWiki w, TagNode node,
        StringBuilder resultBuffer, boolean showWithoutTag) {
        List<Object> children = node.getChildren();
        if (children.size() != 0) {

            StringBuilder buf = new StringBuilder();
            if (fconvertPlainText) {
                w.nodesToPlainText(children, buf);
                char ch;
                for (int i = 0; i < buf.length(); i++) {
                    ch = buf.charAt(i);
                    if (ch == '\n' || ch == '\r' || ch == '\t') {
                        buf.setCharAt(i, ' ');
                    }
                }
            } else {
                w.nodesToText(children, buf);
            }
            String str = buf.toString();
            String trimmedStr = str.trim();
            boolean showWithout = showWithoutTag;
            if (trimmedStr.length() == 0) {
                showWithout = true;
            }
            String attValue = null;
            if (!showWithout) {
                open(node, resultBuffer);
            } else {
                Map<String, String> tagAttributes = node.getAttributes();
                attValue = tagAttributes.get("face");
                if (attValue != null && attValue.contains("Courier")) {
                    resultBuffer.append("<tt>");
                } else {
                    attValue = null;
                }
            }
            if (fconvertPlainText) {
                resultBuffer.append(trimmedStr);
            } else {
                if (formatContent) {
                    formatContent(trimmedStr, resultBuffer);
                } else {
                    resultBuffer.append(str);
                }
            }
            if (!showWithout) {
                close(node, resultBuffer);
            } else {
                if (attValue != null) {
                    resultBuffer.append("</tt>");
                }
            }

        }
    }
    */
}
