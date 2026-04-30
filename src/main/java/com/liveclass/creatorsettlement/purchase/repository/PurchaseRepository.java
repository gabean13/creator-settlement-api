package com.liveclass.creatorsettlement.purchase.repository;

import com.liveclass.creatorsettlement.purchase.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {}
