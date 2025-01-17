<p align="center">
  <img src="https://img.shields.io/static/v1?label=Spring Essential - Dev Superior&message=Spring Webflux - MongoDB&color=8257E5&labelColor=000000" alt="workshop mongodb" />
</p>

# Objetivo

Motivação para Spring WebFlux e alguns conceitos chaves como programação reativa, programação síncrona e assíncrona e as
diferenças entre Spring MVC e WebFlux.

Implementar Back end reativo com Spring WebFlux e MongoDB.

# Resumo

Caso o cluster fique inativo, vá no Cloud do Mongo, dá um terminate e faça a conexão novamente no properties (a string)
de conexão pode mudar.

Não existe mais referência de entidades para o Banco de Dados (como colocar em User uma lista de Posts). Agora, nós
fazemos a referência manual com @DocumentReference. [Veja](#relacionamento-de-entidades-user-e-post)

# Introdução

A principal motivação para o surgimento de aplicações reativas é a necessidade de escalar aplicações.

Escalar significa que a aplicação precisa ser preparada para suportar um número maior de requisições, ou seja, um maior
processamento.

Em geral, **quanto maior o consumo de uma aplicação**, mais a gente acaba pagando por ela.

**Consumo = número de requisições.**

Um ótimo exemplo é a Black Friday ou semana de natal, onde aumenta-se muito o número de acessos e requisições.

Este infográfico é um ótimo exemplo visual do que acontece na internet em apenas um minuto (2021):

![img.png](img.png)

# Programação reativa

Entendemos no tópico anterior que o motivo do surgimento da programação reativa foi, por exemplo, altos índices de 
requisições, demandando um maior processamento.

## O que define a programação reativa?

A utilização de streams (fluxo de dados). Dentro do contexto de programação reativa, TUDO é stream e estamos trabalhando
com operações assíncronas.

Operações assíncronas = requisições que podem ser processadas e executadas de maneira paralela, não precisamos terminar
uma requisição para atender outra.

## Padrão Observer

Veja o exemplo abaixo:

Temos um Publisher (uma entidade) que publicará os dados. 

Esses dados serão trocados por streams (fluxos de dados).

A partir dessas streams, conseguimos manusear esses dados concatenando, por exemplo, com outras streams.

E também teremos o Subscriber (outra entidade). Esse Subscriber irá se inscrever e quando essa stream tiver pronta, 
o Subscriber irá CONSUMIR essa stream.

![img_1.png](img_1.png)

A ideia de trabalhar com Publisher e Subscriber é permitir que o usuário se inscreva e assim que a informação estiver
disponível, o mesmo é notificado, podendo então consumir este dado.

Um ótimo exemplo disso, é o Youtube com suas livestreams. Imagine que o Professor Nélio vá fazer uma live em uma 
quinta-feira. Você pode ir até o YouTube e clicar no botão "Receber notificações".

O Youtube do professor Nélio neste caso, é o Publisher (publicará este evento, onde o mesmo ficará disponível) e nos
notificará. Onde assim, podemos notificar (acessar) essa live.

## Características programação reativa

### São assíncronas

Trabalham de maneira simultânea e não bloqueante. Para entendermos isso, iremos comparar a maneira assíncrona com síncrona.

#### Modelo síncrono (bloqueante)

![img_2.png](img_2.png)

Temos 03 requisições para serem processadas (amarela, roxa e azul).

A roxa só será processada após o término da requisição 1 (amarela) que chegou primeiro. Mesma coisa com o azul.

#### Modelo assíncrono (não bloqueante)

![img_3.png](img_3.png)

Já no modelo assíncrono, a requisição roxa está sendo processada (e até mesmo finalizada) mesmo com a amarela em
processamento.

Seguiremos o modelo assíncrona com o Webflux.

# Spring MVC x Spring Webflux

Em resumo: O Spring Webflux é a versão reativa, assíncrona e não bloqueante do Spring MVC. 

Note as diferenças:

## Spring MVC

O **spring-webmvc** é baseado na API do servlet do Java. Como característica, essa API **é síncrona e bloqueante**.

Uma outra coisa é container do servidor de aplicação. A do Spring MVC roda no Servlet Container (Tomcat), que por padrão
é síncrono e bloqueante.

![img_4.png](img_4.png)

Essa imagem é um modelo de como o Spring MVC trata as requisições. ⬇️

![img_5.png](img_5.png)

Quando o Request#1 bater na Representation Layer (Controller), será criado uma Servlet thread#1. Essa thread será enviada
para a DB Layer (Repository) e ficará bloqueada (veja o blocked). Somente quando houver a resposta de volta, ela será 
desbloqueada (por isso ela é sincrona e bloqueante).

Somente depois da thread#1 ser respondida que a Request#2 será iniciada.

## Spring Webflux

Já o **spring-webflux** é baseado nas reactive streams, ou seja, assíncronas e não bloqueantes.

Seu servidor padrão é o Netty, ele é totalmente reativo! Ele funciona com o princípio de "event loop", onde veremos
depois.

![img_4.png](img_4.png)

❗As versões mais atualizadas do Tomcat, permite que o mesmo seja trabalhado com webflux, mas por padrão utilizaremos o
Netty.

Essa imagem é um modelo de como o Spring Webflux trata as requisições. ⬇️

![img_6.png](img_6.png)

Temos o mesmo Request#1 do exemplo do MVC. 

Quando esse request bate no Controller (representation layer), será transformado em um evento (ou uma stream). Esse
evento será jogado num looping (um laço) onde será tratado pelo Webflux no event loop (uma fila).

Event Loop: pode ser que cheguem várias requisições de maneira simultânea. O Webflux tentará instanciar o mínimo possível 
de threads. Essas requisições ficarão nessa fila até que sejam processadas.

Para que o event handler funcione de maneira REATIVA, o nosso DB também precisa ser reativo. Como um banco não relacional
(MongoDB). Precisamos prover dados reativos para esse banco, para que todo o processo ocorra de maneira reativa também.

### Spring Data Webflux MongoDB

Antes se fosse um banco de dados Mongo, implementaríamos a sua interface com MongoRepository. Agora será com Reactive:

![img_7.png](img_7.png)

O que muda, é o retorno. Repare no findById.

Retornaremos um tipo genérico e o seu retorno será Mono. Esse retorno é para quando será retornado no máximo até 1 objeto.

O findAll por sua vez, retornará um Flux (0 ou N elementos).

O save também retorna mono (um único elemento).

# Apresentação projeto

Teremos a nossa fonte de dados (data source), trabalhando com o MongoDB (reativo).

Trocaremos o Spring MVC da aplicação para Webflux.

![img_8.png](img_8.png)

Temos a camada do Repository que fará a comunicação com o banco de dados. E o repository retornará um tipo de dado flux
para o Controller (teremos o service no meio de campo para regras de negócio).

## Configurando MongoDB

Entrar em [Cloud MongoDB](https://cloud.mongodb.com) e se registrar.

Ir em deploy cluster.

Usaremos Google Cloud! O nome será google-cloud-webflux-mongo.

Criar um user e colocar em Security o nosso IP (exclua o atual).

Iremos para o MongoDB Compass (para visualizar o banco de dados).

No site do Mongo, ir em connect e selecionar o Compass.

![img_9.png](img_9.png)

No MongoDB compass, conectar usando a connection string fornecida no site e passe a senha do user.

Agora, conectaremos na IDE.

Vá em application.properties, e troque pela url fornecida aqui:

![img_10.png](img_10.png)

Além disso, crie também uma database:

![img_11.png](img_11.png)

E depois, vá no MongoDB Compass > conexão > create database. Crie uma collection de user também (uma das entidades do
projeto).

![img_12.png](img_12.png)

Iniciando o projeto, a ideia é que a Collection de user criada esteja mapeada com os dados do TestConfig.

![img_13.png](img_13.png)

## Incluindo dependência e alterando repositories

### Dependência

Para modificar de MVC para Webflux, precisamos adicionar algumas dependências:

O spring data mongodb, virará reactive.

E o springweb, virará webflux.

Ao trocar essas dependências, terá alguns erros de código no projeto que iremos aos poucos adequar.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### Repositories

Atualmente nossos Repositories extendem do MongoRepository, será ReactiveMongoRepository, onde agora trabalharemos
com Mono e Flux <T>.

### ControllerException

![img_14.png](img_14.png)

Lembra do servlet do MVC? Não trabalharemos mais com ele e sim com ServerHttpRequest.

![img_15.png](img_15.png)

### Seeding dados com Reactive MongoDB

Quando trabalhamos com programação reativa no Spring Webflux, teremos dois objetos que representarão streams: do tipo 
Mono (0 ou 1 elemento) e Flux (0 ou N elementos).

❗Lembrar sempre do subscribe após realocar em uma variável.

deleteAll:
-
Seu retorno é do tipo mono e void! Mas esse void é encapsulado por uma stream (mono), ou seja, 0 ou 1 objeto somente.

A ideia é obtermos esse retorno de Mono, e dar um subscribe (inscrever neste evento).

![img_16.png](img_16.png)

### Após alteração

![img_17.png](img_17.png)

saveAll
-

![img_18.png](img_18.png)

Seu retorno é do tipo Flux e um Iterable.

### Após alteração

![img_19.png](img_19.png)

insertPosts
-

Mesma coisa, é um saveAll. Retornará um flux do tipo iterable (Post).

saveUser
-

Save retornará Mono do tipo User.

![img_21.png](img_21.png)

![img_20.png](img_20.png)

### Relacionamento de entidades (User e Post)

Na nossa entidade User, temos uma referência a Post, possuindo uma lista do mesmo. Visto que um User pode ter vários
Posts.

![img_22.png](img_22.png)

No Webflux não é muito comum usarmos esse @DBRef.

A ideia é que a referência seja feita de maneira manual. Nós iremos REMOVER a lista de Posts em User e iremos inserir
uma REFERÊNCIA do User no Post, usando @DocumentReference! Veja:

![img_23.png](img_23.png)

## Alteração nos Endpoints

Lembrar de baixar a Collection do Postman.

## User

#### findAll

#### Service

##### Antes

```java
@Transactional(readOnly = true)
public List<UserDTO> findAll() {
    List<UserDTO> result = repository.findAll().stream().map(x -> new UserDTO(x)).toList();
    return result;
}
```

##### Depois

```java
public Flux<UserDTO> findAll() {
    return repository.findAll().map(UserDTO::new);
}
```

### Controller

##### Antes

```java
@GetMapping
public ResponseEntity<List<UserDTO>> findAll() {
    List<UserDTO> list = service.findAll();
    return ResponseEntity.ok().body(list);
}
```

##### Depois

```java
@GetMapping
public Flux<UserDTO> findAll() {
    return service.findAll();
}
```

#### findById

❗Utilizamos o switchIfEmpty no service parar retornar uma exceção.

#### Service

##### Antes

```java
@Transactional(readOnly = true)
public UserDTO findById(String id) {
    User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
    return new UserDTO(user);
}
```

##### Depois

```java
public Mono<UserDTO> findById(String id) {
    return repository.findById(id)
            .map(UserDTO::new)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("ID não encontrado")));
}
```

### Controller

##### Antes

```java
@GetMapping(value = "/{id}")
public ResponseEntity<UserDTO> findById(@PathVariable String id) {
    UserDTO dto = service.findById(id);
    return ResponseEntity.ok(dto);
}
```

##### Depois

```java
@GetMapping(value = "/{id}")
public Mono<ResponseEntity<UserDTO>> findById(@PathVariable String id) {
    return service.findById(id).map(userDTO -> ResponseEntity.ok().body(userDTO));
}
```

#### insert

#### Service

##### Antes

```java
@Transactional
public UserDTO insert(UserDTO dto) {
    User entity = new User();
    copyDtoToEntity(dto, entity);
    entity = repository.save(entity);
    return new UserDTO(entity);
}
```

##### Depois

```java
public Mono<UserDTO> insert(UserDTO dto) {
    User entity = new User();
    copyDtoToEntity(dto, entity);
    return repository.save(entity).map(UserDTO::new);
}
```

#### Controller

Passamos um URIComponentsBuilder para inserir no created.

##### Antes

```java
@PostMapping
public ResponseEntity<UserDTO> insert(@RequestBody UserDTO dto) {
    dto = service.insert(dto);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
    return ResponseEntity.created(uri).body(dto);
}
```

##### Depois

```java
@PostMapping
public Mono<ResponseEntity<UserDTO>> insert(@RequestBody UserDTO userDTO,
												UriComponentsBuilder builder) {
    return service.insert(userDTO).map(userDTO1 ->
            ResponseEntity.created(builder.path("/users/{id}").buildAndExpand(userDTO1.getId()).toUri())
                    .body(userDTO1));
}
```

#### update

#### Service

Usaremos o flatmap! Ele nos permite um merge, transformando uma ou mais streams em uma nova stream.

##### Antes

```java
@Transactional
public UserDTO update(String id, UserDTO dto) {
    User entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
    copyDtoToEntity(dto, entity);
    entity = repository.save(entity);
    return new UserDTO(entity);
}
```

##### Depois

```java
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
```

### Controller

##### Antes

```java
@PutMapping(value = "/{id}")
public ResponseEntity<UserDTO> update(@PathVariable String id, @RequestBody UserDTO dto) {
    dto = service.update(id, dto);
    return ResponseEntity.ok(dto);
}
```

##### Depois

```java
@PutMapping(value = "/{id}")
public Mono<ResponseEntity<UserDTO>> update(@PathVariable String id,
                                            @RequestBody UserDTO dto) {
    return service.update(id, dto).map(userDTO -> ResponseEntity.ok().body(userDTO));
}
```

#### delete

#### Service


##### Antes

```java
@Transactional
public void delete(String id) {
    User entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
    repository.delete(entity);
}
```

##### Depois

```java
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
```

### Controller

##### Antes

```java
@DeleteMapping(value = "/{id}")
public ResponseEntity<Void> delete(@PathVariable String id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
}
```

##### Depois

O then retorna um void, que é o que precisamos.

```java
	@DeleteMapping(value = "/{id}")
public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
    return service.delete(id).then(Mono.just(ResponseEntity.noContent().build()));
}
```

## Post

### findById

#### Service

##### Antes

```java
@Transactional(readOnly = true)
public PostDTO findById(String id) {
    Post post = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
    return new PostDTO(post);
}
```

##### Depois

```java
public Mono<PostDTO> findById(String id) {
    return repository.findById(id)
            .map(PostDTO::new)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")));
}
```

#### Controller

##### Antes

```java
@GetMapping(value = "/{id}")
public ResponseEntity<PostDTO> findById(@PathVariable String id) {
    PostDTO dto = service.findById(id);
    return ResponseEntity.ok(dto);
}
```

##### Depois

```java
@GetMapping(value = "/{id}")
public Mono<ResponseEntity<PostDTO>> findById(@PathVariable String id) {
    return service.findById(id).map(postDTO -> ResponseEntity.ok().body(postDTO));
}
```

### findByTitle (Query method)

#### Service

##### Antes

```java
public List<PostDTO> findByTitle(String text) {
    List<PostDTO> result = repository.searchTitle(text).stream().map(x -> new PostDTO(x)).toList();
    return result;
}
```

##### Depois

```java
public Flux<PostDTO> findByTitle(String text) {
    Flux<Post> result;

    if (text == null || text.isEmpty()) {
        result = repository.findAll();
    } else {
        result = repository.findPostByTitleIgnoreCase(text);
    }

    return result.map(PostDTO::new);
}
```

#### Controller

##### Antes

```java
@GetMapping(value = "/titlesearch")
public ResponseEntity<List<PostDTO>> findByTitle(@RequestParam(value = "text", defaultValue = "") String text) throws UnsupportedEncodingException {
    text = URL.decodeParam(text);
    List<PostDTO> list = service.findByTitle(text);
    return ResponseEntity.ok(list);
}
```

##### Depois

```java
@GetMapping(value = "/titlesearch")
public Flux<PostDTO> findByTitle(@RequestParam(value = "text", defaultValue = "") String text) throws UnsupportedEncodingException {
    text = URL.decodeParam(text);
    return service.findByTitle(text);
}
```


### fullSearch (Query method)

#### Service

##### Antes

```java
public List<PostDTO> fullSearch(String text, Instant minDate, Instant maxDate) {
    maxDate = maxDate.plusSeconds(86400); // 24 * 60 * 60
    List<PostDTO> result = repository.fullSearch(text, minDate, maxDate).stream().map(x -> new PostDTO(x)).toList();
    return result;
}
```

##### Depois

```java
public Flux<PostDTO> fullSearch(String text, Instant minDate, Instant maxDate) {
    maxDate = maxDate.plusSeconds(86400); // 24 * 60 * 60
    return repository.fullSearch(text, minDate, maxDate)
            .map(PostDTO::new)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")));
}
```

#### Controller

##### Antes

```java
@GetMapping(value = "/fullsearch")
public ResponseEntity<List<PostDTO>> fullSearch(
			@RequestParam(value = "text", defaultValue = "") String text,
			@RequestParam(value = "minDate", defaultValue = "") String minDate,
			@RequestParam(value = "maxDate", defaultValue = "") String maxDate) throws UnsupportedEncodingException, ParseException {
		
    text = URL.decodeParam(text);
    Instant min = URL.convertDate(minDate, Instant.EPOCH);
    Instant max = URL.convertDate(maxDate, Instant.now());
		
    List<PostDTO> list = service.fullSearch(text, min, max);
    return ResponseEntity.ok(list);
}
```

##### Depois

```java
@GetMapping(value = "/fullsearch")
public Flux<PostDTO> fullSearch(
			@RequestParam(value = "text", defaultValue = "") String text,
			@RequestParam(value = "minDate", defaultValue = "") String minDate,
			@RequestParam(value = "maxDate", defaultValue = "") String maxDate) throws UnsupportedEncodingException, ParseException {
		
    text = URL.decodeParam(text);
    Instant min = URL.convertDate(minDate, Instant.EPOCH);
    Instant max = URL.convertDate(maxDate, Instant.now());
		
    return service.fullSearch(text, min, max);
}
```

### searchPostsByUser

Iremos no SeedingDataBase (classe de config).

Percebe-se que ao instanciar um post, passamos um user (maria) com o ``.getId``. O problema é que esse ID foi instanciado
como nulo.

![img_24.png](img_24.png)

Veja que logo acima, nós salvamos os usuários instanciados. A ideia é que a gente busque novamente os dados do Usuário,
voltando no banco de dados (repository) localizando seu ID auto incrementado. 

[Veja aqui toda a lógica dentro do método]()

#### Repository

##### User

```java
    @Query("{ 'email': { $regex: ?0, $options: 'i' } }")
    Mono<User> searchEmail(String email);
```

##### Post

```java
	Flux<Post> findPostsByUserId(String id);
```

#### Service

```java
	public Flux<PostDTO> findPostsByUserId(String id) {
		return repository.findPostsByUserId(id)
				.map(PostDTO::new);
	}
```

#### Controller

```java
	@GetMapping(value = "/user/{id}")
	public Flux<PostDTO> findPostsByUserId(@PathVariable String id) {
		return service.findPostsByUserId(id);
	}
```