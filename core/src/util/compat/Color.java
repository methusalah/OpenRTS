package util.compat;

/**
 * Drop-in replacement for java.awt.Color that is TeaVM-compatible.
 */
public class Color {
    public static final Color WHITE   = new Color(255, 255, 255);
    public static final Color white   = WHITE;
    public static final Color BLACK   = new Color(0, 0, 0);
    public static final Color black   = BLACK;
    public static final Color RED     = new Color(255, 0, 0);
    public static final Color red     = RED;
    public static final Color BLUE    = new Color(0, 0, 255);
    public static final Color blue    = BLUE;
    public static final Color GREEN   = new Color(0, 128, 0);
    public static final Color green   = GREEN;
    public static final Color ORANGE  = new Color(255, 165, 0);
    public static final Color YELLOW  = new Color(255, 255, 0);
    public static final Color GRAY    = new Color(128, 128, 128);
    public static final Color gray    = GRAY;
    public static final Color CYAN    = new Color(0, 255, 255);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color PINK    = new Color(255, 175, 175);
    public static final Color LIGHT_GRAY = new Color(192, 192, 192);

    private final int r, g, b, a;

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
        this.a = clamp(a);
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    public int getRed()   { return r; }
    public int getGreen() { return g; }
    public int getBlue()  { return b; }
    public int getAlpha() { return a; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Color)) return false;
        Color c = (Color) obj;
        return r == c.r && g == c.g && b == c.b && a == c.a;
    }

    @Override
    public int hashCode() {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
