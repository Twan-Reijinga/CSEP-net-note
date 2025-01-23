/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"collection_id", "title"})
})
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(nullable = false)
    public String title;

    @Lob
    @Column(nullable = false)
    public String content;

    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    public Collection collection;

    public Note() {
        // for object mapper
    }

    public Note(String title, String content, Collection collection) {
        this.title = title;
        this.content = content;
        this.collection = collection;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
