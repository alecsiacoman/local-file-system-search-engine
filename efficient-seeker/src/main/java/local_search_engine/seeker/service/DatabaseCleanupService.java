package local_search_engine.seeker.service;

import local_search_engine.seeker.repository.FileRepository;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseCleanupService implements DisposableBean {

  @Autowired private FileRepository fileRepository;

  @Override
  @Transactional
  public void destroy() {
    try {
      fileRepository.deleteAll();
      System.out.println("Database cleared.");
    } catch (Exception e) {
      System.err.println("Error while clearing the database: " + e.getMessage());
    }
  }
}
