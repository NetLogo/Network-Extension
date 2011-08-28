package org.nlogo.prim.etc;

import org.nlogo.agent.AgentSet;
import org.nlogo.api.I18N;
import org.nlogo.api.LogoException;
import org.nlogo.nvm.ArgumentTypeException;
import org.nlogo.nvm.Context;
import org.nlogo.nvm.Reporter;
import org.nlogo.nvm.EngineException;
import org.nlogo.nvm.Syntax;

public final strictfp class MeanPathLength extends Reporter {
  @Override
  public Syntax syntax() {
    int[] right = {Syntax.TYPE_TURTLESET, Syntax.TYPE_LINKSET};
    int ret = Syntax.TYPE_NUMBER;
    return Syntax.reporterSyntax(right, ret);
  }
  @Override
  public Object report(Context context) throws LogoException {
    AgentSet nodeSet = argEvalAgentSet(context, 0);
    AgentSet linkBreed = argEvalAgentSet(context, 1);
    if (nodeSet.type() != org.nlogo.agent.Turtle.class) {
      throw new ArgumentTypeException
          (context, this, 0, Syntax.TYPE_TURTLESET, nodeSet);
    }
    if (linkBreed != world.links() && !world.isLinkBreed(linkBreed)) {
      throw new EngineException (context, this,
              I18N.errorsJ().get("org.nlogo.prim.etc.$common.expectedLastInputToBeLinkBreed"));
    }
    return Double.valueOf(
      world.linkManager.networkMetrics.meanPathLength(nodeSet, linkBreed));
  }
}
