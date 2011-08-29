package org.nlogo.extensions.network;

import java.util.Set;

import org.nlogo.agent.Agent;
import org.nlogo.agent.ArrayAgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.api.AgentSet;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;
import org.nlogo.nvm.ArgumentTypeException;
import org.nlogo.nvm.EngineException;

public final strictfp class InLinkRadius
    extends DefaultReporter {
  @Override
  public Syntax getSyntax() {
    int left = Syntax.TurtlesetType();
    int[] right = {Syntax.NumberType(), Syntax.LinksetType()};
    int ret = Syntax.TurtlesetType();
    return Syntax.reporterSyntax(
      left, right, ret, Syntax.NormalPrecedence() + 2, false,
      "-T--", null);
  }
  @Override
  public Object report(Argument[] args, Context context) throws LogoException {
    AgentSet sourceSet = args[0].getAgentSet();
    double radius = args[1].getDoubleValue();
    AgentSet linkBreed = args[2].getAgentSet();
    if (sourceSet.type() != org.nlogo.agent.Turtle.class) {
      throw new ArgumentTypeException(context, this, 0, Syntax.TurtlesetType(), sourceSet);
    }
    if (linkBreed != world.links() && !world.isLinkBreed(linkBreed)) {
      throw new EngineException
          (context, this, "expected last input to be a link breed.");
    }
    if (radius < 0) {
      throw new EngineException
          (context, this, displayName() + " should not be given a negative radius");
    }
    Set<Turtle> result =
        world.linkManager.networkMetrics.inNetworkRadius((Turtle) context.agent, sourceSet, radius, linkBreed);
    return new ArrayAgentSet
        (sourceSet.type(),
            result.toArray(new Agent[result.size()]),
            world);
  }
}
