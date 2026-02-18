package com.example.BacK.infrastructure.services.g_Utilisateur;
import com.example.BacK.application.g_Utilisateur.Query.user.GetUserQuery;
import com.example.BacK.application.g_Utilisateur.Query.user.GetUserResponse;
import com.example.BacK.application.g_Utilisateur.Query.userStats.GetUserStatsResponse;
import com.example.BacK.application.interfaces.g_Utilisateur.user.IUserRepositoryService;
import com.example.BacK.application.models.PageResponse;
import com.example.BacK.domain.g_Utilisateurs.Role;
import com.example.BacK.domain.g_Utilisateurs.User;
import com.example.BacK.domain.g_Utilisateurs.UserStatus;
import com.example.BacK.infrastructure.repository.g_Utilisateur.RoleRepository;
import com.example.BacK.infrastructure.repository.g_Utilisateur.UserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRepositoryService implements IUserRepositoryService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserRepositoryService(UserRepository userRepository,
            RoleRepository roleRepository,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public String add(User user, Set<String> roleIds) {
        // Validation
        if (userRepository.existsByNomUtilisateur(user.getNomUtilisateur())) {
            throw new IllegalArgumentException("Le nom d'utilisateur '" + user.getNomUtilisateur() + "' existe déjà");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("L'email '" + user.getEmail() + "' existe déjà");
        }

        // Encode password
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        if (user.getStatut() == null) {
            user.setStatut(UserStatus.EN_ATTENTE);
        }
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
        }

        user.setId(null);
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Override
    @Transactional
    public void update(User user, Set<String> roleIds, String newPassword) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + user.getId()));
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            if (!u.getId().equals(user.getId())) {
                throw new IllegalArgumentException("L'email '" + user.getEmail() + "' est déjà utilisé");
            }
        });
        existingUser.setEmail(user.getEmail());
        existingUser.setPrenom(user.getPrenom());
        existingUser.setNom(user.getNom());
        existingUser.setNumeroTelephone(user.getNumeroTelephone());
        existingUser.setDepartement(user.getDepartement());
        existingUser.setPoste(user.getPoste());
        existingUser.setStatut(user.getStatut());
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            existingUser.setMotDePasse(passwordEncoder.encode(newPassword));
        }
        if (roleIds != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            existingUser.setRoles(roles);
        }

        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void assignRole(String userId, String roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé"));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRole(String userId, String roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        user.getRoles().removeIf(r -> r.getId().equals(roleId));
        userRepository.save(user);
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetUserResponse> getById(GetUserQuery query) {
        Optional<User> user = userRepository.findById(query.getId());
        if (user.isPresent()) {
            return Collections.singletonList(mapToResponse(user.get()));
        }
        return Collections.emptyList();
    }

    @Override
    public List<GetUserResponse> search(GetUserQuery query) {
        List<User> users = userRepository.searchUsers(
                query.getSearch(),
                query.getStatut(),
                Pageable.unpaged()).getContent();

        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetUserResponse> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<GetUserResponse> getAllPaginated(GetUserQuery query) {
        Sort sort = createSort(query);
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), sort);

        Page<User> userPage = userRepository.findAll(pageable);

        List<GetUserResponse> content = userPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        PageResponse<GetUserResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setPageNumber(userPage.getNumber());
        response.setPageSize(userPage.getSize());
        response.setTotalElements(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());
        response.setFirst(userPage.isFirst());
        response.setLast(userPage.isLast());
        response.setEmpty(userPage.isEmpty());

        return response;
    }

    @Override
    public PageResponse<GetUserResponse> searchPaginated(GetUserQuery query) {
        Sort sort = createSort(query);
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), sort);

        Page<User> userPage = userRepository.searchUsers(
                query.getSearch(),
                query.getStatut(),
                pageable);

        List<GetUserResponse> content = userPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        PageResponse<GetUserResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setPageNumber(userPage.getNumber());
        response.setPageSize(userPage.getSize());
        response.setTotalElements(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());
        response.setFirst(userPage.isFirst());
        response.setLast(userPage.isLast());
        response.setEmpty(userPage.isEmpty());

        return response;
    }

    @Override
    public GetUserStatsResponse getUserStats() {
        List<User> users = userRepository.findAll();

        GetUserStatsResponse stats = new GetUserStatsResponse();
        stats.setTotalUsers((long) users.size());
        stats.setActiveUsers(users.stream().filter(u -> u.getStatut() == UserStatus.ACTIF).count());
        stats.setInactiveUsers(users.stream().filter(u -> u.getStatut() == UserStatus.INACTIF).count());
        stats.setSuspendedUsers(users.stream().filter(u -> u.getStatut() == UserStatus.SUSPENDU).count());
        stats.setPendingUsers(users.stream().filter(u -> u.getStatut() == UserStatus.EN_ATTENTE).count());
        stats.setTotalRoles(roleRepository.count());

        return stats;
    }

    private GetUserResponse mapToResponse(User user) {
        GetUserResponse response = modelMapper.map(user, GetUserResponse.class);
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            response.setRoles(user.getRoles().stream()
                    .map(role -> modelMapper.map(role,
                            com.example.BacK.application.models.g_Utilisateur.RoleDTO.class))
                    .collect(Collectors.toSet()));
        }
        return response;
    }

    private Sort createSort(GetUserQuery query) {
        if (query.getSortBy() != null && query.getSortDirection() != null) {
            return query.getSortDirection().equalsIgnoreCase("DESC")
                    ? Sort.by(query.getSortBy()).descending()
                    : Sort.by(query.getSortBy()).ascending();
        }
        return Sort.unsorted();
    }
}
