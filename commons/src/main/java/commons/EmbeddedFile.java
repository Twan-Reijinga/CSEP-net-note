package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

@Entity
@Table(name = "files")
public class EmbeddedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(nullable = false)
    public String title;

    @Lob
    @Column()
    public String file;

    @ManyToOne
    @JoinColumn(name = "note", nullable = false)
    public Note note;

    /**
     * Default constructor used to create a NoteTitle object from a JSON file.
     * When used, idn noteId and title still need to be specified later.
     */
    public EmbeddedFile() {}

    /**
     * Constructor for the NoteTitle that initializes all the private fields of the object.
     * @param title The title of the file stored in the database, could be edited later.
     *
     */
    public EmbeddedFile(String title, Note note, String file) {
        this.title = title;
        this.note = note;
        this.file = file;
    }

    /**
     * Equals method for the file
     * Title and id need to be the same to be equal.
     * @param o Other object to compare to.
     * @return Boolean that indicates if Object o is equal to this NoteTitle object.
     */
    @Override
    public boolean equals(Object o) {
        return true;
    }

    /**
     * generated hash base on the title and id of a specified NoteTitle object.
     * @return hash for title and id of object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, id);
    }

    /**
     * To string method for the NoteTitle using a multi line style.
     * @return The object as a human readably string.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(
                this,
                ToStringStyle.MULTI_LINE_STYLE);
    }
}

