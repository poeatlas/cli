package com.github.poeatlas.cli.utils;

import com.github.poeatlas.cli.ggpk.DataNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by NothingSoup on 6/30/17.
 */
public class NodeQueue {
  private static Queue<DataNode> queue = new LinkedList<>();

  private NodeQueue() {
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

  public static final boolean addAll(final NodeQueue queue) {
    return queue.addAll(queue);
  }
}
