package com.example.BacK.application.g_Formation.Query.Apprenant.CheckEnrollment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckApprenantEnrollmentQuery {
    private Long apprenantId;
    private Long classeId;
}
