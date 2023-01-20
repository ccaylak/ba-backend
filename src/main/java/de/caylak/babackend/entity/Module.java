package de.caylak.babackend.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Module {

    @Id
    @GeneratedValue
    private long id;

    private String moduleNumber;

    @EqualsAndHashCode.Include
    private String name;
    @EqualsAndHashCode.Include
    private String ects;
    @EqualsAndHashCode.Include
    private String grade;

    private String ackGrade;

    @ManyToMany(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinTable(
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "course_name"),
                    @JoinColumn(name = "university_id")
            }
    )
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinTable(
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "equivalent_module_id")
    )
    private List<Module> equivalentModules = new ArrayList<>();

    @ManyToMany(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinTable(
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "non_equivalent_module_id")
    )
    private List<Module> nonEquivalentModules = new ArrayList<>();

    @ManyToMany(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinTable(
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "unknown_equivalent_module_id")
    )
    private List<Module> unknownEquivalentModules = new ArrayList<>();

    public Module(String moduleNumber, String name, String ects, String grade, String ackGrade, Course course) {
        this.moduleNumber = moduleNumber;
        this.name = name;
        this.ects = ects;
        this.grade = grade;
        this.ackGrade = ackGrade;
        courses.add(course);
    }
}
