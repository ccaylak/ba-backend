package de.caylak.babackend.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@IdClass(CourseId.class)
public class Course implements Serializable {

    @Id
    private String name;

    @Id
    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;
}
