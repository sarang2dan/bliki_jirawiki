package info.bliki.wiki.impl;

import info.bliki.api.creator.ImageData;
import info.bliki.api.creator.TopicData;
import info.bliki.api.creator.WikiDB;
import info.bliki.htmlcleaner.TagNode;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.filter.ParsedPageName;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.ImageFormat;
import info.bliki.wiki.model.WikiModel;
import info.bliki.wiki.model.WikiModelContentException;
import info.bliki.wiki.namespaces.INamespace;
import info.bliki.wiki.namespaces.INamespace.NamespaceCode;
import info.bliki.wiki.tags.WPATag;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Locale;
import java.util.Map;

import static info.bliki.wiki.tags.WPATag.CLASS;
import static info.bliki.wiki.tags.WPATag.HREF;
import static info.bliki.wiki.tags.WPATag.WIKILINK;

public class DumpWikiModel extends WikiModel {
    private Siteinfo fSiteinfo;
    private WikiDB fWikiDB;
    private final String fTemplateNamespace;

    private final File fImageDirectory;
    static {
        TagNode.addAllowedAttribute("style");
    }

    /**
     *
     *
     * @param wikiDB
     *          a wiki database to retrieve already cached templates
     * @param imageBaseURL
     *          a url string which must contains a &quot;${image}&quot; variable
     *          which will be replaced by the image name, to create links to
     *          images.
     * @param linkBaseURL
     *          a url string which must contains a &quot;${title}&quot; variable
     *          which will be replaced by the topic title, to create links to
     *          other wiki topics.
     * @param imageDirectory
     *          a directory for storing downloaded Wikipedia images. The directory
     *          must already exist.
     */
    public DumpWikiModel(WikiDB wikiDB, Siteinfo siteinfo, String imageBaseURL, String linkBaseURL, @Nullable File imageDirectory) {
        this(wikiDB, siteinfo, Locale.ENGLISH, imageBaseURL, linkBaseURL, imageDirectory);
    }

    /**
     *
     * @param wikiDB
     *          a wiki database to retrieve already cached templates
     * @param locale
     *          a locale for loading language specific resources
     * @param imageBaseURL
     *          a url string which must contains a &quot;${image}&quot; variable
     *          which will be replaced by the image name, to create links to
     *          images.
     * @param linkBaseURL
     *          a url string which must contains a &quot;${title}&quot; variable
     *          which will be replaced by the topic title, to create links to
     *          other wiki topics.
     * @param imageDirectory
     *          a directory for storing downloaded Wikipedia images. The directory
     *          must already exist.
     */
    public DumpWikiModel(WikiDB wikiDB, Siteinfo siteinfo, Locale locale, String imageBaseURL, String linkBaseURL,
            @Nullable File imageDirectory) {
        super(new Configuration(), locale, siteinfo.getNamespace(), imageBaseURL,
                linkBaseURL);
        fWikiDB = wikiDB;
        fSiteinfo = siteinfo;
        fTemplateNamespace = fSiteinfo.getNamespace(INamespace.NamespaceCode.TEMPLATE_NAMESPACE_KEY.code);
        if (imageDirectory != null) {
            if (!imageDirectory.exists()) {
                assert imageDirectory.mkdirs();
            }
        }
        fImageDirectory = imageDirectory;
    }

    /**
     * Get the raw wiki text for the given namespace and article name. This model
     * implementation uses a Derby database to cache downloaded wiki template
     * texts.
     *
     * @param parsedPagename
     *          the parsed template name
     * @param templateParameters
     *          if the namespace is the <b>Template</b> namespace, the current
     *          template parameters are stored as <code>String</code>s in this map
     *
     * @return <code>null</code> if no content was found
     *
     * @see info.bliki.api.User#queryContent(String[])
     */
    @Override
    public String getRawWikiContent(ParsedPageName parsedPagename, Map<String, String> templateParameters) throws WikiModelContentException {
        String result = super.getRawWikiContent(parsedPagename, templateParameters);
        if (result != null) {
            // found magic word template
            return result;
        }
        if (parsedPagename.namespace.isType(NamespaceCode.TEMPLATE_NAMESPACE_KEY)) {
            String name = parsedPagename.pagename;
            if (fSiteinfo.getCharacterCase().equals("first-letter")) {
                // first character as uppercase
                name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            }

            String content = null;
            try {
                TopicData topicData = fWikiDB.selectTopic(fTemplateNamespace + ":" + name);
                if (topicData != null) {
                    content = topicData.getContent();
                    content = getRedirectedWikiContent(content, templateParameters);
                    if (content != null) {
                        return content.length() == 0 ? null : content;
                    } else {
                        return null;
                    }
                }
            } catch (Exception e) {
                if (Configuration.DEBUG) {
                    e.printStackTrace();
                }
                String temp = e.getMessage();
                if (temp != null) {
                    throw new WikiModelContentException("<span class=\"error\">Exception: " + temp + "</span>", e);
                }
                throw new WikiModelContentException("<span class=\"error\">Exception: " + e.getClass().getSimpleName() + "</span>", e);

            }
            return content;
        }
        return null;
    }

    public String getRedirectedWikiContent(String rawWikitext, Map<String, String> templateParameters) {
        if (rawWikitext.length() < 9) {
            // less than "#REDIRECT" string
            return rawWikitext;
        }
        String redirectedLink = WikipediaParser.parseRedirect(rawWikitext, this);
        if (redirectedLink != null) {
            ParsedPageName redirParsedPage = ParsedPageName.parsePageName(this, redirectedLink, fNamespace.getTemplate(), true, true);
            return WikipediaParser.getRedirectedRawContent(this, redirParsedPage, templateParameters);
        }
        return rawWikitext;
    }

    public void appendInternalImageLink(String hrefImageLink, String srcImageLink, ImageFormat imageFormat) {
        try {
            String imageName = imageFormat.getFilename();
            ImageData imageData = fWikiDB.selectImage(imageName);
            if (imageData != null) {
                File file = imageData.getFile();
                if (file.exists()) {
                    super.appendInternalImageLink(hrefImageLink, file.toURI().toString(), imageFormat);
                    return;
                }
            }
            String imageNameURL = Encoder.encodeTitleLocalUrl(imageName);
            super.appendInternalImageLink(hrefImageLink, "file:///" + fImageDirectory + imageNameURL, imageFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appendInternalLink(String topic, String hashSection, String topicDescription, String cssClass, boolean parseRecursive) {
        WPATag aTagNode = new WPATag();
        // append(aTagNode);
        aTagNode.addAttribute("id", "w", true);
        String titleURL = Encoder.encodeTitleLocalUrl(topic);
        String href = titleURL + ".html";
        if (hashSection != null) {
            href = href + '#' + hashSection;
        }
        aTagNode.addAttribute(HREF, href, true);
        if (cssClass != null) {
            aTagNode.addAttribute(CLASS, cssClass, true);
        }
        aTagNode.addObjectAttribute(WIKILINK, topic);
        pushNode(aTagNode);
        WikipediaParser.parseRecursive(topicDescription.trim(), this, false, true);
        popNode();
        // ContentToken text = new ContentToken(topicDescription);
        // aTagNode.addChild(text);
    }

    public void parseInternalImageLink(String imageNamespace, String rawImageLink) {
        String imageSrc = getImageBaseURL();
        if (imageSrc != null) {
            String imageHref = getWikiBaseURL();
            ImageFormat imageFormat = ImageFormat.getImageFormat(rawImageLink, imageNamespace);

            String imageName = imageFormat.getFilename();
            // String sizeStr = imageFormat.getSizeStr();
            // if (sizeStr != null) {
            // imageName = sizeStr + '-' + imageName;
            // }
            // if (imageName.endsWith(".svg")) {
            // imageName += ".png";
            // }
            imageName = Encoder.encodeUrl(imageName);
            // if (replaceColon()) {
            // imageName = imageName.replaceAll(":", "/");
            // }
            if (replaceColon()) {
                imageHref = imageHref.replace("${title}", imageNamespace + '/' + imageName);
                imageSrc = imageSrc.replace("${image}", imageName);
            } else {
                imageHref = imageHref.replace("${title}", imageNamespace + ':' + imageName);
                imageSrc = imageSrc.replace("${image}", imageName);
            }
            appendInternalImageLink(imageHref, imageSrc, imageFormat);
        }
    }

}
