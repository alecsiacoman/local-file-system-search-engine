package local_search_engine.seeker.repository;

import local_search_engine.seeker.model.IndexedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.UUID;

public interface FileRepository extends JpaRepository<IndexedFile, UUID> {

    @Modifying
    @Query(value = "DELETE FROM files i WHERE i.file_path = :filePath", nativeQuery = true)
    void deleteByFilePath(@Param("filePath") String filePath);

    @Query(value = """
        SELECT * FROM files
        WHERE
            content_tsv @@ plainto_tsquery('english', :query)
            OR file_name_tsv @@ plainto_tsquery('english', :query)
            OR file_name ILIKE %:query%
            OR file_path ILIKE %:query%
            """, nativeQuery = true)
    Page<IndexedFile> searchFiles(@Param("query") String query, Pageable pageable);

    @Query(value = """
    SELECT * FROM files 
    WHERE content_tsv @@ plainto_tsquery('english', :query) 
    OR content ILIKE %:query%
    """, nativeQuery = true)
    Page<IndexedFile> searchByContent(@Param("query") String query, Pageable pageable);

    @Query(value = """
    SELECT * FROM files 
    WHERE file_path ILIKE %:query%
    """, nativeQuery = true)
    Page<IndexedFile> searchByPath(@Param("query") String query, Pageable pageable);

    @Query(value = """
    SELECT * FROM files 
    WHERE file_name_tsv @@ plainto_tsquery('english', :query)
    OR content_tsv @@ plainto_tsquery('english', :query)
    OR file_name ILIKE %:query% 
    OR content ILIKE %:query%
    OR file_path ILIKE %:query%
    """, nativeQuery = true)
    Page<IndexedFile> searchEverywhere(@Param("query") String query, Pageable pageable);



    boolean existsByFilePathAndLastModified(String filePath, String lastModified);

}

