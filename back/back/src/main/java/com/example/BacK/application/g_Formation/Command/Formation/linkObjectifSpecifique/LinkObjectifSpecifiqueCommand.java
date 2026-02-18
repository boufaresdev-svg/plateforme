package com.example.BacK.application.g_Formation.Command.Formation.linkObjectifSpecifique;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkObjectifSpecifiqueCommand {
    private Long formationId;
    private Long objectifSpecifiqueId;
}
