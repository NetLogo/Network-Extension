package org.nlogo.extensions.network

import org.nlogo.api.{
  DefaultClassManager, PrimitiveManager, AgentSet, Argument, Context, DefaultReporter,
  ExtensionException, I18N, Primitive, Syntax, Turtle }
import org.nlogo.nvm.ArgumentTypeException

class NetworkExtension extends DefaultClassManager {

  override def load(primManager: PrimitiveManager) {
    primManager.addPrimitive("in-link-radius", new InLinkRadius)
    primManager.addPrimitive("link-distance", new LinkDistance)
    primManager.addPrimitive("mean-path-length", new MeanPathLength)
    primManager.addPrimitive("path-links", new PathLinks)
    primManager.addPrimitive("path-turtles", new PathTurtles)
  }

  /// shared error messages

  private def requireTurtleset(context: Context, agents: AgentSet) {
    if(!classOf[Turtle].isAssignableFrom(agents.`type`))
      throw new ArgumentTypeException(
        context.nvmContext, this, 0, Syntax.TurtlesetType, agents)
  }

  private def requireLinkBreed(context: Context, agents: AgentSet) {
    val world = context.getAgent.world
    if (linkBreed ne world.links && !world.isLinkBreed(linkBreed))
      throw new ExtensionException(
        I18N.errors.get("org.nlogo.prim.etc.$common.expectedLastInputToBeLinkBreed"))
  }

  private def requireAlive(context: Context, agent: Agent) {
    if (agent.id == -1)
      throw new ExtensionException(
        context, I18N.errors.get("org.nlogo.$common.thatAgentIsDead",
                                 agent.classDisplayName))
  }

  /// primitives

  class InLinkRadius extends DefaultReporter {
    override def getSyntax =
      Syntax.reporterSyntax(
        left = Syntax.TurtlesetType,
        right = Array(Syntax.NumberType, Syntax.LinksetType),
        ret = Syntax.TurtlesetType,
        precedence = Syntax.NormalPrecedence + 2,
        agentClassString = "-T--")
    override def report(args: Array[Argument], context: Context) = {
      val sourceSet = args(0).getAgentSet
      val radius = args(1).getDoubleValue
      val linkBreed = args(2).getAgentSet
      requireTurtleset(sourceSet)
      if (radius < 0)
        throw new ExtensionException(
          context, displayName + " cannot be given a negative radius")
      requireLinkBreed(context, linkBreed)
      Metrics.inLinkRadius(
        context.agent.asInstanceOf[Turtle], sourceSet, radius, linkBreed)
    }
  }

  class LinkDistance extends DefaultReporter {
    override def getSyntax =
      Syntax.reporterSyntax(
        Array(Syntax.TurtleType, Syntax.LinksetType),
        Syntax.NumberType, "-T--")
    override def report(args: Array[Argument], context: Context) = {
      val destNode = args(0).getTurtle
      val linkBreed = args(1).getAgentSet
      requireLinkBreed(context, linkBreed)
      requireAlive(destNode)
      java.lang.Double.valueOf(
        Metrics.networkDistance(context.agent.asInstanceOf[Turtle],
                                destNode, linkBreed))
    }
  }

  class MeanPathLength extends DefaultReporter {
    override def getSyntax =
      Syntax.reporterSyntax(
        Array(Syntax.TurtlesetType, Syntax.LinksetType),
        ret)
    override def report(args: Array[Argument], context: Context) = {
      val nodeSet = args(0).getAgentSet
      val linkBreed = args(1).getAgentSet
      requireTurtleset(context, nodeSet)
      requireLinkBreed(context, linkBreed)
      java.lang.Double.valueOf(
        Metrics.meanPathLength(nodeSet, linkBreed))
    }
  }

  class PathLinks extends DefaultReporter {
    override def getSyntax =
      Syntax.reporterSyntax(
        Array(Syntax.TurtleType, Syntax.LinksetType),
        Syntax.ListType, "-T--")
    override def report(args: Array[Argument], context: Context) = {
      val destNode = args(0).getTurtle
      val linkBreed = args(1).getAgentSet
      requireLinkBreed(context, linkBreed)
      requireAlive(destNode)
      Metrics.shortestPathLinks(
        context.job.random, context.agent.asInstanceOf[Turtle], destNode, linkBreed)
    }
  }
  
  class PathTurtles extends DefaultReporter {
    override def getSyntax =
      Syntax.reporterSyntax(
        Array(Syntax.TurtleType, Syntax.LinksetType),
        Syntax.ListType, "-T--")
    override def report(args: Array[Argument], context: Context) = {
      val destNode = args(0).getTurtle
      val linkBreed = args(1).getAgentSet
      requireLinkBreed(context, linkBreed)
      requireAlive(context, destNode)
      Metrics.networkShortestPathNodes(
        context.job.random, context.agent.asInstanceOf[Turtle], destNode, linkBreed)
    }
  }

}
