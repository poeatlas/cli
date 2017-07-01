package com.github.poeatlas.cli.ggpk;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by NothingSoup on 6/30/17.
 */
public class NodeQueue {
  private static Queue<DataNode> queue = new LinkedList<>();

  private NodeQueue() {
    // do not instantiate
  }

  public static final boolean add(final DataNode node) {
    return queue.add(node);
  }

  public static final DataNode element() {
    return queue.element();
  }

  public static final boolean offer(final DataNode node) {
    return queue.offer(node);
  }

  public static final DataNode peek() {
    return queue.peek();
  }

  public static final DataNode poll() {
    return queue.poll();
  }

  public static final DataNode remove() {
    return queue.remove();
  }

  public static final boolean isEmpty() {
    return queue.isEmpty();
  }

  public static final boolean addAll(final List<DataNode> list) {
    return queue.addAll(list);
  }
}
