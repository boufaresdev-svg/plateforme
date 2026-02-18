package com.example.BacK.infrastructure.services.g_Projet;

import com.example.BacK.application.g_Projet.Query.mission.all.GetMissionResponse;
import com.example.BacK.application.interfaces.g_Projet.mission.IMissionRepositoryService;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.infrastructure.repository.g_Projet.MissionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MissionRepositoryService implements IMissionRepositoryService {

    private final MissionRepository missionRepository;
    private final ModelMapper mapper;

    public MissionRepositoryService(MissionRepository missionRepository, ModelMapper mapper) {
        this.missionRepository = missionRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Mission mission) {
        mission.setId(null); // ID null pour création
        missionRepository.save(mission);
        return "ok";
    }

    @Override
    public void update(Mission mission) {
        if (!missionRepository.existsById(mission.getId())) {
            throw new IllegalArgumentException("Mission ID not found");
        }
        missionRepository.save(mission);
    }

    @Override
    public void delete(String id) {
        missionRepository.deleteById(id);
    }

    @Override
    public Mission get(String id) {
        return missionRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetMissionResponse> getall() {
        List<Mission> missions = missionRepository.findAll();
        return missions.stream()
                .map(m -> mapper.map(m, GetMissionResponse.class))
                .toList();
    }
}
