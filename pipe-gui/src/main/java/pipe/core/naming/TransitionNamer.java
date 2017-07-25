package pipe.core.naming;

import java.util.Iterator;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.Transition;
import uk.ac.imperial.pipe.naming.ComponentNamer;

public class TransitionNamer extends ComponentNamer {
   public TransitionNamer(PetriNet petriNet) {
      super(petriNet, "T", "newTransition", "deleteTransition");
      this.initialiseTransitionNames();
   }

   private void initialiseTransitionNames() {
      Iterator i$ = this.petriNet.getTransitions().iterator();

      while(i$.hasNext()) {
         Transition transition = (Transition)i$.next();
         transition.addPropertyChangeListener(this.nameListener);
         this.names.add(transition.getId());
      }

   }
}
