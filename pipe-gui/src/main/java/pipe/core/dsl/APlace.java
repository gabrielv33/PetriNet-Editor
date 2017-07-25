package pipe.core.dsl;

import java.util.HashMap;
import java.util.Map;
import pipe.core.models.petrinet.DiscretePlace;
import pipe.core.models.petrinet.Place;

public final class APlace implements DSLCreator {
   private String id;
   private int capacity = 0;
   private Map tokenCounts = new HashMap();
   private int x = 0;
   private int y = 0;

   private APlace(String id) {
      super();
      this.id = id;
   }

   public static APlace withId(String id) {
      return new APlace(id);
   }

   public APlace andCapacity(int capacity) {
      this.capacity = capacity;
      return this;
   }

   public APlace containing(int count, String tokenId) {
      this.tokenCounts.put(tokenId, count);
      return this;
   }

   public APlace tokens() {
      return this;
   }

   public APlace token() {
      return this;
   }

   public Place create(Map tokens, Map places, Map transitions, Map rateParameters) {
      Place place = new DiscretePlace(this.id, this.id);
      place.setX(this.x);
      place.setY(this.y);
      place.setCapacity(this.capacity);
      place.setTokenCounts(this.tokenCounts);
      places.put(this.id, place);
      return place;
   }

   public APlace and(int count, String tokenName) {
      this.tokenCounts.put(tokenName, count);
      return this;
   }

   public APlace locatedAt(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
   }
}
