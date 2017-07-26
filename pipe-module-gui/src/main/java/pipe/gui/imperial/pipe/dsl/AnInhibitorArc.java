package pipe.gui.imperial.pipe.dsl;

import java.util.Map;

import pipe.gui.imperial.pipe.dsl.DSLCreator;
import pipe.gui.imperial.pipe.models.petrinet.Arc;
import pipe.gui.imperial.pipe.models.petrinet.InboundInhibitorArc;
import pipe.gui.imperial.pipe.models.petrinet.Place;
import pipe.gui.imperial.pipe.models.petrinet.Transition;

public final class AnInhibitorArc implements DSLCreator {
   private String source;
   private String target;

   public static AnInhibitorArc withSource(String source) {
      AnInhibitorArc anInhibitorArc = new AnInhibitorArc();
      anInhibitorArc.source = source;
      return anInhibitorArc;
   }

   public AnInhibitorArc andTarget(String target) {
      this.target = target;
      return this;
   }

   public Arc create(Map tokens, Map places, Map transitions, Map rateParameters) {
      return new InboundInhibitorArc((Place)places.get(this.source), (Transition)transitions.get(this.target));
   }
}