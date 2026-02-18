package com.example.BacK.application.g_Formation.Query.ObjectifSpecifique.ObjectifsByContenu;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetObjectifsByContenuHandler")
public class GetObjectifsByContenuHandler implements RequestHandler<GetObjectifsByContenuQuery, List<GetObjectifsByContenuResponse>> {

    private final ObjectifSpecifiqueRepositoryService objectifRepo;

    public GetObjectifsByContenuHandler(ObjectifSpecifiqueRepositoryService objectifRepo) {
        this.objectifRepo = objectifRepo;
    }

    @Override
    public List<GetObjectifsByContenuResponse> handle(GetObjectifsByContenuQuery query) {

        List<ObjectifSpecifique> objectifs =
                objectifRepo.getObjectifsByContenu(query.getIdContenu());

        return objectifs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetObjectifsByContenuResponse mapToResponse(ObjectifSpecifique obj) {
        GetObjectifsByContenuResponse res = new GetObjectifsByContenuResponse();
        res.setIdObjectifSpec(obj.getIdObjectifSpec());
        res.setTitre(obj.getTitre());
        res.setDescription(obj.getDescription());
        return res;
    }
}
