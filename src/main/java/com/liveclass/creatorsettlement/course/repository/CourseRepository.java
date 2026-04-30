package com.liveclass.creatorsettlement.course.repository;

import com.liveclass.creatorsettlement.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {}
