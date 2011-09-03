package org.nlogo.extensions.network

import org.nlogo.api.{
  DefaultClassManager, PrimitiveManager, Agent, AgentSet, Argument, Context, DefaultReporter,
  ExtensionException, I18N, Primitive, Syntax, Turtle }

class NetworkExtension extends DefaultClassManager {
  override def load(primManager: PrimitiveManager) {
    primManager.addPrimitive("in-link-radius", new InLinkRadius)
    primManager.addPrimitive("link-distance", new LinkDistance)
    primManager.addPrimitive("mean-path-length", new MeanPathLength)
    primManager.addPrimitive("path-links", new PathLinks)
    primManager.addPrimitive("path-turtles", new PathTurtles)
  }
}

trait Helpers {
  def requireTurtleset(agents: AgentSet) {
    if(!classOf[Turtle].isAssignableFrom(agents.`type`))
      throw new ExtensionException(
        "Expected input to be a turtleset")
  }
  def requireLinkBreed(context: Context, agents: AgentSet) {
    val world = context.getAgent.world
    if ((agents ne world.links) &&
        !world.asInstanceOf[org.nlogo.agent.World].isLinkBreed(agents.asInstanceOf[org.nlogo.agent.AgentSet]))
      throw new ExtensionException(
        I18N.errors.get("org.nlogo.prim.etc.$common.expectedLastInputToBeLinkBreed"))
  }
  def requireAlive(agent: Agent) {
    if (agent.id == -1)
      throw new ExtensionException(
        I18N.errors.get("org.nlogo.$common.thatAgentIsDead"))
  }
}

/// primitives

class InLinkRadius extends DefaultReporter with Helpers {
  override def getSyntax =
    Syntax(
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
      throw new ExtensionException("radius cannot be negative")
    requireLinkBreed(context, linkBreed)
    Metrics.inLinkRadius(
      context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
      sourceSet.asInstanceOf[org.nlogo.agent.AgentSet],
      radius, linkBreed.asInstanceOf[org.nlogo.agent.AgentSet])
  }
}

class LinkDistance extends DefaultReporter with Helpers {
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
      Metrics.networkDistance(context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
                              destNode.asInstanceOf[org.nlogo.agent.Turtle],
                              linkBreed.asInstanceOf[org.nlogo.agent.AgentSet]))
  }
}

class MeanPathLength extends DefaultReporter with Helpers {
  override def getSyntax =
    Syntax.reporterSyntax(
      Array(Syntax.TurtlesetType, Syntax.LinksetType),
      Syntax.NumberType)
  override def report(args: Array[Argument], context: Context) = {
    val nodeSet = args(0).getAgentSet
    val linkBreed = args(1).getAgentSet
    requireTurtleset(nodeSet)
    requireLinkBreed(context, linkBreed)
    java.lang.Double.valueOf(
      Metrics.meanPathLength(nodeSet.asInstanceOf[org.nlogo.agent.AgentSet],
                             linkBreed.asInstanceOf[org.nlogo.agent.AgentSet]))
  }
}

class PathLinks extends DefaultReporter with Helpers {
  override def getSyntax =
    Syntax.reporterSyntax(
      Array(Syntax.TurtleType, Syntax.LinksetType),
      Syntax.ListType, "-T--")
  override def report(args: Array[Argument], context: Context) = {
    val destNode = args(0).getTurtle
    val linkBreed = args(1).getAgentSet
    requireLinkBreed(context, linkBreed)
    requireAlive(destNode)
    Metrics.pathLinks(
      context.getRNG, context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
      destNode.asInstanceOf[org.nlogo.agent.Turtle],
      linkBreed.asInstanceOf[org.nlogo.agent.AgentSet])
  }
}

class PathTurtles extends DefaultReporter with Helpers {
  override def getSyntax =
    Syntax.reporterSyntax(
      Array(Syntax.TurtleType, Syntax.LinksetType),
      Syntax.ListType, "-T--")
  override def report(args: Array[Argument], context: Context) = {
    val destNode = args(0).getTurtle
    val linkBreed = args(1).getAgentSet
    requireLinkBreed(context, linkBreed)
    requireAlive(destNode)
    Metrics.pathNodes(
      context.getRNG, context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
      destNode.asInstanceOf[org.nlogo.agent.Turtle],
      linkBreed.asInstanceOf[org.nlogo.agent.AgentSet])
  }
}
