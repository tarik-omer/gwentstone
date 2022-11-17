package fileio;

public final class Coordinates {
   private int x, y;

   public Coordinates() {
   }

   public Coordinates(Coordinates coordinates) {
      this.x = coordinates.x;
      this.y = coordinates.y;
   }

    public int getX() {
      return x;
   }

   public void setX(final int x) {
      this.x = x;
   }

   public int getY() {
      return y;
   }

   public void setY(final int y) {
      this.y = y;
   }

   @Override
   public String toString() {
      return "Coordinates{"
              + "x="
              + x
              + ", y="
              + y
              + '}';
   }
}
