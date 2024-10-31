package com.devsuperior.workshopmongo.repositories;

import java.time.Instant;
import java.util.List;

import com.devsuperior.workshopmongo.dto.PostDTO;
import org.springframework.data.mongodb.repository.Query;

import com.devsuperior.workshopmongo.entities.Post;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PostRepository extends ReactiveMongoRepository<Post, String> {
	
	@Query("{ 'title': { $regex: ?0, $options: 'i' } }")
	List<Post> searchTitle(String text);
	
    Flux<Post> findPostByTitleIgnoreCase(String text);

	@Query("{ $and: [ { date: {$gte: ?1} }, { date: { $lte: ?2} } , { $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'body': { $regex: ?0, $options: 'i' } }, { 'comments.text': { $regex: ?0, $options: 'i' } } ] } ] }")
	Flux<Post> fullSearch(String text, Instant minDate, Instant maxDate);

	Flux<Post> findPostsByUserId(String id);
}
