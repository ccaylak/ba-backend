package de.caylak.babackend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class University {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @OneToMany(mappedBy = "university")
    private List<Course> courseList;
}
