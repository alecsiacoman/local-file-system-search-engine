package local_search_engine.seeker.repository;

import local_search_engine.seeker.model.IndexedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<IndexedFile, UUID> {
    Optional<IndexedFile> findByFilePath(String filePath);

    boolean existsByFilePath(String filePath);

    @Query(value = """
        SELECT * FROM files 
        WHERE tsv @@ plainto_tsquery('english', :query) 
        OR file_name ILIKE %:query%;
    """, nativeQuery = true)
    List<IndexedFile> searchFiles(@Param("query") String keyword);

}

