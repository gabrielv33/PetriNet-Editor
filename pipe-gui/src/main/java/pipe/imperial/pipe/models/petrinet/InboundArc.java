package pipe.imperial.pipe.models.petrinet;

import java.util.Map;

import uk.ac.imperial.pipe.models.petrinet.ArcType;
import uk.ac.imperial.pipe.models.petrinet.ArcVisitor;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

public abstract class InboundArc extends AbstractArc {
   public InboundArc(Place source, Transition target, Map tokenWeights, ArcType type) {
      super(source, target, tokenWeights, type);
   }

   public final void accept(PetriNetComponentVisitor visitor) {
      if (visitor instanceof uk.ac.imperial.pipe.models.petrinet.ArcVisitor) {
         ((ArcVisitor)visitor).visit(this);
      }

   }
}