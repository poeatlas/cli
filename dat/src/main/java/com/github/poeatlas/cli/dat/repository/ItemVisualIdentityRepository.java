package com.github.poeatlas.cli.dat.repository;

import com.github.poeatlas.cli.dat.domain.ItemVisualIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by NothingSoup on 7/19/17.
 */
public interface ItemVisualIdentityRepository extends JpaRepository<ItemVisualIdentity, Integer> {
}
