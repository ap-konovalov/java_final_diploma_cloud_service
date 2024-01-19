package ru.netology.cloudservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.io.Serializable;
import java.sql.Types;

@Entity
@NoArgsConstructor
@Builder
@Data
@AllArgsConstructor
@Table(name = "USER_FILES")
public class UserFile implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String fileName;
    @Lob
    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "bytea")
    private byte[] fileData;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
