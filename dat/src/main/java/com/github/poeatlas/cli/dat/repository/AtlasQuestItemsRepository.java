package com.github.poeatlas.cli.dat.repository;

import com.github.poeatlas.cli.dat.domain.AtlasQuestItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AtlasQuestItemsRepository extends JpaRepository<AtlasQuestItems, Integer> {

  @Query("SELECT a FROM AtlasQuestItems a")
  List<AtlasQuestItems> fetchShaperMaps();
}
