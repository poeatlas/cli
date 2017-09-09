package com.github.poeatlas.cli.dat.repository;

import com.github.poeatlas.cli.dat.domain.WorldAreas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by NothingSoup on 7/14/17.
 */
public interface WorldAreasRepository extends JpaRepository<WorldAreas, Integer> {

  @Query("SELECT w FROM WorldAreas w WHERE w.areaKey LIKE '%tier%'")
  List<WorldAreas> getTierList();
}
