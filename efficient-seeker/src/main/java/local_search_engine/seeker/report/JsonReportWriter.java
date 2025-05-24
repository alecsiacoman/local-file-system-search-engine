package local_search_engine.seeker.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonReportWriter implements ReportWriter {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void write(IndexReport report, String fileName) {
    try {
      mapper.writeValue(Paths.get("efficient-seeker/reports", fileName).toFile(), report);
    } catch (IOException e) {
      log.error("Couldn't generate json report!");
    }
  }
}
