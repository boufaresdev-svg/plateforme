package com.example.BacK.application.g_Formation.Query.formation.formationById;

import com.example.BacK.domain.g_Formation.Formation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFormationByIdResponse {

    private Formation formation;

}

