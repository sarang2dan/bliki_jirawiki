package info.bliki.api;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reads <code>Page</code> data from an XML file generated by the
 * <a href="http://meta.wikimedia.org/w/api.php">Wikimedia API</a>
 */
public class XMLPagesParser extends AbstractXMLParser {
    private static final String REV_ID = "rev";
    private static final String CATEGORY_ID = "cl";
    private static final String URL_ID = "url";
    private static final String THUMB_URL_ID = "thumburl";
    private static final String ANON_ID = "anon";
    private static final String PL_ID = "pl";
    private static final String TIMESTAMP_ID = "timestamp";
    private static final String IMAGEINFO_ID = "imageinfo";
    private static final String II_ID = "ii";
    private static final String EDIT_TOKEN_ID = "edittoken";
    private static final String WARNINGS = "warnings";

    private Page fPage;
    private Revision fRevision;
    private List<Page> pagesList;
    private List<String> warnings;
    private boolean isParsingWarnings;

    public XMLPagesParser(String xmlText) throws SAXException {
        super(xmlText);
        pagesList = new ArrayList<>();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        fAttributes = atts;

        if (PAGE_TAG1.equals(qName) || PAGE_TAG2.equals(qName)) {
            fPage = new Page();
            fPage.setPageid(fAttributes.getValue(PAGE_ID));
            fPage.setNs(fAttributes.getValue(NS_ID));
            fPage.setTitle(fAttributes.getValue(TITLE_ID));
            fPage.setEditToken(fAttributes.getValue(EDIT_TOKEN_ID));
        } else if (REV_ID.equals(qName)) {
            fRevision = new Revision();
            fRevision.setAnon(fAttributes.getValue(ANON_ID));
            fRevision.setTimestamp(fAttributes.getValue(TIMESTAMP_ID));
            fPage.setCurrentRevision(fRevision);
        } else if (CATEGORY_ID.equals(qName)) {
            if (fPage != null) {
                PageInfo cat = new PageInfo();
                cat.setNs(fAttributes.getValue(NS_ID));
                cat.setTitle(fAttributes.getValue(TITLE_ID));
                fPage.addCategory(cat);
            }
        } else if (PL_ID.equals(qName)) {
            if (fPage != null) {
                Link link = new Link();
                link.setNs(fAttributes.getValue(NS_ID));
                link.setTitle(fAttributes.getValue(TITLE_ID));
                fPage.addLink(link);
            }
        } else if (II_ID.equals(qName)) {
            // <imgeinfo><ii url="...">...</imageinfo>
            if (fPage != null) {
                fPage.setImageUrl(fAttributes.getValue(URL_ID));
                fPage.setImageThumbUrl(fAttributes.getValue(THUMB_URL_ID));
            }
        } else if (WARNINGS.equals(qName)) {
            warnings = new ArrayList<>();
            isParsingWarnings = true;
        }
        fData = null;
    }

    @Override
    public void endElement(String uri, String name, String qName) {
        try {
            if (REV_ID.equals(qName)) {
                if (fRevision != null) {
                    fRevision.setContent(getString());
                }
            } else if (PAGE_TAG1.equals(qName) || PAGE_TAG2.equals(qName)) {
                if (fPage != null) {
                    pagesList.add(fPage);
                }
            } else if (WARNINGS.equals(qName)) {
                isParsingWarnings = false;
            } else if (isParsingWarnings) {
                String warning = getString();
                if (warning != null) {
                    warnings.add(warning);
                }
            }
            fData = null;
            fAttributes = null;
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
    }

    public List<Page> getPagesList() {
        return pagesList;
    }

    public List<String> getWarnings() {
        if (warnings == null) {
            return Collections.emptyList();
        } else {
            return warnings;
        }
    }
}
