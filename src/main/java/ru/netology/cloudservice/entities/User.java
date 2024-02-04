package ru.netology.cloudservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@NoArgsConstructor
@Builder
@Data
@AllArgsConstructor
@Table(name = "USERS")
public class User implements Serializable {

    private static final long serialVersionUID = 6346239330241570987L;

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String login;
    private String password;
    private String authToken;
    @OneToMany(mappedBy = "user")
    private List<UserFile> files;
}
