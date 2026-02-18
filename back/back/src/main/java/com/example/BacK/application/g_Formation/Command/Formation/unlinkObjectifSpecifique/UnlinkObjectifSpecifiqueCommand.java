package com.example.BacK.application.g_Formation.Command.Formation.unlinkObjectifSpecifique;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnlinkObjectifSpecifiqueCommand {
    private Long formationId;
    private Long objectifSpecifiqueId;
}
