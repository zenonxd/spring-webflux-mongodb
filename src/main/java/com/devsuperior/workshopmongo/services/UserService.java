package com.devsuperior.workshopmongo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.workshopmongo.dto.PostDTO;
import com.devsuperior.workshopmongo.dto.UserDTO;
import com.devsuperior.workshopmongo.entities.User;
import com.devsuperior.workshopmongo.repositories.UserRepository;
import com.devsuperior.workshopmongo.services.exceptions.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public Flux<UserDTO> findAll() {
		return repository.findAll().map(UserDTO::new);
	}

	public Mono<UserDTO> findById(String id) {
		return repository.findById(id)
				.map(UserDTO::new)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Id não encontrado")));
	}

	public Mono<UserDTO> insert(UserDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		return repository.save(entity).map(UserDTO::new);
	}

	public Mono<UserDTO> update(String id, UserDTO dto) {
		return repository.findById(id)
				.flatMap(exinstingUser -> {
					//modificamos os dados do User que está no banco
					exinstingUser.setName(dto.getName());
					exinstingUser.setEmail(dto.getEmail());
					return repository.save(exinstingUser);
				})
				//transformamos por fim em um Mono de UserDTO.
				.map(UserDTO::new)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")));
	}

	public Mono<Void> delete(String id) {
		//verificamos se o User existe
		//se ele não existir, lançará a exception
		return repository.findById(id)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")))
				//se ele existir, ele vai retornar um Mono de User
				//iremos transformar esse Mono de User em Void para deletar
				//o usuário
				.flatMap(existingUser -> repository.delete(existingUser));
	}

	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
	}

}
