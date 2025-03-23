package local_search_engine.seeker.service;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class SearchService {
    @Autowired
    private FileRepository fileRepository;

    public Page<IndexedFile> searchFiles(String query, Pageable pageable) {
        return fileRepository.searchFiles(query, pageable);
    }

    public boolean openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
                return true;
            }
        } catch (IOException e) {
            log.error("Cannot open the file!");
            e.printStackTrace();
        }
        return false;
    }
}
