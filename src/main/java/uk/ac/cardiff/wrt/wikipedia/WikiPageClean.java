/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cardiff.wrt.wikipedia;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;


public class WikiPageClean {

    String content; 
    
    public WikiPageClean(String content) {
      this.content = content; 
    }
    
    public String clean() {
         
        content = removeRefs(content);
        content = removeInterWikiLinks(content);
        content = removeParentheticals(content);
        content = fixUnitConversion(content);
        content = ImageCaptionsRemover.remove(content);
        content = DoubleBracesRemover.remove(content);
        content = removeHtmlComments(content);
        content = removeEmphasis(content);
        content = removeHeadings(content);
        content = removeCategoryLinks(content);
        content = removeLinks(content);
        content = removeMath(content);
        content = removeGallery(content);
        content = removeNoToc(content);
        content = removeIndentation(content);
        content = TableRemover.remove(content);

        // For some reason, some HTML entities are doubly encoded.
        content = StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeHtml4(content));
        content = removeHtmlTags(content);
   
         // Finally, fold multiple newlines.
         content = compressMultipleNewlines(content);

       return content.trim();
  }


  private static final Pattern BR = Pattern.compile("&lt;br */&gt;");
  private static final Pattern REF1 = Pattern.compile("&lt;ref[^/]+/&gt;", Pattern.DOTALL);
  private static final Pattern REF2 = Pattern.compile("&lt;ref.*?&lt;/ref&gt;", Pattern.DOTALL);

  private String removeRefs(String s) {
    s = BR.matcher(s).replaceAll(""); // See test case for why we do this.
    s = REF1.matcher(s).replaceAll("");
    s = REF2.matcher(s).replaceAll("");
    return s;
  }
    
    
  // Note that WiktionaryLinks have the form [[wikt:anarchism|anarchism]], which is easily confused
  // with
  // inter-wikilinks. The distinguishing characteristic is the lack of pipe (|).
  private static final Pattern INTER_WIKI_LINKS = Pattern.compile("\\[\\[[a-z\\-]+:[^|\\]]+\\]\\]");

  private String removeInterWikiLinks(String s) {
    return INTER_WIKI_LINKS.matcher(s).replaceAll(" ");
  }
    

  // IPA parenthetical may be enclosed either with parentheses or brackets (de articles).
  private static final Pattern IPA1 = Pattern.compile(" (\\(|\\[)\\{\\{IPA[^\\}]+\\}\\}(\\)|\\])");
  private static final Pattern IPA2 = Pattern.compile(" \\{\\{IPA[^\\}]+\\}\\}");

  private String removeParentheticals(String s) {
    // Take care of things like: id 36
    // '''Albedo''' ({{IPAc-en|icon|æ|l|ˈ|b|iː|d|oʊ}}), or ''reflection coefficient'' ...
    //
    // Note that we shouldn't just leave to the double-curly remover, since that would leave
    // the dangling empty parens.
    s = IPA1.matcher(s).replaceAll("");

    // Straight-up IPA, with no parenthetical.
    s = IPA2.matcher(s).replaceAll("");

    return s;
  }
  
    private static final Pattern UNIT_CONVERSION1 =
         Pattern.compile("\\{\\{convert\\|(\\d+)\\|([^|]+)\\}\\}");
    private static final Pattern UNIT_CONVERSION2 =
      Pattern.compile("\\{\\{convert\\|(\\d+)\\|([^|]+)\\|[^}]+\\}\\}");
  private String fixUnitConversion(String s) {
    String t = UNIT_CONVERSION1.matcher(s).replaceAll("$1 $2");
    return UNIT_CONVERSION2.matcher(t).replaceAll("$1 $2");
  }


  private static final class ImageCaptionsRemover {
    private static final int DEFAULT_NO_BRACKET = 0;
    private static final int STATE_1CLOSE_BRACKET = 1;
    private static final int STATE_1OPEN_BRACKET = 2;

    private static String remove(String s) {
      String[] labels = { "[[File:", "[[Image:", "[[Datei" // We see this in de wikipedia.
      };
      for (String label : labels) {
        s = removeLabel(s, label);
      }
      return s;
    }

    // This method encodes a finite state machine to handle links in caption, which result in
    // nested [[ ... [[foo]] ... ]] constructs.
    private static String removeLabel(String s, String label) {
      int i = s.indexOf(label);
      while (i != -1) {
        int state = DEFAULT_NO_BRACKET;
        int level = 1;
        int cur = i + label.length();

        while (cur < s.length()) {
          if (state == STATE_1OPEN_BRACKET && s.charAt(cur) == '[') {
            level++;
            state = DEFAULT_NO_BRACKET;
          }
          // If there's only one close, move back to default state.
          if (state == STATE_1OPEN_BRACKET) {
            state = DEFAULT_NO_BRACKET;
          }
          if (s.charAt(cur) == '[') {
            state = STATE_1OPEN_BRACKET;
          }

          if (state == STATE_1CLOSE_BRACKET && s.charAt(cur) == ']') {
            level--;
            if (level == 0) {
              break;
            }
            state = DEFAULT_NO_BRACKET;
          } else {
            // If there's only one close, move back to default state.
            if (state == STATE_1CLOSE_BRACKET) {
              state = DEFAULT_NO_BRACKET;
            }
            if (s.charAt(cur) == ']') {
              state = STATE_1CLOSE_BRACKET;
            }
          }
          cur++;
        }

        if (cur == s.length()) {
          return s.substring(0, i);
        }

        s = s.substring(0, i) + s.substring(cur + 1, s.length());
        i = s.indexOf(label, i);
      }

      return s;
    }
  }
  
  private static final class DoubleBracesRemover {
    private static final int DEFAULT_NO_BRACE = 0;
    private static final int STATE_1CLOSE_BRACE = 1;
    private static final int STATE_1OPEN_BRACE = 2;

    // This method encodes a finite state machine to handle nested double braces (e.g., in
    // infoboxes).
    private static String remove(String s) {
      int i = s.indexOf("{{");
      while (i != -1) {
        int state = DEFAULT_NO_BRACE;
        int level = 1;
        int cur = i + 2;

        while (cur < s.length()) {
          if (state == STATE_1OPEN_BRACE && s.charAt(cur) == '{') {
            level++;
            state = DEFAULT_NO_BRACE;
          }
          // If there's only one close, move back to default state.
          if (state == STATE_1OPEN_BRACE) {
            state = DEFAULT_NO_BRACE;
          }
          if (s.charAt(cur) == '{') {
            state = STATE_1OPEN_BRACE;
          }

          if (state == STATE_1CLOSE_BRACE && s.charAt(cur) == '}') {
            level--;
            if (level == 0) {
              break;
            }
            state = DEFAULT_NO_BRACE;
          } else {
            // If there's only one close, move back to default state.
            if (state == STATE_1CLOSE_BRACE) {
              state = DEFAULT_NO_BRACE;
            }
            if (s.charAt(cur) == '}') {
              state = STATE_1CLOSE_BRACE;
            }
          }
          cur++;
        }

        if (cur == s.length()) {
          return s.substring(0, i);
        }

        s = s.substring(0, i) + s.substring(cur + 1, s.length());
        i = s.indexOf("{{", i);
      }

      return s;
    }
  }


  private static final Pattern HTML_COMMENT = Pattern.compile(
      "(<|&lt;|&#60;)!--.*?--(>|&gt;|&#62;)", Pattern.DOTALL);

  private String removeHtmlComments(String s) {
    return HTML_COMMENT.matcher(s).replaceAll("");
  }


  
  private static final Pattern EMPHASIS = Pattern.compile("('''|'')");

  private String removeEmphasis(String s) {
    return EMPHASIS.matcher(s).replaceAll("");
  }

  private static final Pattern HEADINGS = Pattern.compile("=+\\s?(.*?)=+");

  private String removeHeadings(String s) {
    // Make sure there's an extra newline after headings.
    return HEADINGS.matcher(s).replaceAll("$1\n");
  }
  
  private static final Pattern CATEGORY_LINKS_EN = Pattern
      .compile("\\[\\[Category:([^\\]]+)\\]\\]");

  private String removeCategoryLinks(String s) {
      return CATEGORY_LINKS_EN.matcher(s).replaceAll("");
  }

  
  private static final Pattern LINKS1 = Pattern.compile("\\[\\[[^\\]]+\\|([^\\]]+)\\]\\]");
  private static final Pattern LINKS2 = Pattern.compile("(\\[\\[|\\]\\])");

  private String removeLinks(String s) {
    return LINKS2.matcher(LINKS1.matcher(s).replaceAll("$1")).replaceAll("");
  }
  
  private static final Pattern MATH = Pattern.compile("&lt;math&gt;.*?&lt;/math&gt;",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private String removeMath(String s) {
    return MATH.matcher(s).replaceAll("");
  }

  private static final Pattern GALLERY = Pattern.compile("&lt;gallery&gt;.*?&lt;/gallery&gt;",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private String removeGallery(String s) {
    return GALLERY.matcher(s).replaceAll("");
  }
  
  private static final Pattern NO_TOC = Pattern.compile("__NOTOC__");

  private String removeNoToc(String s) {
    return NO_TOC.matcher(s).replaceAll("");
  }
  
  
  private static final Pattern INDENTATION = Pattern.compile("[\\n\\r]:\\s*");

  private String removeIndentation(String s) {
    return INDENTATION.matcher(s).replaceAll("\n");
  }

  
  
  private static final class TableRemover {
    private static final int DEFAULT = 0;
    private static final int STATE_PIPE = 1;
    private static final int STATE_1OPEN_BRACE = 2;

    private static String remove(String s) {
      int i = s.indexOf("{|");
      while (i != -1) {
        int state = DEFAULT;
        int level = 1;
        int cur = i + 2;

        while (cur < s.length()) {
          if (state == STATE_1OPEN_BRACE && s.charAt(cur) == '|') {
            level++;
            state = DEFAULT;
          }
          // If there's only one close, move back to default state.
          if (state == STATE_1OPEN_BRACE) {
            state = DEFAULT;
          }
          if (s.charAt(cur) == '{') {
            state = STATE_1OPEN_BRACE;
          }

          if (state == STATE_PIPE && s.charAt(cur) == '}') {
            level--;
            if (level == 0) {
              break;
            }
            state = DEFAULT;
          } else {
            // If there's a pipe but no close brace, move back to default state.
            if (state == STATE_PIPE) {
              state = DEFAULT;
            }
            if (s.charAt(cur) == '|') {
              state = STATE_PIPE;
            }
          }
          cur++;
        }

        if (cur == s.length()) {
          return s.substring(0, i);
        }

        s = s.substring(0, i) + s.substring(cur + 1, s.length());
        i = s.indexOf("{|", i);
      }

      return s;
    }
  }

  
  private static final Pattern HTML_TAGS = Pattern.compile("<[^>]+>");
  private String removeHtmlTags(String s) {
    return HTML_TAGS.matcher(s).replaceAll("");
  }


  private static final Pattern MULTIPLE_NEWLINES = Pattern.compile("[\\n\\r][\\n\\r]+");

  private String compressMultipleNewlines(String s) {
    return MULTIPLE_NEWLINES.matcher(s).replaceAll("\n\n");
  }

  private static final Pattern FOOTER_EN1 = Pattern.compile("==\\s*See also\\s*==.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER_EN2 = Pattern.compile("==\\s*References\\s*==.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER_EN3 = Pattern.compile("==\\s*Further reading\\s*==.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER_EN4 = Pattern.compile("==\\s*External Links\\s*==.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER_EN5 = Pattern.compile("==\\s*Related pages\\s*==.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private String removeFooter(String s) {
      s = FOOTER_EN1.matcher(s).replaceAll("");
      s = FOOTER_EN2.matcher(s).replaceAll("");
      s = FOOTER_EN3.matcher(s).replaceAll("");
      s = FOOTER_EN4.matcher(s).replaceAll("");
      s = FOOTER_EN5.matcher(s).replaceAll("");
      return s;
  }


}
