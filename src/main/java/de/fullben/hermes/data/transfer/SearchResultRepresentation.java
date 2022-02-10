package de.fullben.hermes.data.transfer;

/**
 * Represents the core data of a single result item of a web search.
 *
 * @author Benedikt Full
 */
public class SearchResultRepresentation {

  private String title;
  private String snippet;
  private String url;
  private String pageHierarchy;

  public SearchResultRepresentation() {
    title = null;
    snippet = null;
    url = null;
    pageHierarchy = null;
  }

  public SearchResultRepresentation(SearchResultRepresentation other) {
    this.title = other.title;
    this.snippet = other.snippet;
    this.url = other.url;
    this.pageHierarchy = other.pageHierarchy;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSnippet() {
    return snippet;
  }

  public void setSnippet(String snippet) {
    this.snippet = snippet;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPageHierarchy() {
    return pageHierarchy;
  }

  public void setPageHierarchy(String pageHierarchy) {
    this.pageHierarchy = pageHierarchy;
  }
}
