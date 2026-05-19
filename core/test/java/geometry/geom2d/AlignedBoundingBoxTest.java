package geometry.geom2d;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class AlignedBoundingBoxTest {

    @Test
    public void testBoxCreationFromTwoCorners() {
        Point2D c1 = new Point2D(0, 0);
        Point2D c2 = new Point2D(10, 5);

        AlignedBoundingBox box = new AlignedBoundingBox(c1, c2);

        assertThat(box.minX).isEqualTo(0.0);
        assertThat(box.maxX).isEqualTo(10.0);
        assertThat(box.minY).isEqualTo(0.0);
        assertThat(box.maxY).isEqualTo(5.0);
        assertThat(box.width).isEqualTo(10.0);
        assertThat(box.height).isEqualTo(5.0);
        assertThat(box.getArea()).isEqualTo(50.0);
    }

    @Test
    public void testBoxCreationFromPointsList() {
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(new Point2D(3, 8));
        points.add(new Point2D(-1, 2));
        points.add(new Point2D(5, -4));

        AlignedBoundingBox box = new AlignedBoundingBox(points);

        assertThat(box.minX).isEqualTo(-1.0);
        assertThat(box.maxX).isEqualTo(5.0);
        assertThat(box.minY).isEqualTo(-4.0);
        assertThat(box.maxY).isEqualTo(8.0);
        assertThat(box.width).isEqualTo(6.0);
        assertThat(box.height).isEqualTo(12.0);
        assertThat(box.getArea()).isEqualTo(72.0);
    }

    @Test
    public void testContainsPoint() {
        Point2D c1 = new Point2D(0, 0);
        Point2D c2 = new Point2D(10, 10);
        AlignedBoundingBox box = new AlignedBoundingBox(c1, c2);

        Point2D inside = new Point2D(5, 5);
        Point2D outside = new Point2D(12, 5);
        Point2D boundary = new Point2D(10, 0);

        assertThat(box.contains(inside)).isTrue();
        assertThat(box.contains(outside)).isFalse();
        assertThat(box.contains(boundary)).isTrue(); // Boundary is approximate contained based on PrecisionUtil
    }

    @Test
    public void testGetCenter() {
        Point2D c1 = new Point2D(2, 4);
        Point2D c2 = new Point2D(8, 12);
        AlignedBoundingBox box = new AlignedBoundingBox(c1, c2);

        Point2D center = box.getCenter();
        assertThat(center.x).isEqualTo(5.0);
        assertThat(center.y).isEqualTo(8.0);
    }

    @Test
    public void testCollideBoxes() {
        AlignedBoundingBox box1 = new AlignedBoundingBox(new Point2D(0, 0), new Point2D(5, 5));
        AlignedBoundingBox box2 = new AlignedBoundingBox(new Point2D(4, 4), new Point2D(8, 8));
        AlignedBoundingBox box3 = new AlignedBoundingBox(new Point2D(6, 6), new Point2D(10, 10));

        assertThat(box1.collide(box2)).isTrue();
        assertThat(box2.collide(box3)).isTrue();
        assertThat(box1.collide(box3)).isFalse();
    }
}
