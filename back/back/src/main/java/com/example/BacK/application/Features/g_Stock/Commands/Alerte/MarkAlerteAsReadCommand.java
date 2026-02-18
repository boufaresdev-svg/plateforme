package com.example.BacK.application.Features.g_Stock.Commands.Alerte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarkAlerteAsReadCommand {
    private String alerteId;
}
