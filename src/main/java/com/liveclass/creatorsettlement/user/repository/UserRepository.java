package com.liveclass.creatorsettlement.user.repository;

import com.liveclass.creatorsettlement.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
