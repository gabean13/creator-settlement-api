package com.liveclass.creatorsettlement.settlement.repository;

import com.liveclass.creatorsettlement.settlement.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {}
