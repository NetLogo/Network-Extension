package org.nlogo.extensions.network

import org.nlogo.api.{
  DefaultClassManager, PrimitiveManager, Agent, AgentSet, Argument, Context, DefaultReporter,
  ExtensionException, I18N, Primitive, Syntax, Turtle }

class NetworkExtension extends DefaultClassManager {
  override def load(primManager: PrimitiveManager) {
    primManager.addPrimitive("in-link-radius", InLinkRadius)
    primManager.addPrimitive("in-out-link-radius", InOutLinkRadius)
    primManager.addPrimitive("in-in-link-radius", InInLinkRadius)
    primManager.addPrimitive("link-distance", LinkDistance)
    primManager.addPrimitive("mean-link-path-length", MeanLinkPathLength)
    primManager.addPrimitive("link-path", LinkPath)
    primManager.addPrimitive("link-path-turtles", LinkPathTurtles)
  }
}

trait Helpers {
  val radiusSyntax =
    Syntax.reporterSyntax(
      left = Syntax.AgentsetType,
      right = Array(Syntax.NumberType, Syntax.LinksetType),
      ret = Syntax.TurtlesetType,
      isRightAssociative = false,
      precedence = Syntax.NormalPrecedence + 2, // same as in-radius
      agentClassString = "-T--",
      blockAgentClassString = null)
  def requireTurtleset(agents: AgentSet) {
    if(!classOf[Turtle].isAssignableFrom(agents.`type`))
      throw new ExtensionException(
        "Expected input to be a turtleset")
  }
  def requireLinkBreed(context: Context, agents: AgentSet, allowDirected: Boolean = true, allowUndirected: Boolean = true) {
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

object InLinkRadius extends DefaultReporter with Helpers {
  override def getSyntax = radiusSyntax
  override def report(args: Array[Argument], context: Context) = {
    val sourceSet = args(0).getAgentSet
    val radius = args(1).getDoubleValue
    val linkBreed = args(2).getAgentSet
    requireTurtleset(sourceSet)
    if (radius < 0)
      throw new ExtensionException("radius cannot be negative")
    requireLinkBreed(context, linkBreed, allowDirected = false)
    Metrics.inLinkRadius(
      sourceSet.asInstanceOf[org.nlogo.agent.AgentSet],
      context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
      radius, linkBreed.asInstanceOf[org.nlogo.agent.AgentSet])
  }
}

object InOutLinkRadius extends DefaultReporter with Helpers {
  override def getSyntax = radiusSyntax
  override def report(args: Array[Argument], context: Context) = {
    val sourceSet = args(0).getAgentSet
    val radius = args(1).getDoubleValue
    val linkBreed = args(2).getAgentSet
    requireTurtleset(sourceSet)
    if (radius < 0)
      throw new ExtensionException("radius cannot be negative")
    requireLinkBreed(context, linkBreed, allowUndirected = false)
    Metrics.inLinkRadius(
      sourceSet.asInstanceOf[org.nlogo.agent.AgentSet],
      context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
      radius, linkBreed.asInstanceOf[org.nlogo.agent.AgentSet])
  }
}

object InInLinkRadius extends DefaultReporter with Helpers {
  override def getSyntax = radiusSyntax
  override def report(args: Array[Argument], context: Context) = {
    val sourceSet = args(0).getAgentSet
    val radius = args(1).getDoubleValue
    val linkBreed = args(2).getAgentSet
    requireTurtleset(sourceSet)
    if (radius < 0)
      throw new ExtensionException("radius cannot be negative")
    requireLinkBreed(context, linkBreed, allowUndirected = false)
    Metrics.inLinkRadius(
      sourceSet.asInstanceOf[org.nlogo.agent.AgentSet],
      context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
      radius, linkBreed.asInstanceOf[org.nlogo.agent.AgentSet],
      reverse = true)
  }
}

object LinkDistance extends DefaultReporter with Helpers {
  override def getSyntax =
    Syntax.reporterSyntax(
      Array(Syntax.TurtleType, Syntax.LinksetType),
      Syntax.NumberType | Syntax.BooleanType,
      agentClassString = "-T--")
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val destNode = args(0).getTurtle
    val linkBreed = args(1).getAgentSet
    requireLinkBreed(context, linkBreed)
    requireAlive(destNode)
    val result =
      Metrics.linkDistance(
        context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
        destNode.asInstanceOf[org.nlogo.agent.Turtle],
        linkBreed.asInstanceOf[org.nlogo.agent.AgentSet])
    result.map(Double.box(_))
      .getOrElse(java.lang.Boolean.FALSE)
  }
}

object MeanLinkPathLength extends DefaultReporter with Helpers {
  override def getSyntax =
    Syntax.reporterSyntax(
      Array(Syntax.TurtlesetType, Syntax.LinksetType),
      Syntax.NumberType | Syntax.BooleanType)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val nodeSet = args(0).getAgentSet
    val linkBreed = args(1).getAgentSet
    requireTurtleset(nodeSet)
    requireLinkBreed(context, linkBreed)
    val result =
      Metrics.meanLinkPathLength(
        nodeSet.asInstanceOf[org.nlogo.agent.AgentSet],
        linkBreed.asInstanceOf[org.nlogo.agent.AgentSet])
    result.map(Double.box(_))
      .getOrElse(java.lang.Boolean.FALSE)
  }
}

object LinkPath extends DefaultReporter with Helpers {
  override def getSyntax =
    Syntax.reporterSyntax(
      Array(Syntax.TurtleType, Syntax.LinksetType),
      Syntax.ListType,
      agentClassString = "-T--")
  override def report(args: Array[Argument], context: Context) = {
    val destNode = args(0).getTurtle
    val linkBreed = args(1).getAgentSet
    requireLinkBreed(context, linkBreed)
    requireAlive(destNode)
    Metrics.linkPath(
      context.getRNG, context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
      destNode.asInstanceOf[org.nlogo.agent.Turtle],
      linkBreed.asInstanceOf[org.nlogo.agent.AgentSet])
  }
}

object LinkPathTurtles extends DefaultReporter with Helpers {
  override def getSyntax =
    Syntax.reporterSyntax(
      Array(Syntax.TurtleType, Syntax.LinksetType),
      Syntax.ListType,
      agentClassString = "-T--")
  override def report(args: Array[Argument], context: Context) = {
    val destNode = args(0).getTurtle
    val linkBreed = args(1).getAgentSet
    requireLinkBreed(context, linkBreed)
    requireAlive(destNode)
    Metrics.linkPathTurtles(
      context.getRNG, context.getAgent.asInstanceOf[org.nlogo.agent.Turtle],
      destNode.asInstanceOf[org.nlogo.agent.Turtle],
      linkBreed.asInstanceOf[org.nlogo.agent.AgentSet])
  }
}
