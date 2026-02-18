package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.copyAndLink;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CopyAndLinkObjectifSpecifiqueCommand {
    private Long objectifSpecifiqueId;  // The existing objective to copy
    private Long objectifGlobalId;       // The global objective to link it to
    private Long formationId;            // The formation to link it to
}
