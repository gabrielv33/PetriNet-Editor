package pipe.core.models.petrinet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.collections.CollectionUtils;
import pipe.core.exceptions.InvalidRateException;
import pipe.core.exceptions.PetriNetComponentException;
import pipe.core.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Rate;
import uk.ac.imperial.pipe.models.petrinet.Token;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetName;
import pipe.core.parsers.EvalVisitor;
import pipe.core.parsers.FunctionalResults;
import pipe.core.parsers.FunctionalWeightParser;
import pipe.core.parsers.PetriNetWeightParser;
import pipe.core.visitor.component.PetriNetComponentVisitor;

public class PetriNet {
   public static final String PETRI_NET_NAME_CHANGE_MESSAGE = "nameChange";
   public static final String NEW_ANNOTATION_CHANGE_MESSAGE = "newAnnotation";
   public static final String DELETE_PLACE_CHANGE_MESSAGE = "deletePlace";
   public static final String DELETE_ARC_CHANGE_MESSAGE = "deleteArc";
   public static final String DELETE_TRANSITION_CHANGE_MESSAGE = "deleteTransition";
   public static final String DELETE_ANNOTATION_CHANGE_MESSAGE = "deleteAnnotation";
   public static final String NEW_PLACE_CHANGE_MESSAGE = "newPlace";
   public static final String NEW_TRANSITION_CHANGE_MESSAGE = "newTransition";
   public static final String NEW_ARC_CHANGE_MESSAGE = "newArc";
   public static final String NEW_TOKEN_CHANGE_MESSAGE = "newToken";
   public static final String DELETE_TOKEN_CHANGE_MESSAGE = "deleteToken";
   public static final String NEW_RATE_PARAMETER_CHANGE_MESSAGE = "newRateParameter";
   public static final String DELETE_RATE_PARAMETER_CHANGE_MESSAGE = "deleteRateParameter";
   protected final PropertyChangeSupport changeSupport;
   private final FunctionalWeightParser functionalWeightParser;
   private final PetriNetComponentVisitor deleteVisitor;
   private final Map transitions;
   private final Map places;
   private final Map tokens;
   private final Map inboundArcs;
   private final Map outboundArcs;
   private final Map rateParameters;
   private final Map annotations;
   private final Multimap transitionOutboundArcs;
   private final Multimap transitionInboundArcs;
   private final Map componentMaps;
   private final PetriNetComponentVisitor addVisitor;
   public String pnmlName;
   private PetriNetName petriNetName;
   private boolean validated;

   public PetriNet(PetriNetName name) {
      this();
      this.petriNetName = name;
   }

   public PetriNet() {
      super();
      this.changeSupport = new PropertyChangeSupport(this);
      this.functionalWeightParser = new PetriNetWeightParser(new EvalVisitor(this), this);
      this.deleteVisitor = new PetriNetComponentRemovalVisitor(this);
      this.transitions = new HashMap();
      this.places = new HashMap();
      this.tokens = new HashMap();
      this.inboundArcs = new HashMap();
      this.outboundArcs = new HashMap();
      this.rateParameters = new HashMap();
      this.annotations = new HashMap();
      this.transitionOutboundArcs = HashMultimap.create();
      this.transitionInboundArcs = HashMultimap.create();
      this.componentMaps = new HashMap();
      this.addVisitor = new PetriNetComponentAddVisitor(this);
      this.pnmlName = "";
      this.validated = false;
      this.initialiseIdMap();
   }

   private void initialiseIdMap() {
      this.componentMaps.put(uk.ac.imperial.pipe.models.petrinet.Place.class, this.places);
      this.componentMaps.put(Transition.class, this.transitions);
      this.componentMaps.put(InboundArc.class, this.inboundArcs);
      this.componentMaps.put(OutboundArc.class, this.outboundArcs);
      this.componentMaps.put(uk.ac.imperial.pipe.models.petrinet.Token.class, this.tokens);
      this.componentMaps.put(RateParameter.class, this.rateParameters);
      this.componentMaps.put(Annotation.class, this.annotations);
   }

   public int hashCode() {
      int result = this.transitions.hashCode();
      result = 31 * result + this.places.hashCode();
      result = 31 * result + this.tokens.hashCode();
      result = 31 * result + this.inboundArcs.hashCode();
      result = 31 * result + this.outboundArcs.hashCode();
      result = 31 * result + this.annotations.hashCode();
      result = 31 * result + this.rateParameters.hashCode();
      result = 31 * result + (this.petriNetName != null ? this.petriNetName.hashCode() : 0);
      return result;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof PetriNet)) {
         return false;
      } else {
         PetriNet petriNet = (PetriNet)o;
         if (!CollectionUtils.isEqualCollection(this.annotations.values(), petriNet.annotations.values())) {
            return false;
         } else if (!CollectionUtils.isEqualCollection(this.inboundArcs.values(), petriNet.inboundArcs.values())) {
            return false;
         } else if (!CollectionUtils.isEqualCollection(this.outboundArcs.values(), petriNet.outboundArcs.values())) {
            return false;
         } else {
            label44: {
               if (this.petriNetName != null) {
                  if (this.petriNetName.equals(petriNet.petriNetName)) {
                     break label44;
                  }
               } else if (petriNet.petriNetName == null) {
                  break label44;
               }

               return false;
            }

            if (!CollectionUtils.isEqualCollection(this.places.values(), petriNet.places.values())) {
               return false;
            } else if (!CollectionUtils.isEqualCollection(this.rateParameters.values(), petriNet.rateParameters.values())) {
               return false;
            } else if (!CollectionUtils.isEqualCollection(this.tokens.values(), petriNet.tokens.values())) {
               return false;
            } else {
               return CollectionUtils.isEqualCollection(this.transitions.values(), petriNet.transitions.values());
            }
         }
      }
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      this.changeSupport.addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      this.changeSupport.removePropertyChangeListener(listener);
   }

   @XmlTransient
   public String getPnmlName() {
      return this.pnmlName;
   }

   public void setPnmlName(String pnmlName) {
      this.pnmlName = pnmlName;
   }

   @XmlTransient
   public boolean isValidated() {
      return this.validated;
   }

   public void setValidated(boolean validated) {
      this.validated = validated;
   }

   public void resetPNML() {
      this.pnmlName = null;
   }

   public void addPlace(uk.ac.imperial.pipe.models.petrinet.Place place) {
      if (!this.places.containsValue(place)) {
         this.places.put(place.getId(), place);
         place.addPropertyChangeListener(new PetriNet.NameChangeListener(place, this.places));
         this.changeSupport.firePropertyChange("newPlace", (Object)null, place);
      }

   }

   public Collection getPlaces() {
      return this.places.values();
   }

   public void removePlace(uk.ac.imperial.pipe.models.petrinet.Place place) throws PetriNetComponentException {
      Collection components = this.getComponentsReferencingId(place.getId());
      if (!components.isEmpty()) {
         throw new PetriNetComponentException("Cannot delete " + place.getId() + " it is referenced in a functional expression!");
      } else {
         this.places.remove(place.getId());
         Iterator i$ = this.outboundArcs(place).iterator();

         while(i$.hasNext()) {
            InboundArc arc = (InboundArc)i$.next();
            this.removeArc(arc);
         }

         this.changeSupport.firePropertyChange("deletePlace", place, (Object)null);
      }
   }

   private Collection getComponentsReferencingId(String componentId) {
      Set results = new HashSet();
      Iterator i$ = this.getTransitions().iterator();

      while(i$.hasNext()) {
         Transition transition = (Transition)i$.next();
         if (this.referencesId(transition.getRateExpr(), componentId)) {
            results.add(transition.getId());
         }
      }

      i$ = this.getArcs().iterator();

      while(true) {
         while(i$.hasNext()) {
            uk.ac.imperial.pipe.models.petrinet.Arc arc = (uk.ac.imperial.pipe.models.petrinet.Arc)i$.next();
            Iterator i$ = arc.getTokenWeights().values().iterator();

            while(i$.hasNext()) {
               String expr = (String)i$.next();
               if (this.referencesId(expr, componentId)) {
                  results.add(arc.getId());
                  break;
               }
            }
         }

         i$ = this.getRateParameters().iterator();

         while(i$.hasNext()) {
            RateParameter rateParameter = (RateParameter)i$.next();
            if (this.referencesId(rateParameter.getExpression(), componentId)) {
               results.add(rateParameter.getId());
            }
         }

         return results;
      }
   }

   private boolean referencesId(String expr, String id) {
      Collection components = this.getComponents(expr);
      return components.contains(id);
   }

   private Collection getComponents(String expression) {
      FunctionalResults results = this.parseExpression(expression);
      return results.getComponents();
   }

   public Collection outboundArcs(uk.ac.imperial.pipe.models.petrinet.Place place) {
      Collection outbound = new LinkedList();
      Iterator i$ = this.inboundArcs.values().iterator();

      while(i$.hasNext()) {
         InboundArc arc = (InboundArc)i$.next();
         if (((uk.ac.imperial.pipe.models.petrinet.Place)arc.getSource()).equals(place)) {
            outbound.add(arc);
         }
      }

      return outbound;
   }

   public void removeArc(InboundArc arc) {
      this.inboundArcs.remove(arc.getId());
      this.transitionInboundArcs.remove(((Transition)arc.getTarget()).getId(), arc);
      this.changeSupport.firePropertyChange("deleteArc", arc, (Object)null);
   }

   public void addTransition(Transition transition) {
      if (!this.transitions.containsValue(transition)) {
         this.transitions.put(transition.getId(), transition);
         transition.addPropertyChangeListener(new PetriNet.NameChangeListener(transition, this.transitions));
         transition.addPropertyChangeListener(new PetriNet.NameChangeArcListener());
         this.changeSupport.firePropertyChange("newTransition", (Object)null, transition);
      }

   }

   public void removeTransition(Transition transition) {
      this.transitions.remove(transition.getId());
      Iterator i$ = this.outboundArcs(transition).iterator();

      while(i$.hasNext()) {
         OutboundArc arc = (OutboundArc)i$.next();
         this.removeArc(arc);
      }

      this.transitionOutboundArcs.removeAll(transition.getId());
      this.transitionInboundArcs.removeAll(transition.getId());
      this.changeSupport.firePropertyChange("deleteTransition", transition, (Object)null);
   }

   public Collection outboundArcs(Transition transition) {
      return this.transitionOutboundArcs.get(transition.getId());
   }

   public void removeArc(OutboundArc arc) {
      this.outboundArcs.remove(arc.getId());
      this.transitionOutboundArcs.remove(((Transition)arc.getSource()).getId(), arc);
      this.changeSupport.firePropertyChange("deleteArc", arc, (Object)null);
   }

   public Collection getTransitions() {
      return this.transitions.values();
   }

   public void addArc(InboundArc inboundArc) {
      if (!this.inboundArcs.containsKey(inboundArc.getId())) {
         this.inboundArcs.put(inboundArc.getId(), inboundArc);
         this.transitionInboundArcs.put(((Transition)inboundArc.getTarget()).getId(), inboundArc);
         inboundArc.addPropertyChangeListener(new PetriNet.NameChangeListener(inboundArc, this.inboundArcs));
         this.changeSupport.firePropertyChange("newArc", (Object)null, inboundArc);
      }

   }

   public void addArc(OutboundArc outboundArc) {
      if (!this.outboundArcs.containsKey(outboundArc.getId())) {
         this.outboundArcs.put(outboundArc.getId(), outboundArc);
         this.transitionOutboundArcs.put(((Transition)outboundArc.getSource()).getId(), outboundArc);
         outboundArc.addPropertyChangeListener(new PetriNet.NameChangeListener(outboundArc, this.outboundArcs));
         this.changeSupport.firePropertyChange("newArc", (Object)null, outboundArc);
      }

   }

   public Collection getArcs() {
      Collection arcs = new LinkedList();
      arcs.addAll(this.getOutboundArcs());
      arcs.addAll(this.getInboundArcs());
      return arcs;
   }

   public Collection getOutboundArcs() {
      return this.outboundArcs.values();
   }

   public Collection getInboundArcs() {
      return this.inboundArcs.values();
   }

   public void addToken(uk.ac.imperial.pipe.models.petrinet.Token token) {
      if (!this.tokens.containsValue(token)) {
         this.tokens.put(token.getId(), token);
         token.addPropertyChangeListener(new PetriNet.NameChangeListener(token, this.tokens));
         token.addPropertyChangeListener(new PetriNet.TokenNameChanger());
         this.changeSupport.firePropertyChange("newToken", (Object)null, token);
      }

   }

   public void removeToken(uk.ac.imperial.pipe.models.petrinet.Token token) throws PetriNetComponentException {
      Collection referencedPlaces = this.getPlacesContainingToken(token);
      Collection referencedTransitions = this.getTransitionsReferencingToken(token);
      if (referencedPlaces.isEmpty() && referencedTransitions.isEmpty()) {
         this.tokens.remove(token.getId());
         this.changeSupport.firePropertyChange("deleteToken", token, (Object)null);
      } else {
         StringBuilder message = new StringBuilder();
         message.append("Cannot remove ").append(token.getId()).append(" token");
         Iterator i$;
         if (!referencedPlaces.isEmpty()) {
            message.append(" places: ");
            i$ = referencedPlaces.iterator();

            while(i$.hasNext()) {
               uk.ac.imperial.pipe.models.petrinet.Place place = (uk.ac.imperial.pipe.models.petrinet.Place)i$.next();
               message.append(place.getId());
            }

            message.append(" contains it\n");
         }

         if (!referencedTransitions.isEmpty()) {
            message.append(" transitions: ");
            i$ = referencedTransitions.iterator();

            while(i$.hasNext()) {
               Transition transition = (Transition)i$.next();
               message.append(transition.getId());
            }

            message.append(" reference it\n");
         }

         throw new PetriNetComponentException(message.toString());
      }
   }

   private Collection getPlacesContainingToken(uk.ac.imperial.pipe.models.petrinet.Token token) {
      Collection result = new LinkedList();
      Iterator i$ = this.places.values().iterator();

      while(i$.hasNext()) {
         uk.ac.imperial.pipe.models.petrinet.Place place = (uk.ac.imperial.pipe.models.petrinet.Place)i$.next();
         if (place.getTokenCount(token.getId()) > 0) {
            result.add(place);
         }
      }

      return result;
   }

   private Collection getTransitionsReferencingToken(Token token) {
      Collection result = new LinkedList();
      Iterator i$ = this.transitions.values().iterator();

      while(i$.hasNext()) {
         Transition transition = (Transition)i$.next();
         FunctionalResults results = this.functionalWeightParser.evaluateExpression(transition.getRateExpr());
         if (results.getComponents().contains(token.getId())) {
            result.add(transition);
         }
      }

      return result;
   }

   public Collection getTokens() {
      return this.tokens.values();
   }

   public void addAnnotation(Annotation annotation) {
      if (!this.annotations.containsKey(annotation.getId())) {
         this.annotations.put(annotation.getId(), annotation);
         annotation.addPropertyChangeListener(new PetriNet.NameChangeListener(annotation, this.annotations));
         this.changeSupport.firePropertyChange("newAnnotation", (Object)null, annotation);
      }

   }

   public void removeAnnotation(Annotation annotation) {
      this.annotations.remove(annotation.getId());
      this.changeSupport.firePropertyChange("deleteAnnotation", annotation, (Object)null);
   }

   public Collection getAnnotations() {
      return this.annotations.values();
   }

   public void addRateParameter(RateParameter rateParameter) throws InvalidRateException {
      if (!this.validFunctionalExpression(rateParameter.getExpression())) {
         throw new InvalidRateException(rateParameter.getExpression());
      } else {
         if (!this.rateParameters.containsValue(rateParameter)) {
            this.rateParameters.put(rateParameter.getId(), rateParameter);
            rateParameter.addPropertyChangeListener(new PetriNet.NameChangeListener(rateParameter, this.rateParameters));
            this.changeSupport.firePropertyChange("newRateParameter", (Object)null, rateParameter);
         }

      }
   }

   public boolean validFunctionalExpression(String expression) {
      FunctionalResults result = this.functionalWeightParser.evaluateExpression(expression);
      return !result.hasErrors();
   }

   public void removeRateParameter(RateParameter parameter) {
      this.removeRateParameterFromTransitions(parameter);
      this.rateParameters.remove(parameter.getId());
      this.changeSupport.firePropertyChange("deleteRateParameter", parameter, (Object)null);
   }

   private void removeRateParameterFromTransitions(RateParameter parameter) {
      Iterator i$ = this.transitions.values().iterator();

      while(i$.hasNext()) {
         Transition transition = (Transition)i$.next();
         if (transition.getRate().equals(parameter)) {
            Rate rate = new NormalRate(parameter.getExpression());
            transition.setRate(rate);
         }
      }

   }

   public Collection getRateParameters() {
      return this.rateParameters.values();
   }

   public void add(PetriNetComponent component) throws PetriNetComponentException {
      component.accept(this.addVisitor);
   }

   public void remove(PetriNetComponent component) throws PetriNetComponentException {
      if (this.contains(component.getId())) {
         component.accept(this.deleteVisitor);
      }

   }

   public boolean containsDefaultToken() {
      return this.tokens.containsKey("Default");
   }

   public boolean containsComponent(String id) {
      Iterator i$ = this.componentMaps.values().iterator();

      Map map;
      do {
         if (!i$.hasNext()) {
            return false;
         }

         map = (Map)i$.next();
      } while(!map.containsKey(id));

      return true;
   }

   public PetriNetComponent getComponent(String id, Class clazz) throws PetriNetComponentNotFoundException {
      Map map = this.getMapForClass(clazz);
      if (map.containsKey(id)) {
         return (PetriNetComponent)map.get(id);
      } else {
         throw new PetriNetComponentNotFoundException("No component " + id + " exists in Petri net.");
      }
   }

   private Map getMapForClass(Class clazz) {
      return (Map)this.componentMaps.get(clazz);
   }

   public Collection inboundArcs(Transition transition) {
      return this.transitionInboundArcs.get(transition.getId());
   }

   @XmlTransient
   public PetriNetName getName() {
      return this.petriNetName;
   }

   public void setName(PetriNetName name) {
      PetriNetName old = this.petriNetName;
      this.petriNetName = name;
      this.changeSupport.firePropertyChange("nameChange", old, name);
   }

   public String getNameValue() {
      return this.petriNetName.getName();
   }

   public FunctionalResults parseExpression(String expr) {
      return this.functionalWeightParser.evaluateExpression(expr);
   }

   public Set getComponentIds() {
      Set results = new HashSet();
      Iterator i$ = this.componentMaps.values().iterator();

      while(i$.hasNext()) {
         Map entry = (Map)i$.next();
         results.addAll(entry.keySet());
      }

      return results;
   }

   public boolean contains(String id) {
      return this.getComponentIds().contains(id);
   }

   // $FF: synthetic class
   static class SyntheticClass_1 {
   }

   private class TokenNameChanger implements PropertyChangeListener {
      private TokenNameChanger() {
         super();
      }

      public void propertyChange(PropertyChangeEvent evt) {
         if (evt.getPropertyName().equals("id")) {
            String oldId = (String)evt.getOldValue();
            String newId = (String)evt.getNewValue();
            this.changePlaceTokens(oldId, newId);
            this.changeArcTokens(oldId, newId);
         }

      }

      private void changePlaceTokens(String oldId, String newId) {
         Iterator i$ = PetriNet.this.getPlaces().iterator();

         while(i$.hasNext()) {
            uk.ac.imperial.pipe.models.petrinet.Place place = (Place)i$.next();
            int count = place.getTokenCount(oldId);
            place.removeAllTokens(oldId);
            place.setTokenCount(newId, count);
         }

      }

      private void changeArcTokens(String oldId, String newId) {
         Iterator i$ = PetriNet.this.getArcs().iterator();

         while(i$.hasNext()) {
            uk.ac.imperial.pipe.models.petrinet.Arc arc = (Arc)i$.next();
            if (arc.getTokenWeights().containsKey(oldId)) {
               String weight = arc.getWeightForToken(oldId);
               arc.removeAllTokenWeights(oldId);
               arc.setWeight(newId, weight);
            }
         }

      }

      // $FF: synthetic method
      TokenNameChanger(PetriNet.SyntheticClass_1 x1) {
         this();
      }
   }

   private class NameChangeArcListener implements PropertyChangeListener {
      private NameChangeArcListener() {
         super();
      }

      public void propertyChange(PropertyChangeEvent evt) {
         if (evt.getPropertyName().equals("id")) {
            String oldId = (String)evt.getOldValue();
            String newId = (String)evt.getNewValue();
            Collection inbound = PetriNet.this.transitionInboundArcs.removeAll(oldId);
            Collection outbound = PetriNet.this.transitionOutboundArcs.removeAll(oldId);
            PetriNet.this.transitionInboundArcs.putAll(newId, inbound);
            PetriNet.this.transitionOutboundArcs.putAll(newId, outbound);
         }

      }

      // $FF: synthetic method
      NameChangeArcListener(PetriNet.SyntheticClass_1 x1) {
         this();
      }
   }

   private static class NameChangeListener implements PropertyChangeListener {
      private final PetriNetComponent component;
      private final Map componentMap;

      public NameChangeListener(PetriNetComponent component, Map componentMap) {
         super();
         this.component = component;
         this.componentMap = componentMap;
      }

      public void propertyChange(PropertyChangeEvent evt) {
         if (evt.getPropertyName().equals("id")) {
            String oldId = (String)evt.getOldValue();
            String newId = (String)evt.getNewValue();
            this.componentMap.remove(oldId);
            this.componentMap.put(newId, this.component);
         }

      }
   }
}
