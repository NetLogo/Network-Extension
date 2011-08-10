  // layout-sphere
  // experimental

  // An experimental "__layout-sphere" for 3D.  Not polished yet.
  // Also not sure if it's a worthwhile primitive or not,
  // especially given how slow it runs for large numbers of turtles.
  //  ~Forrest (12/5/2006)
  public static void sphere(AgentSet nodeset, double radius, double initialTemp,
                            org.nlogo.util.MersenneTwisterFast random) {
    World3D world = (World3D) nodeset.world();
    int nodeCount = nodeset.count();
    if (nodeCount == 0) {
      return;
    }
    double[] nx = new double[nodeCount];
    double[] ny = new double[nodeCount];
    double[] nz = new double[nodeCount];

    Turtle3D[] agt = new Turtle3D[nodeCount];
    int i = 0;
    for (AgentSet.Iterator it = nodeset.iterator(); it.hasNext(); i++) {
      Turtle3D t = (Turtle3D) it.next();
      agt[i] = t;
      nx[i] = t.xcor();
      ny[i] = t.ycor();
      nz[i] = t.zcor();
      if (nx[i] == 0.0 && ny[i] == 0.0 && nz[i] == 0.0) {
        nz[i] = random.nextDouble() * 2.0 - 1.0;
        double remainder = StrictMath.sqrt(1.0 * 1.0 - nz[i] * nz[i]);
        double angle = random.nextDouble() * StrictMath.PI * 2;
        nx[i] = remainder * StrictMath.sin(angle);
        ny[i] = remainder * StrictMath.cos(angle);
      }
    }

    double temperature = initialTemp / nodeCount;
    for (int k = 0; k < 30; k++) {
      for (i = 0; i < nodeCount; i++) {
        for (int j = i + 1; j < nodeCount; j++) {
          double dx = nx[j] - nx[i];
          double dy = ny[j] - ny[i];
          double dz = nz[j] - nz[i];

          double distSq = dx * dx + dy * dy + dz * dz;
          if (distSq < 1.0E-20) {
            dx = temperature * (random.nextDouble() - 0.5);
            dy = temperature * (random.nextDouble() - 0.5);
            dz = temperature * (random.nextDouble() - 0.5);
          } else {
            // repulse according to an inverse cubic function
            double f = temperature / (distSq * distSq);
            dx = -(f * dx);
            dy = -(f * dy);
            dz = -(f * dz);
          }

          nx[i] += dx;
          ny[i] += dy;
          nz[i] += dz;
          double magnitude = StrictMath.sqrt(nx[i] * nx[i] + ny[i] * ny[i] + nz[i] * nz[i]);
          nx[i] = nx[i] / magnitude;
          ny[i] = ny[i] / magnitude;
          nz[i] = nz[i] / magnitude;

          nx[j] -= dx;
          ny[j] -= dy;
          nz[j] -= dz;
          magnitude = StrictMath.sqrt(nx[j] * nx[j] + ny[j] * ny[j] + nz[j] * nz[j]);
          nx[j] = nx[j] / magnitude;
          ny[j] = ny[j] / magnitude;
          nz[j] = nz[j] / magnitude;
        }
      }
      temperature *= 0.75;
    }

    for (i = 0; i < nodeCount; i++) {
      double newx = nx[i] * radius;
      double newy = ny[i] * radius;
      double newz = nz[i] * radius;

      if (newx > world.maxPxcor()) {
        newx = world.maxPxcor();
      } else if (newx < world.minPxcor()) {
        newx = world.minPxcor();
      }

      if (newy > world.maxPycor()) {
        newy = world.maxPycor();
      } else if (newy < world.minPycor()) {
        newy = world.minPycor();
      }
      if (newz > world.maxPzcor()) {
        newz = world.maxPzcor();
      } else if (newz < world.minPzcor()) {
        newz = world.minPzcor();
      }

      agt[i].xyandzcor(newx, newy, newz);
    }
  }


  /// magspring

  private static final double MAGSPRING_SMALL_THRESHOLD = 0.0000001;

  private static final int FIELD_NONE = 0;
  private static final int FIELD_NORTH = 1;
  private static final int FIELD_NORTHEAST = 2;
  private static final int FIELD_EAST = 3;
  private static final int FIELD_SOUTHEAST = 4;
  private static final int FIELD_SOUTH = 5;
  private static final int FIELD_SOUTHWEST = 6;
  private static final int FIELD_WEST = 7;
  private static final int FIELD_NORTHWEST = 8;
  private static final int FIELD_POLAR = 9;
  private static final int FIELD_CONCENTRIC = 10;

  public static void magspring(AgentSet nodeset, AgentSet linkset,
                               double spr, double len, double rep,
                               double magStr, int fieldType,
                               boolean bidirectional,
                               org.nlogo.util.MersenneTwisterFast random) {
    World world = nodeset.world();

    int nodeCount = nodeset.count();

    Turtle[] agt = new Turtle[nodeCount];
    double[] ax = new double[nodeCount];
    double[] ay = new double[nodeCount];
    int ctr = 0;

    for (AgentSet.Iterator iter = nodeset.shufflerator(random); iter.hasNext(); ctr++) {
      agt[ctr] = (Turtle) iter.next();
    }

    for (int i = 0; i < nodeCount; i++) {
      Turtle t = agt[i];

      double fx = 0, fy = 0;
      for (AgentSet.Iterator it = world.links().shufflerator(random); it.hasNext();) {
        Link link = (Link) it.next();
        if ((link.end1() == t || link.end2() == t) &&
            (linkset.contains(link))) {
          Turtle other = link.end1();
          if (t == link.end1()) {
            other = link.end2();
          }
          double dist = world.protractor().distance(t, other, false);
          double dx = other.xcor() - t.xcor();
          double dy = other.ycor() - t.ycor();
          if (StrictMath.abs(dist) < MAGSPRING_SMALL_THRESHOLD) {
            if (t == link.end1()) {
              fx += (spr * len);
            } else {
              fx -= (spr * len);
            }
          } else {
            // calculate attractive force
            double f = spr * (dist - len);
            fx = fx + (f * dx / dist);
            fy = fy + (f * dy / dist);

            // calculate magnetic force
            java.awt.geom.Point2D.Double mf = magForce(t.xcor(), t.ycor(), fieldType);

            // we want to know the angle between the link and the magnetic field
            // calculate dotProduct first
            double dot = mf.x * dx + mf.y * dy;
            // then calculate the cosine of
            double cosAngle = StrictMath.abs(dot) / dist;

            // keep track of whether the dotProduct was negative
            // so we can push or pull, accordingly
            double negFlag = (dot < 0) ? 1 : -1;

            if (!bidirectional) {
              negFlag = 1;
            }

            // cosAngle can be > 1 because of weird float rounding.
            // if cosAngle >= 1, then angle = 0, and no magnetism occurs.
            if (cosAngle < 1) {
              double angle = StrictMath.abs(StrictMath.acos(cosAngle));
              // 1.5 is an arbitrary choice, chosen because it seemed to work well
              // some other power > 1 could be used as well.
              fx = fx + negFlag * magStr * mf.x * StrictMath.pow(angle, 1.5);
              fy = fy + negFlag * magStr * mf.y * StrictMath.pow(angle, 1.5);
            }
          }
        }
      }
      for (AgentSet.Iterator it = nodeset.shufflerator(random); it.hasNext();) {
        Turtle other = (Turtle) it.next();
        if (other != t) {
          double dx = other.xcor() - t.xcor();
          double dy = other.ycor() - t.ycor();

          if (dx == 0 && dy == 0) {
            double ang = 360 * random.nextDouble();
            fx = fx - (rep * StrictMath.sin(StrictMath.toRadians(ang)));
            fy = fy - (rep * StrictMath.cos(StrictMath.toRadians(ang)));
          } else {
            double dist = StrictMath.sqrt((dx * dx) + (dy * dy));
            //if ( dist <= 2 * len )
            //{
            double f = rep / (dist * dist);
            fx = fx - (f * dx / dist);
            fy = fy - (f * dy / dist);
            //}

          }
        }
      }
      double limit = 1;
      if (fx > limit) {
        fx = limit;
      } else {
        if (fx < -limit) {
          fx = -limit;
        }
      }
      if (fy > limit) {
        fy = limit;
      } else {
        if (fy < -limit) {
          fy = -limit;
        }
      }
      fx += t.xcor();
      fy += t.ycor();
      if (fx > world.maxPxcor()) {
        fx = world.maxPxcor();
      } else {
        if (fx < world.minPxcor()) {
          fx = world.minPxcor();
        }
      }
      if (fy > world.maxPycor()) {
        fy = world.maxPycor();
      } else {
        if (fy < world.minPycor()) {
          fy = world.minPycor();
        }
      }
      ax[i] = fx;
      ay[i] = fy;
    }

    // we need to bump some node a small amount, in case all nodes
    // are stuck on a single line
    if (nodeCount > 1) {
      double perturbAmt = (world.worldWidth() + world.worldHeight()) / (1.0E10);
      ax[0] += random.nextDouble() * perturbAmt - perturbAmt / 2.0;
      ay[0] += random.nextDouble() * perturbAmt - perturbAmt / 2.0;
    }

    reposition(agt, ax, ay);
  }

  private static final double COS45 = StrictMath.sqrt(2.0) / 2.0;

  private static java.awt.geom.Point2D.Double magForce(double x, double y, int fieldType) {
    double dist;
    switch (fieldType) {
      case FIELD_NORTH:
        return new java.awt.geom.Point2D.Double(0, 1);
      case FIELD_NORTHEAST:
        return new java.awt.geom.Point2D.Double(COS45, COS45);
      case FIELD_EAST:
        return new java.awt.geom.Point2D.Double(1, 0);
      case FIELD_SOUTHEAST:
        return new java.awt.geom.Point2D.Double(COS45, -COS45);
      case FIELD_SOUTH:
        return new java.awt.geom.Point2D.Double(0, -1);
      case FIELD_SOUTHWEST:
        return new java.awt.geom.Point2D.Double(-COS45, -COS45);
      case FIELD_WEST:
        return new java.awt.geom.Point2D.Double(-1, 0);
      case FIELD_NORTHWEST:
        return new java.awt.geom.Point2D.Double(-COS45, COS45);
      case FIELD_POLAR:
        dist = StrictMath.sqrt((x * x) + (y * y));
        if (StrictMath.abs(dist) < MAGSPRING_SMALL_THRESHOLD) {
          return new java.awt.geom.Point2D.Double(0, 0);
        }
        return new java.awt.geom.Point2D.Double(x / dist, y / dist);
      case FIELD_CONCENTRIC:
        dist = StrictMath.sqrt((x * x) + (y * y));
        if (StrictMath.abs(dist) < MAGSPRING_SMALL_THRESHOLD) {
          return new java.awt.geom.Point2D.Double(0, 0);
        }
        return new java.awt.geom.Point2D.Double(y / dist, -x / dist);
      case FIELD_NONE:
        return new java.awt.geom.Point2D.Double(0, 0);
      default:
        throw new IllegalStateException();
    }
  }

