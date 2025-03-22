package local_search_engine.seeker.repository;

import local_search_engine.seeker.model.IndexedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<IndexedFile, UUID> {
    @Query(value = "SELECT f FROM files f WHERE to_tsvector('english', content) @@ to_tsquery(:query)", nativeQuery = true)
    List<IndexedFile> searchFiles(@Param("query") String query);

}

