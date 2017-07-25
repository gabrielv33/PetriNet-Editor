package pipe.core.models.petrinet.name;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import uk.ac.imperial.pipe.models.petrinet.name.FileNameVisitor;
import uk.ac.imperial.pipe.models.petrinet.name.NameVisitor;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetName;

public final class PetriNetFileName implements PetriNetName {
   private File file;

   public PetriNetFileName(File file) {
      super();
      this.file = file;
   }

   public String getPath() {
      return this.file.getAbsolutePath();
   }

   public String getName() {
      return FilenameUtils.removeExtension(this.file.getName());
   }

   public void visit(NameVisitor visitor) {
      if (visitor instanceof uk.ac.imperial.pipe.models.petrinet.name.FileNameVisitor) {
         ((FileNameVisitor)visitor).visit(this);
      }

   }

   public File getFile() {
      return this.file;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof PetriNetFileName)) {
         return false;
      } else {
         PetriNetFileName that = (PetriNetFileName)o;
         return this.file.equals(that.file);
      }
   }

   public int hashCode() {
      return this.file.hashCode();
   }
}
