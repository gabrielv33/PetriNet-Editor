package pipe.core.exceptions;

public class PetriNetComponentException extends Exception {
   public PetriNetComponentException(String message) {
      super(message);
   }

   public PetriNetComponentException(Throwable throwable) {
      super(throwable);
   }
}
