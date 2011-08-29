package org.nlogo.extensions.network;

import org.nlogo.api.AgentSet;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.I18N;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;
import org.nlogo.nvm.ArgumentTypeException;
import org.nlogo.nvm.EngineException;

public final strictfp class MeanPathLength extends DefaultReporter {
  @Override
  public Syntax getSyntax() {
    int[] right = {Syntax.TurtlesetType(), Syntax.LinksetType()};
    int ret = Syntax.NumberType();
    return Syntax.reporterSyntax(right, ret);
  }
  @Override
  public Object report(Argument[] args, Context context) throws LogoException {
    AgentSet nodeSet = args[0].getAgentSet();
    AgentSet linkBreed = args[1].getAgentSet();
    if (nodeSet.type() != org.nlogo.agent.Turtle.class) {
      throw new ArgumentTypeException(
        context, this, 0, Syntax.TurtlesetType(), nodeSet);
    }
    if (linkBreed != world.links() && !world.isLinkBreed(linkBreed)) {
      throw new EngineException (context, this,
              I18N.errorsJ().get("org.nlogo.prim.etc.$common.expectedLastInputToBeLinkBreed"));
    }
    return Double.valueOf(
      world.linkManager.networkMetrics.meanPathLength(nodeSet, linkBreed));
  }
}
