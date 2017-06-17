package com.github.poeatlas.cli;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  public static void main(final String[] args) {
    new Main().hello();
  }

  private void hello() {
    log.info("Hello, world");
  }
}
