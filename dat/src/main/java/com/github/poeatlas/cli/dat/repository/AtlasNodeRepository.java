package com.github.poeatlas.cli.dat.repository;

import com.github.poeatlas.cli.dat.domain.AtlasNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by blei on 7/12/17.
 */
public interface AtlasNodeRepository extends JpaRepository<AtlasNode, Integer> {

  @Query("SELECT an.defaultItemVisualIdentityKey.ddsFile FROM AtlasNode an")
  List<String> findDdsImagePath();

  // List<AtlasNode> findCoordinate();


}
