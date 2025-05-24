package local_search_engine.seeker.model;

public class QueryCriteria {
  public String content;
  public String path;

  public QueryCriteria(String query) {
    String[] parts = query.split("\\s+");
    for (String part : parts) {
      if (part.startsWith("content:")) {
        this.content = part.substring(8);
      } else if (part.startsWith("path:")) {
        this.path = part.substring(5);
      }
    }
  }
}
