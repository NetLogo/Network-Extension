package org.nlogo.extensions.network;

import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.PrimitiveManager;

import java.util.Iterator;

public class TableExtension
extends DefaultClassManager {

  public void load(PrimitiveManager primManager) {
    primManager.addPrimitive("in-link-radius", new InLinkRadius());
    primManager.addPrimitive("link-distance", new LinkDistance());
    primManager.addPrimitive("mean-path-length", new MeanPathLength());
    primManager.addPrimitive("path-links", new PathLinks());
    primManager.addPrimitive("path-turtles", new PathTurtles());
  }

}
