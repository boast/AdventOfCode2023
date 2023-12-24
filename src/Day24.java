import com.microsoft.z3.Context;
import com.microsoft.z3.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day24 {
    
    public static void main(final String[] args) throws IOException {
        final var lines = Files.readAllLines(Path.of("resources/day24.txt"));
        
        System.out.println(part1(lines));
        System.out.println(part2(lines));
    }
    
    static long part1(final List<String> lines) {
        final var hailstones = lines.stream().map(Hailstone::fromString).toList();
        final var max        = 400000000000000L;
        final var min        = 200000000000000L;
        
        var count = 0;
        
        for (var i = 0; i < hailstones.size() - 1; i++) {
            for (var j = i + 1; j < hailstones.size(); j++) {
                final var t0 = hailstones.get(i).intersectionTime2D(hailstones.get(j));
                final var t1 = hailstones.get(j).intersectionTime2D(hailstones.get(i));
                
                if (Double.isInfinite(t0) || t0 < 0 || t1 < 0) {
                    continue;
                }
                
                final var x = hailstones.get(i).p().x() + hailstones.get(i).v().x() * t0;
                final var y = hailstones.get(i).p().y() + hailstones.get(i).v().y() * t0;
                
                if (x < min || x > max || y < min || y > max) {
                    continue;
                }
                count++;
            }
        }
        
        return count;
    }
    
    static long part2(final List<String> lines) {
        try (final var z3 = new Context()) {
            final var hailstones = lines.stream().map(Hailstone::fromString).toList();
            // Observation: once you hit the first 3 hailstones, you will hit all of them (as given, there is a solution)
            // We have to find a "hailstone" (our throw) that hits all 3 of them at 3 (possible different) times
            // That results in 9 equations with 9 unknowns (x, y, z, vx, vy, vz, t0, t1, t2) - plus all t >= 0
            
            // We can solve this with Z3
            final var x  = z3.mkRealConst("x");
            final var y  = z3.mkRealConst("y");
            final var z  = z3.mkRealConst("z");
            final var vx = z3.mkRealConst("vx");
            final var vy = z3.mkRealConst("vy");
            final var vz = z3.mkRealConst("vz");
            final var zero = z3.mkReal(0);
            
            final var solver = z3.mkSolver();
            
            for (var i = 0; i <= 2; i++) {
                final var h   = hailstones.get(i);
                final var t   = z3.mkRealConst("t" + i);
                final var hx  = z3.mkReal(h.p().x());
                final var hy  = z3.mkReal(h.p().y());
                final var hz  = z3.mkReal(h.p().z());
                final var hvx = z3.mkReal(h.v().x());
                final var hvy = z3.mkReal(h.v().y());
                final var hvz = z3.mkReal(h.v().z());
                
                // t >= 0
                solver.add(z3.mkGe(t, zero));
                // x + vx * t = hx + hvx * t
                solver.add(z3.mkEq(z3.mkAdd(x, z3.mkMul(vx, t)), z3.mkAdd(hx, z3.mkMul(hvx, t))));
                // y + vy * t = hy + hvy * t
                solver.add(z3.mkEq(z3.mkAdd(y, z3.mkMul(vy, t)), z3.mkAdd(hy, z3.mkMul(hvy, t))));
                // z + vz * t = hz + hvz * t
                solver.add(z3.mkEq(z3.mkAdd(z, z3.mkMul(vz, t)), z3.mkAdd(hz, z3.mkMul(hvz, t))));
            }
            
            if (solver.check() != Status.SATISFIABLE) {
                throw new IllegalStateException("No solution found");
            }
            final var model = solver.getModel();
            
            // Evaluate x, y and z and add the results up
            final var xr = Long.parseLong(model.eval(x, false).toString());
            final var yr = Long.parseLong(model.eval(y, false).toString());
            final var zr = Long.parseLong(model.eval(z, false).toString());
            
            return xr + yr + zr;
        }
    }
    
    private record Hailstone(Point3D p, Point3D v) {
        public static Hailstone fromString(final String line) {
            final var parts  = line.split(" @ ");
            final var points = new Point3D[2];
            for (int i = 0; i < 2; i++) {
                final var coords = parts[i].split(", ");
                points[i] = new Point3D(Long.parseLong(coords[0]),
                                        Long.parseLong(coords[1]),
                                        Long.parseLong(coords[2])
                );
            }
            return new Hailstone(points[0], points[1]);
        }
        
        public double intersectionTime2D(final Hailstone other) {
            return (double) (other.v().x() * (p().y() - other.p().y()) - other.v().y() * (p().x() - other.p().x())) /
                   (v().x() * other.v().y() - v().y() * other.v().x());
        }
    }
    
    private record Point3D(long x, long y, long z) {
    }
}