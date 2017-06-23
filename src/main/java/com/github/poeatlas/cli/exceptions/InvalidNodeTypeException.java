package com.github.poeatlas.cli.exceptions;

import java.io.IOException;

/**
 * Created by blei on 6/22/17.
 */
public class InvalidNodeTypeException extends IOException {
  private static final long serialVersionUID = -6848270921128074056L;

  public InvalidNodeTypeException(final String message) {
    super(message);
  }
}
