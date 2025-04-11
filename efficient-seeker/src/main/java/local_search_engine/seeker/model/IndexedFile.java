package local_search_engine.seeker.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class IndexedFile {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String filePath;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private String lastModified;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    private double score;
}

