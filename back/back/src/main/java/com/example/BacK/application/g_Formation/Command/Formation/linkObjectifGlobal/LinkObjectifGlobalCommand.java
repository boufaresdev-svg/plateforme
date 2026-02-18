package com.example.BacK.application.g_Formation.Command.Formation.linkObjectifGlobal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkObjectifGlobalCommand {
    private Long formationId;
    private Long objectifGlobalId;
}
