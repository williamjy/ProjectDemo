package com.nuggets.valueeats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Data
public class User {
    @JsonIgnore
    @Id
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(unique = true)
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
    private String alias;
    private String address;
    @JsonIgnore
    private String token;
    private String profilePic;
}
