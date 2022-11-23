package de.caylak.babackend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class Module {

    @Id
    @GeneratedValue
    private String id;

    private String name;
    private double ects;
}
