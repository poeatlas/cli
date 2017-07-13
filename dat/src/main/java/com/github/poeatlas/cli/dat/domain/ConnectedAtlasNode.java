package com.github.poeatlas.cli.dat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by NothingSoup on 7/12/17.
 */
@Data
@Entity
public class ConnectedAtlasNode {

  @EmbeddedId
  private ConnectedId id;

  @ManyToOne
  @JoinColumn(name = "atlas_node_id", insertable = false, updatable = false)
  private AtlasNode atlasNode;

  @ManyToOne
  @JoinColumn(name = "connected_atlas_node_id", insertable = false, updatable = false)
  private AtlasNode connAtlasNode;

  @Data
  @EqualsAndHashCode
  @Embeddable
  public static class ConnectedId implements Serializable {

    private static final long serialVersionUID = 2932417778185540154L;

    @Column(name = "atlas_node_id", nullable = false)
    private int atlasNodeId;

    @Column(name = "connected_atlas_node_id", nullable = false)
    private int connectedAtlasNodeId;

  }
}
