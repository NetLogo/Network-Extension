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

  def inLinkRadius(sourceSet: AgentSet, start: Turtle, radius: Double, links: AgentSet): AgentSet = {
    val resultArray =
      breadthFirstSearch(start, links)
        .takeWhile(_.tail.size <= radius)
        .map(_.head)
        .filter(sourceSet.contains)
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

  def meanPathLength(nodeSet: AgentSet, linkBreed: AgentSet): Option[Double] = {
    val count = nodeSet.count
    if(count == 0)
      None
    else {
      val paths =
        for {
          start <- asScala(nodeSet.iterator).toIndexedSeq[Turtle]
          path <- breadthFirstSearch(start, linkBreed)
          if path.tail.nonEmpty && nodeSet.contains(path.head)
        } yield path
      val size = paths.size
      if(size != count * (count - 1))
        None
      else
        Some(paths.map(_.length - 1).sum.toDouble / size)
    }
  }

}
