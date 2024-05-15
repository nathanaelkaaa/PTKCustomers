package com.example.springapi.service;

import com.example.springapi.api.dto.UserDTO;
import com.example.springapi.persistence.entity.UserEntity;
import com.example.springapi.persistence.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserDTO> getUser(Integer id) {
        Optional<UserEntity> userFromDB = userRepository.getUserEntityById(id);

        return userFromDB.map(this::userEntityMapper);
    }

    public List<UserDTO> getUsers() {
        List<UserEntity> usersFromDB = userRepository.findAll().stream().limit(10).collect(Collectors.toList());

        return usersFromDB.stream().map(this::userEntityMapper).collect(Collectors.toList());
    }

    public void postUser(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(generateId()); // Générer un nouvel identifiant
        userEntity.setName(userDTO.getName());
        userEntity.setAge(userDTO.getAge());
        userEntity.setEmail(userDTO.getEmail());
        userRepository.save(userEntity);
    }

    public void putUser(Integer id, UserDTO userDTO) {
        Optional<UserEntity> optionalUserEntity = userRepository.getUserEntityById(id);
        optionalUserEntity.ifPresent(userEntity -> {
            userEntity.setName(userDTO.getName());
            userEntity.setAge(userDTO.getAge());
            userEntity.setEmail(userDTO.getEmail());
            userRepository.save(userEntity);
        });
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    private UserDTO userEntityMapper(UserEntity userEntity){
        return UserDTO.builder()
                .name(userEntity.getName())
                .age(userEntity.getAge())
                .email(userEntity.getEmail())
                .build();
    }

    private int generateId() {
        // Implémentez la logique pour générer un identifiant unique, par exemple en utilisant un compteur ou une méthode de génération d'identifiant unique
        // Pour cet exemple, vous pouvez simplement utiliser un identifiant incrémental
        return Math.toIntExact(userRepository.count() + 1);
    }
}
