package com.example.BacK.application.g_Formation.Query.formation.formationById;

import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component
public class GetFormationByIdHandler implements RequestHandler<GetFormationByIdQuery, GetFormationByIdResponse> {

    @Override
    public GetFormationByIdResponse handle(GetFormationByIdQuery command) {
        return null;
    }
}
