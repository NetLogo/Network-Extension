package org.nlogo.extensions.network

import org.nlogo.api.{ LogoList, LogoListBuilder }
import org.nlogo.agent.{ LinkManager, Agent, Turtle, Link, AgentSet, ArrayAgentSet }
import org.nlogo.util.{ MersenneTwisterFast => Random }

object Metrics {

  // make agentset iterators easier to use in Scala
  private def asScala[T <: Agent](it: AgentSet.Iterator): Iterator[T] =
    new Iterator[T] {
      def hasNext = it.hasNext
      def next() = it.next().asInstanceOf[T]
    }

  // all of the primitives use this to traverse the network in breadth-first order.
  // each List[Turtle] is a reversed path (destination is head); the paths share
  // storage, so total memory usage stays within O(n).
  private def breadthFirstSearch(start: Turtle, links: AgentSet): Stream[List[Turtle]] = {
    val seen: Turtle => Boolean = {
      val memory = collection.mutable.HashSet[Turtle](start)
      t => memory(t) || { memory += t; false }
    }
    val neighborFinder: Turtle => AgentSet = {
      val linkManager = start.world.linkManager
      if (links.isDirected)
        linkManager.findLinkedFrom(_, links)
      else
        linkManager.findLinkedWith(_, links)
    }
    def neighbors(node: Turtle): Iterator[Turtle] =
      asScala(neighborFinder(node).iterator)
        .filterNot(seen)
    def nextLayer(layer: Stream[List[Turtle]]) =
      for {
        path <- layer
        neighbor <- neighbors(path.head)
      } yield neighbor :: path
    Stream.iterate(Stream(List(start)))(nextLayer)
      .takeWhile(_.nonEmpty)
      .flatten
  }
    
  def linkDistance(start: Turtle, end: Turtle, links: AgentSet): Option[Int] =
    breadthFirstSearch(start, links)
      .find(_.head eq end)
      .map(_.size - 1)

  def extendedLinkNeighbors(start: Turtle, radius: Double, links: AgentSet): AgentSet = {
    val resultArray =
      breadthFirstSearch(start, links)
        .takeWhile(_.tail.size <= radius)
        .map(_.head)
        .toArray[Agent]
    new ArrayAgentSet(classOf[Turtle], resultArray, start.world)
  }

  def pathTurtles(random: Random, start: Turtle, end: Turtle, links: AgentSet): LogoList =
    breadthFirstSearch(start, links)
      .find(_.head eq end)
      .map(path => LogoList.fromIterator(path.reverseIterator))
      .getOrElse(LogoList.Empty)

  def pathLinks(random: Random, start: Turtle, end: Turtle, links: AgentSet): LogoList = {
    val linkManager = start.world.linkManager
    def turtlesToLinks(turtles: List[Turtle]): Iterator[Link] =
      for((end1, end2) <- turtles.iterator zip turtles.tail.iterator)
      yield linkManager.findLink(end1, end2, links, true)
    breadthFirstSearch(start, links)
      .find(_.head eq end)
      .map(path => LogoList.fromIterator(turtlesToLinks(path.reverse)))
      .getOrElse(LogoList.Empty)
  }

  def meanPathLength(nodeSet: AgentSet, linkBreed: AgentSet): Option[Double] =
    None
/*
    var linkManager: LinkManager = null
    val seen = collection.mutable.HashSet[Turtle]()
    val queue = collection.mutable.Queue[Option[Turtle]]()
    var totalSum = 0L
    val it2 = nodeSet.iterator
    while(it2.hasNext) {
      val agt = it2.next().asInstanceOf[Turtle]
      var nodeSetVisitedCount = 0
      seen.clear();
      seen += agt
      queue += Some(agt)
      // we use None to mark radius-layer boundaries
      queue += None
      var layer = 0
      var done = false
      while (true) {
        val curNode = queue.dequeue()
        if (curNode == None && queue.isEmpty)
          done = true
        else if (curNode == None) {
          layer += 1
          queue += None
        }
        else {
          if (nodeSet.contains(curNode.get)) {
            totalSum += layer
            nodeSetVisitedCount += 1
          }
          if(linkManager == null)
            linkManager = curNode.get.world.linkManager
          val neighborSet = linkManager.findLinkedWith(curNode.get, linkBreed)
          val it = neighborSet.iterator
          while(it.hasNext) {
            val toAdd = it.next().asInstanceOf[Turtle]
            if (!seen(toAdd)) {
              seen += toAdd
              queue += Some(toAdd)
            }
          }
        }
        if (nodeSetVisitedCount != nodeSet.count)
          return -1.0
      }
    }
    val nodeCount = nodeSet.count
    if (nodeCount == 1)
      0
    else
      totalSum.toDouble / (nodeCount * (nodeCount - 1))
  }
*/

}
