package pipe.imperial.pipe.naming;

import uk.ac.imperial.pipe.models.petrinet.PetriNet;

public final class PetriNetNamer extends AbstractUniqueNamer {
   public PetriNetNamer() {
      super("Petri Net ");
   }

   public void registerPetriNet(PetriNet petriNet) {
      this.names.add(petriNet.getNameValue());
   }

   public void deRegisterPetriNet(PetriNet petriNet) {
      this.names.remove(petriNet.getNameValue());
   }
}