package com.codemaster.demo.repository;

import com.codemaster.demo.model.RankingSemanal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RankingSemanalRepository
        extends JpaRepository<RankingSemanal, UUID> {

}
