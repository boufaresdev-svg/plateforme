package com.example.BacK.application.g_Stock.Command.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteArticleCommandResponse {
    private boolean success;
    private String message;
}
