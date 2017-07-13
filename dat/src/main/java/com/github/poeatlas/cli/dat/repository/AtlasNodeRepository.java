package com.github.poeatlas.cli.dat.repository;

import com.github.poeatlas.cli.dat.domain.AtlasNode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by blei on 7/12/17.
 */
public interface AtlasNodeRepository extends JpaRepository<AtlasNode, Long> {
}
