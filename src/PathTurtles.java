package org.nlogo.extensions.network;

import org.nlogo.api.AgentSet;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.I18N;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoList;
import org.nlogo.api.Syntax;
import org.nlogo.api.Turtle;
import org.nlogo.nvm.EngineException;

public final strictfp class PathTurtles extends DefaultReporter {
  @Override
  public Syntax getSyntax() {
    int[] right = {Syntax.TurtleType(), Syntax.LinksetType()};
    int ret = Syntax.ListType();
    return Syntax.reporterSyntax(right, ret, "-T--");
  }
  @Override
  public Object report(Argument[] args, final Context context) throws LogoException {
    Turtle destNode = args[0].getTurtle();
    AgentSet linkBreed = args[1].getAgentSet();
    if (linkBreed != world.links() && !world.isLinkBreed(linkBreed)) {
      throw new EngineException (context, this,
              I18N.errorsJ().get("org.nlogo.prim.etc.$common.expectedLastInputToBeLinkBreed"));
    }
    if (destNode.id == -1) {
      throw new EngineException(context, this,
        I18N.errorsJ().getN("org.nlogo.$common.thatAgentIsDead", destNode.classDisplayName()));
    }
    return world.linkManager.networkMetrics.networkShortestPathNodes
        (context.job.random, (Turtle) context.agent, destNode, linkBreed);
  }
}
