package com.github.poeatlas.cli.dat;

import lombok.Builder;
import lombok.Data;

/**
 * Created by blei on 7/18/17.
 */
@Data
@Builder
public class DatMeta {
  private int magicOffset;

  private int tableLength;

  private int tableRows;

  private int tableRowLength;
}

