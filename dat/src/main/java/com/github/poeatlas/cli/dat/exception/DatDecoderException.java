package com.github.poeatlas.cli.dat.exception;

/**
 * Created by blei on 7/18/17.
 */
public class DatDecoderException extends RuntimeException {
  private static final long serialVersionUID = 8459838661153202270L;

  /**
   * Constructs a new runtime exception with the specified detail message.
   * The cause is not initialized, and may subsequently be initialized by a
   * call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the {@link
   *                #getMessage()} method.
   */
  public DatDecoderException(String message) {
    super(message);
  }
}
