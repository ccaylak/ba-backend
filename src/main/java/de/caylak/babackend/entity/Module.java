package de.caylak.babackend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

@Entity
@NoArgsConstructor
@Data
public class Module {

    @Id
    @GeneratedValue
    private long id;

    private String moduleNumber;
    private String name;
    private String ects;

    private String grade;
    private String ackGrade;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinColumn(name = "university_id")
    private University university;

    @ManyToMany(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinTable(
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "equivalent_module_id")
    )
    private List<Module> equivalentModules = new ArrayList<>();

    public Module(String moduleNumber, String name, String ects, University university) {
        this.moduleNumber = moduleNumber;
        this.name = name;
        this.ects = ects;
        this.university = university;
    }

    public Module(String name, String ects, String grade, String ackGrade, University university) {
        this.name = name;
        this.ects = ects;
        this.grade = grade;
        this.ackGrade = ackGrade;
        this.university = university;
    }
}
