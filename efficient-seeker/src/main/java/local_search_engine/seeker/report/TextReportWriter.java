package local_search_engine.seeker.report;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextReportWriter implements ReportWriter {
  @Override
  public void write(IndexReport report, String fileName) {
    try (PrintWriter writer =
        new PrintWriter(Files.newBufferedWriter(Paths.get("efficient-seeker/reports", fileName)))) {
      writer.println("Indexing Report");
      writer.println("===================");
      writer.println("Start time: " + report.getStartTime());
      writer.println("End time: " + report.getEndTime());
      writer.println("Duration: " + report.getDuration());
      writer.println("Total: " + report.getTotalFiles());
      writer.println("Indexed: " + report.getIndexedFiles());
      writer.println("Failed: " + report.getFailedFiles());
      writer.println();
      if (!report.getErrorMessages().isEmpty()) {
        writer.println("Errors:");
        report.getErrorMessages().forEach(writer::println);
      }
    } catch (IOException e) {
      log.error("Couldn't write the text report!");
    }
  }
}
