package com.github.poeatlas.cli.dat;

import com.github.poeatlas.cli.dat.domain.AtlasNode;
import com.github.poeatlas.cli.dat.repository.AtlasNodeRepository;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by blei on 7/12/17.
 */
@Component
@Data
@ToString
@Slf4j
public class TestComponent {
  @Autowired
  private AtlasNodeRepository atlasNodeRepository;

  public void test() {
    final List<AtlasNode> list = atlasNodeRepository.findAll();
    log.info("{}", list.toString());
  }
}
