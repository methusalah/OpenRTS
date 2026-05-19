package model.battlefield.army.motion.pathfinding;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import geometry.geom2d.Point2D;
import model.battlefield.map.Map;
import model.battlefield.map.MapStyle;
import model.battlefield.map.Tile;
import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderTest {

    private Map createTestMap(int width, int height) {
        MapStyle style = new MapStyle(
            "test_style",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            width, height
        );
        return new Map(style);
    }

    @Test
    public void testPathFinderStraightLine() {
        Map map = createTestMap(10, 10);
        PathFinder pathFinder = new PathFinder(map);

        Point2D start = new Point2D(1.5, 1.5);
        Point2D dest = new Point2D(4.5, 1.5);

        Path path = pathFinder.findPath(start, dest);

        assertThat(path).isNotNull();
        // Path should not be empty since there are no obstacles
        assertThat(path.isEmpty()).isFalse();
        
        // The path should successfully start at start and end at destination
        assertThat(path.getFirstWaypoint().x).isEqualTo(1.5);
        assertThat(path.getFirstWaypoint().y).isEqualTo(1.5);
        assertThat(path.getLastWaypoint().x).isEqualTo(4.5);
        assertThat(path.getLastWaypoint().y).isEqualTo(1.5);
    }

    @Test
    public void testPathFinderBlockedDestination() {
        Map map = createTestMap(10, 10);
        
        // Block the destination tile
        Tile tile = map.get(4, 1);
        tile.hasBlockingTrinket = true;

        PathFinder pathFinder = new PathFinder(map);
        Point2D start = new Point2D(1.5, 1.5);
        Point2D dest = new Point2D(4.5, 1.5);

        Path path = pathFinder.findPath(start, dest);

        // Should return empty path since destination is blocked
        assertThat(path.isEmpty()).isTrue();
    }
}
