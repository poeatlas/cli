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

@Data
@Entity
public class ConnectedWorldAreas {

  @EmbeddedId
  private ConnectedId id;

  @ManyToOne
  @JoinColumn(name = "world_areas_id", insertable = false, updatable = false)
  private WorldAreas worldAreas;

  @ManyToOne
  @JoinColumn(name = "connected_world_areas_id", insertable = false, updatable = false)
  private WorldAreas connWorldAreas;

  @Data
  @EqualsAndHashCode
  @Embeddable
  public static class ConnectedId implements Serializable {

    private static final long serialVersionUID = -9209099242065677107L;

    @Column(name = "world_areas_id", nullable = false)
    private int worldAreasId;

    @Column(name = "connected_world_areas_id", nullable = false)
    private int connectedWorldAreasId;

  }
}
