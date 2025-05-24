package local_search_engine.seeker.report;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReportWriterFactory {

  @Value("${report.format}")
  private String format;

  public ReportWriter getReportWriter() {
    return switch (format.toLowerCase()) {
      case "json" -> new JsonReportWriter();
      default -> new TextReportWriter();
    };
  }
}
