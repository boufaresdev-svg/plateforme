package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.copyAndLink;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CopyAndLinkContenuJourCommand {
    private Long contenuJourId;          // The existing contenu jour to copy
    private Long objectifSpecifiqueId;   // The objectif specifique to link it to
    private Long formationId;            // The formation context
    private Long planFormationId;        // The plan formation to link it to
    private Integer niveau;              // Level: 1 = Débutant, 2 = Intermédiaire, 3 = Avancé (optional)
    private String niveauLabel;          // Level label: DEBUTANT, INTERMEDIAIRE, AVANCE (optional)

    // Constructor without niveau for backward compatibility
    public CopyAndLinkContenuJourCommand(Long contenuJourId, Long objectifSpecifiqueId, Long formationId, Long planFormationId) {
        this.contenuJourId = contenuJourId;
        this.objectifSpecifiqueId = objectifSpecifiqueId;
        this.formationId = formationId;
        this.planFormationId = planFormationId;
        this.niveau = null;
        this.niveauLabel = null;
    }
}
