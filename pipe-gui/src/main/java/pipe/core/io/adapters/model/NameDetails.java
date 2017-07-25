package pipe.core.io.adapters.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public final class NameDetails {
   @XmlElement(
      name = "value"
   )
   private String name;
   @XmlElement
   private OffsetGraphics graphics = new OffsetGraphics();

   public NameDetails() {
      super();
   }

   public OffsetGraphics getGraphics() {
      return this.graphics;
   }

   public void setGraphics(OffsetGraphics graphics) {
      this.graphics = graphics;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
