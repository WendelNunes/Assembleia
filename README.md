# Sobre

A API Assembleia tem como finalidade gerenciar o processo de votação em uma assembleia, podendo abrir pautas e sessões de votações 
nessas pautas e fazendo a contabilização do resultado dessa votação.

A API tem as seguintes funcionalidades

Associado

	- Cadastrar
	- Atualizar
	- Excluir
	- Obter um associado
	- Listar os associados

Pauta

	- Cadastrar
	- Atualizar
	- Excluir
	- Obter um associado
	- Listar os associados

Sessão

	- Abrir sessão para uma pauta
	- Obter uma sessão
	- Obter as sessões
	- Realizar voto em uma sessão
	- Obter resultado parcial ou final da sessão

# Subindo a API
	
Para subir a aplicação deve renomear o arquivo app.properties.backup para app.properties, se for rodar o serviço fora da ide
esse arquivo deve ficar no mesmo diretório do executável, nesse arquivo que deve ser feita a configuração da porta da API
e os dados de conexão com o banco, conforme exemplo abaixo:

	# PORTA
	app.port=8080 (Porta que A API irá subir)

	# BANCO DE DADOS
	app.db.url=localhost:8080 (URL do banco de dados)
	app.db.name=db_assembleia (Nome do banco de dados)
	app.db.username=admin (Usuário do banco de dados)
	app.db.password=123456 (Senha do banco de dados)

A documentação da API se encontra no seguinte endereço "<URL_DA_API>/swagger-ui/" ao subir a mesma:
	Ex.: https://wendelnunes.com.br/swagger-ui/ (Está URL é ficticia)

O log é gerado no diretório logs que será criado no mesmo caminho que o executável da API está, o nome do arquivo está app.log

# Tecnologias

- SpringBoot: Trás praticidade na hora configurar o projeto pois grande parte o mesmo faz de forma automática
- Maven: Usado para gerenciar as dependências e o processo de build
- SpringFox: Usado para criação da documentação da API
- Hibernate: Usado para fazer a persistência do modelo de domínio	
- Postgres: Bando de dados usado para persistir os dados
- Flyway: Usado para controle de versão do banco de dados
- Lombok: Usado para evitar códigos boileparte como getters, setters, equals, hashcodes e outros
- ModelMapper: Usado para fazer a conversão dos DTOS em modelo de domínio ou vice-versa
- JUnit: Usado para realizar testes unitários
- Mockito: Usado para fazer o mock de objetos utilizados nos testes
- Apache Commons Lang: Usado para fornecimento de classes utilitárias
	
# Melhorias

- Implementar controle de acesso usando por exemplo JWT para autenticação do mesmo
- Fazer testes dos repositorys
- No caso de comunicação com serviços terceiros implementar um circuit breaker garantindo assim um retorno rápido para 
  o cliente em caso de demora do serviço terceiro
