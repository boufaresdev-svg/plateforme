package com.example.BacK.application.g_Formation.Command.Formation.unlinkObjectifGlobal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnlinkObjectifGlobalCommand {
    private Long formationId;
    private Long objectifGlobalId;
}
