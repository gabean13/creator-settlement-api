package com.liveclass.creatorsettlement.course;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // CREATOR 유형 User 의 id
  @Column(nullable = false)
  private Long creatorId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private Long price;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Builder
  private Course(Long creatorId, String title, Long price, LocalDateTime createdAt) {
    this.creatorId = creatorId;
    this.title = title;
    this.price = price;
    this.createdAt = createdAt;
  }
}
