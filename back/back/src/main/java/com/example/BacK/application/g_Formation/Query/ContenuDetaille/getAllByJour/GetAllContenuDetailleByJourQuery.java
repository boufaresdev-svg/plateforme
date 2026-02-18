package com.example.BacK.application.g_Formation.Query.ContenuDetaille.getAllByJour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get all ContenuDetaille for a specific jour formation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllContenuDetailleByJourQuery {

    private Long idJourFormation;
}
