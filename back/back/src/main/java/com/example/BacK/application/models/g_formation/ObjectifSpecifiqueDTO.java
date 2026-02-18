package com.example.BacK.application.models.g_formation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectifSpecifiqueDTO {

    private Long idObjectifSpec;
    private String titre;
    private String description;
    private Long idObjectifGlobal;  // Link to parent global objective
    private List<ContenuJourFormationDTO> contenus;
    
    public ObjectifSpecifiqueDTO(Long idObjectifSpec, String titre, String description) {
        this.idObjectifSpec = idObjectifSpec;
        this.titre = titre;
        this.description = description;
    }
}
