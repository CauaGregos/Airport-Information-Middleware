# IATA-ICAO App

Projeto Spring Boot organizado em arquitetura em camadas.

## Estrutura
```text
com.example.iataicaoapp
├── client        # Acesso a API externa
├── controller    # Entrada HTTP da aplicacao
├── domain        # Modelos centrais do negocio
├── dto           # Objetos usados nas respostas/entradas da API
├── exception     # Excecoes e tratamento global de erros
└── service       # Regras de negocio e orquestracao
```

## Fluxo

```text
Controller -> Service -> Client -> Domain
```

O controller recebe a requisição HTTP, chama o service, que chama o client e retorna a entidade *AirportInformation*
do domínio. O *AviationApiClient* representa a API https://api-v2.aviationapi.com/v2/docs. O service precisa dele para acessar 
a API externa e trazer os dados para o nosso "lado". O middleware criado, é responsável por validar o ICAO code enviado e tratar os 
erros não tratados pelo provedor da API externa. O retorno, são os dados do aeroporto, cujo, é identificado por esse ICAO code.
## Endpoint de exemplo

```http
GET /api/airport/codes/KATL
```

Resposta esperada:

```json
{
  "iataCode": "ATL",
  "icaoCode": "KATL",
  "name": "HARTSFIELD/JACKSON ATLANTA INTL",
  "city": "ATLANTA",
  "country": "USA"
}
```

## Uso de agentes IA
Nessa aplicação foi utilizado o ChatGPT 5.5 e o InteliJ.

Quais situações demandaram as consultas?

### Criação do observador de exceções global:
Foi indicado o uso de um observador global para centralizar o tratamento do retorno de exceções que ocorrem no controller principal da aplicação
Notanto ser uma ferramenta nativa do spring boot, optei por seguir com o uso. Com a notation *@RestControllerAdvice* preparamos a classe em contexto 
para ouvir alguns eventos do controller que indicarmos. Adicionando a notation *@ExceptionHandler* num método dentro dessa classe, indicamos que vamos tomar uma ação, 
sempre que uma exception do tipo que indicarmos for lançada.
### Montagem do template inicial dos testes:
Para fins de ganho de tempo, solicitei ao ChatGPT que gerasse o modelo inicial dos testes, porém, ele criou com o mockito.
Pesquisei sobre os testes no spring boot e notei que os dados, resposta e validações esperadas estavam mockadas, fazendo assim, um bypass.
Percebi o erro e fiz as alterações para parar de mockar as validações do controller. 
Criei um mock que com a resposta da API externa e outro para a simulação da chamada, assim consigo controlar a 
resposta que ela vai retornar e os dados. Para tudo isso funcionar, precisei da notation *@AutoConfigureMockRestServiceServer* que faz com que 
eu consiga configurar interpretador HTTP (MockRestServiceServer), se não, o teste chama a API real.
### Conhecimento específico do Spring Boot:
O agente, foi muito consultado para sanar dúvidas quanto a notations, muito comumente usadas no spring boot. 
Como um desenvolvedor que não atua diariamente com este framework, usei a ferramenta para indicar as notations
usadas e quais usar em situações especificas, como teste, serviço e cliente. É notável o uso de records nesta aplicação, um assunto que também
busquei conhecimento para fazer este projeto.

# Como rodar o projeto

Este projeto é uma aplicação Spring Boot com Maven Wrapper. Os comandos abaixo devem ser executados na raiz do projeto.

## Pre-requisitos

- JDK 17 instalado.
- Variavel `JAVA_HOME` apontando para o JDK 17.
- Acesso a internet na primeira execução, caso o Maven Wrapper precise baixar o Maven.

Para confirmar a versão do Java:

```bash
java -version
```

A versao deve ser Java 17.

## Windows CMD

Entre na pasta do projeto:

```bat
cd C:\Users\cauan\IdeaProjects\IATA-ICAO-App
```

Se o `JAVA_HOME` ainda não estiver configurado, configure apenas para a sessao atual:

```bat
Exemplo: 
set JAVA_HOME=C:\Users\cauan\.jdks\ms-17.0.19
set PATH=%JAVA_HOME%\bin;%PATH%
```

Rodar todos os testes:

```bat
mvnw.cmd test
```

Rodar apenas o teste do controller:

```bat
mvnw.cmd -Dtest=AirportInformationControllerTest test
```

Subir a aplicação:

```bat
mvnw.cmd spring-boot:run
```

## Linux e macOS

Entre na pasta do projeto:

```bash
Exemplo:
cd /caminho/para/IATA-ICAO-App
```

Se o `JAVA_HOME` ainda nao estiver configurado, configure apenas para a sessao atual:

```bash
export JAVA_HOME=/caminho/para/seu/jdk-17
export PATH="$JAVA_HOME/bin:$PATH"
```

Se necessario, permita executar o Maven Wrapper:

```bash
chmod +x ./mvnw
```

Rodar todos os testes:

```bash
./mvnw test
```

Rodar apenas o teste do controller:

```bash
./mvnw -Dtest=AirportInformationControllerTest test
```

Subir a aplicacao:

```bash
./mvnw spring-boot:run
```

## Testar a API manualmente

Com a aplicacao rodando, acesse:

```text
http://localhost:8080/api/airport/codes/KATL
```

Ou use `curl`:

```bash
curl http://localhost:8080/api/airport/codes/KATL
```