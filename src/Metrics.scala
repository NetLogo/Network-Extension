package org.nlogo.extensions.network

import org.nlogo.api.{ LogoList, LogoListBuilder }
import org.nlogo.agent.{ LinkManager, Turtle, AgentSet }

object Metrics {

  /**
   * This method performs a BFS from the sourceNode, following the network imposed by the given
   * linkBreed, going up to radius layers out, and only collecting nodes that are members of
   * sourceSet.
   * 
   * Note: this method follows directed links both directions.  But we could change its
   * functionality when dealing with directed links -- I'm not sure what the right thing is.
   * ~Forrest (5/11/2007)
   */
  def inLinkRadius(sourceNode: Turtle, sourceSet: AgentSet, radius: Double, linkBreed: AgentSet): collection.Set[Turtle] = {
    val linkManager = sourceNode.world.linkManager
    val seen = collection.mutable.Set[Turtle]()
    val visited = collection.mutable.Set[Turtle]()
    val queue = collection.mutable.Queue[Option[Turtle]]()
    queue += Some(sourceNode)
    seen += sourceNode
    // we use None to mark radius-layer boundaries
    queue += None
    var layer = 0
    var done = false
    while (!done && layer <= radius) {
      val curNode = queue.dequeue()
      if (curNode == None && queue.isEmpty)
        done = true
      else if(curNode == None) {
        layer += 1
        queue += None
      }
      else {
        visited.add(curNode.get)
        val neighborSet = linkManager.findLinkedWith(curNode.get, linkBreed)
        val it = neighborSet.iterator()
        while(it.hasNext) {
          val toAdd = it.next().asInstanceOf[Turtle]
          if (!seen(toAdd)) {
            seen += toAdd
            queue += Some(toAdd)
          }
        }
      }
    }
    queue.clear()
    seen.clear()
    val result = collection.mutable.Set[Turtle]()
    // filter, so we only have agents from sourceSet
    for (node <- visited)
      if (sourceSet.contains(node))
        result += node
    result
  }

  /**
   * This method performs a BFS from the sourceNode, following the network imposed by the given
   * linkBreed, to find the distance to destNode.  Directed links are only followed in the "forward"
   * direction.  It returns -1 if there is no path between the two nodes.  ~Forrest (5/11/2007)
   */
  def networkDistance(sourceNode: Turtle, destNode: Turtle, linkBreed: AgentSet): Int = {
    val linkManager = sourceNode.world.linkManager
    val isDirectedBreed = linkBreed.isDirected
    val seen = collection.mutable.HashSet[Turtle]()
    val queue = collection.mutable.Queue[Option[Turtle]]()
    queue += Some(sourceNode)
    seen += sourceNode
    // we use None to mark radius-layer boundaries
    queue += None
    var layer = 0
    var done = false
    while (!done) {
      val curNode = queue.dequeue()
      if (curNode == None && queue.isEmpty)
        done = true
      else if(curNode == None) {
        layer += 1
        queue += None
      }
      else {
        if (curNode eq destNode)
          return layer
        val neighborSet =
          if (isDirectedBreed)
            linkManager.findLinkedFrom(curNode.get, linkBreed)
          else
            linkManager.findLinkedWith(curNode.get, linkBreed)
        val it = neighborSet.iterator
        while(it.hasNext) {
          val toAdd = it.next().asInstanceOf[Turtle]
          if (!seen(toAdd)) {
            seen += toAdd
            queue += Some(toAdd)
          }
        }
      }
    }
    -1
  }

  /**
   * This method performs a BFS from the sourceNode, following the network imposed by the given
   * linkBreed, to find the shortest path to destNode.  Directed links are only followed in the
   * "forward" direction.
   * 
   * It returns an empty list if there is no path between the two nodes.  The BFS proceeds in a
   * random order, so if there are multiple shortest paths, a random one will be returned.  Note,
   * however, that the probability distribution of this random choice is subtly different from if we
   * had enumerated *all* shortest paths, and chose one of them uniformly at random.  I don't think
   * there is an efficient way to implement it that other way.  ~Forrest (5/11/2007)
   */
  def pathNodes(random: org.nlogo.util.MersenneTwisterFast, sourceNode: Turtle,
                destNode: Turtle, linkBreed: AgentSet): LogoList = {
    val linkManager = sourceNode.world.linkManager
    val path = new LogoListBuilder
    if (sourceNode eq destNode) {
      path.add(sourceNode)
      path.toLogoList
    }
    val isDirectedBreed = linkBreed.isDirected
    // we use this HashMap to track which nodes have been seen by the BFS, as well as who their
    // "parents" are, so we can walk the path back to the source.
    val seenParents = collection.mutable.HashMap[Turtle, Option[Turtle]]()
    val queue = collection.mutable.Queue[Turtle]()
    queue += sourceNode
    seenParents(sourceNode) = None
    while (queue.nonEmpty) {
      val curNode = queue.dequeue()
      val neighborSet =
        if (isDirectedBreed)
          linkManager.findLinkedFrom(curNode, linkBreed)
        else
        linkManager.findLinkedWith(curNode, linkBreed)
      val it = neighborSet.shufflerator(random)
      while(it.hasNext) {
        val toAdd = it.next().asInstanceOf[Turtle]
        if (toAdd eq destNode) {
          path.add(destNode)
          var agt = curNode
          while (agt != null) {
            path.add(agt)
            agt = seenParents.get(agt).get.orNull
          }
          return path.toLogoList
        }
        if (!seenParents.contains(toAdd)) {
          seenParents(toAdd) = Some(curNode)
          queue += toAdd
        }
      }
    }
    LogoList.Empty
  }

  def pathLinks(random: org.nlogo.util.MersenneTwisterFast,
                sourceNode: Turtle, destNode: Turtle, linkBreed: AgentSet): LogoList = {
    val linkManager = sourceNode.world.linkManager
    val nodes = pathNodes(random, sourceNode, destNode, linkBreed)
    val links = new LogoListBuilder
    if (nodes.size <= 1)
      LogoList.Empty
    else {
      val it = nodes.iterator
      var t1 = it.next().asInstanceOf[Turtle]
      while (it.hasNext) {
        val t2 = it.next().asInstanceOf[Turtle]
        links.add(linkManager.findLink(t1, t2, linkBreed, true))
        t1 = t2
      }
      links.toLogoList
    }
  }

  /**
   * Calculates the mean shortest-path length between all (distinct) pairs of nodes in the given
   * nodeSet, by traveling along links of the given linkBreed.
   * 
   * It returns -1 if any two nodes in nodeSet are not connected by a path.
   * 
   * Note: this method follows directed links both directions.  But we could change its
   * functionality when dealing with directed links -- I'm not sure what the right thing is.  Seems
   * like often the mean path length (when only following links "forward") in a directed-graph would
   * be undefined.
   * 
   * ~Forrest (5/11/2007)
   */
  def meanPathLength(nodeSet: AgentSet, linkBreed: AgentSet): Double = {
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

}
