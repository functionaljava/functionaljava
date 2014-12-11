package fj.demo;

import fj.P;
import fj.P2;
import fj.data.Lens;
import fj.data.State;

public final class Lens_demo {

  static final class Point {
    private final double x;
    private final double y;

    Point(final double x, final double y) {
      this.x = x;
      this.y = y;
    }

    double x() {
      return x;
    }

    Point withX(final double newX) {
      return new Point(newX, y);
    }

    double y() {
      return y;
    }

    Point withY(final double newY) {
      return new Point(x, newY);
    }

    @Override
    public String toString() {
      return String.format("Point{x=%s, y=%s}", x, y);
    }

    static final Lens<Point, Double> X =
        Lens.lens(Point::x, Point::withX);

    static final Lens<Point, Double> Y =
        Lens.lens(Point::y, Point::withY);
  }

  static final class Turtle {
    private final Point position;
    private final double heading;

    Turtle(final Point position, final double heading) {
      this.position = position;
      this.heading = heading;
    }

    Point position() {
      return position;
    }

    Turtle withPosition(final Point newPosition) {
      return new Turtle(newPosition, heading);
    }

    double heading() {
      return heading;
    }

    Turtle withHeading(final double newHeading) {
      return new Turtle(position, newHeading);
    }

    @Override
    public String toString() {
      return String.format("Turtle{position=%s, heading=%s}", position, heading);
    }

    static final Lens<Turtle, Point> Position =
        Lens.lens(Turtle::position, Turtle::withPosition);

    static final Lens<Turtle, Double> Heading =
        Lens.lens(Turtle::heading, Turtle::withHeading);

    static final Lens<Turtle, Double> X =
        Turtle.Position.andThen(Point.X);

    static final Lens<Turtle, Double> Y =
        Turtle.Position.andThen(Point.Y);

    static State<Turtle, P2<Double, Double>> forward(final double dist) {
      return Heading.flatMap(
          heading -> X.modS(x -> x + dist * Math.cos(heading)).flatMap(
          x       -> Y.modS(y -> y + dist * Math.sin(heading)).map(
          y       -> P.p(x, y))));
    }

    static Turtle forward(final Turtle t, final double dist) {
      return forward(dist).exec(t);
    }
  }

  public static void main(final String[] args) {
    final Turtle t0 = new Turtle(new Point(1.0, 2.0), 0.0);

    System.out.println("Turtle.X.get(t0) = " + Turtle.X.get(t0));
    System.out.println("Turtle.X.set(t0, 4.0) = " + Turtle.X.set(t0, 4.0));
    System.out.println("Turtle.X.mod(x -> x + 3.0, t0) = " + Turtle.X.mod(x -> x + 3.0, t0));
    System.out.println("Turtle.X.mod(x -> x + 3.0).f(t0) = " + Turtle.X.mod(x -> x + 3.0).f(t0));
    System.out.println("Turtle.X.modS(x -> x + 3.0).exec(t0) = " + Turtle.X.modS(x -> x + 3.0).exec(t0));
    System.out.println("Turtle.X.modS(x -> x + 3.0).run(t0) = " + Turtle.X.modS(x -> x + 3.0).run(t0));
    System.out.println("Turtle.forward(t0, 10.0) = " + Turtle.forward(t0, 10.0));
    System.out.println("Turtle.forward(13.37).eval(t0) = " + Turtle.forward(13.37).eval(t0));
  }
}
