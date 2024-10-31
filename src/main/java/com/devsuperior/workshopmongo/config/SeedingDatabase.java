package com.devsuperior.workshopmongo.config;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.devsuperior.workshopmongo.entities.Post;
import com.devsuperior.workshopmongo.entities.User;
import com.devsuperior.workshopmongo.repositories.PostRepository;
import com.devsuperior.workshopmongo.repositories.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class SeedingDatabase implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Override
	public void run(String... args) throws Exception {
		/*
		userRepository.deleteAll();
		postRepository.deleteAll();
		*/

		Mono<Void> deleteUsers = userRepository.deleteAll();
		Mono<Void> deletePosts = postRepository.deleteAll();

		deleteUsers.then(deletePosts).subscribe();

		User maria = new User(null, "Maria Brown", "maria@gmail.com");
		User alex = new User(null, "Alex Green", "alex@gmail.com");
		User bob = new User(null, "Bob Grey", "bob@gmail.com");

		/*userRepository.saveAll(Arrays.asList(maria, alex, bob));*/
		Flux<User> insertUsers = userRepository.saveAll(Arrays.asList(maria, alex, bob));

		insertUsers
				.thenMany(userRepository.findAll())
				.doOnNext(user -> System.out.println("Usuário salvo: " + user))
				.then(
						Mono.defer(() -> {

							Mono<User> mariaFromDb = userRepository.searchEmail("maria@gmail.com");
							Mono<User> alexFromDb = userRepository.searchEmail("alex@gmail.com");
							Mono<User> bobFromDb = userRepository.searchEmail("bob@gmail.com");

							return Mono.zip(mariaFromDb, alexFromDb, bobFromDb);
						})
				)
				.subscribe(users -> {
					User mariaSaved = users.getT1();
					User alexSaved = users.getT2();
					User bobSaved = users.getT3();

					Post post1 = new Post(null, Instant.parse("2022-11-21T18:35:24.00Z"), "Partiu viagem",
							"Vou viajar para São Paulo. Abraços!", mariaSaved.getId(), mariaSaved.getName());
					Post post2 = new Post(null, Instant.parse("2022-11-23T17:30:24.00Z"), "Bom dia", "Acordei feliz hoje!",
							mariaSaved.getId(), mariaSaved.getName());


					post1.addComment("Boa viagem mano!", Instant.parse("2022-11-21T18:52:24.00Z"), alex.getId(), alex.getName());
					post1.addComment("Aproveite!", Instant.parse("2022-11-22T11:35:24.00Z"), bob.getId(), bob.getName());

					post2.addComment("Tenha um ótimo dia!", Instant.parse("2022-11-23T18:35:24.00Z"), alex.getId(), alex.getName());

					post1.setUser(mariaSaved);
					post2.setUser(mariaSaved);

					Flux<Post> insertPosts = postRepository.saveAll(Arrays.asList(post1, post2));
					insertPosts.subscribe();
				});


//		//toFuture converterá um tipo reativo Mono ou Flux em um CompletableFuture,
//		//o CompletableFuture é usado para tratar operações assíncronas e permite esperar
//		//pela conclusão de uma tarefa
//		maria = userRepository.searchEmail("maria@gmail.com").toFuture().get();
//		alex = userRepository.searchEmail("maria@gmail.com").toFuture().get();
//		bob = userRepository.searchEmail("maria@gmail.com").toFuture().get();
	}

}
