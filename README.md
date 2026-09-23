# Datadog HTTP Status Load Test

Servidor Spring Boot para testes de carga HTTP/HTTPS. Ele responde automaticamente a métodos HTTP comuns, permite escolher códigos de status por query string e registra cada requisição e resposta.

## Requisitos

- Docker com Docker Compose
- 2 vCPU e 4 GB de RAM recomendados

## Executar

```bash
docker compose up --build
```

A aplicação ficará disponível em:

- HTTP: `http://localhost:8080`
- HTTPS: `https://localhost:8443` (certificado autoassinado)
- Health: `http://localhost:8080/actuator/health`

## Exemplos

```bash
curl -i http://localhost:8080/test
curl -i -X POST http://localhost:8080/orders
curl -i http://localhost:8080/test?status=404
curl -i http://localhost:8080/test?status=500
curl -i http://localhost:8080/random
curl -i http://localhost:8080/test?status=random
curl -k -i https://localhost:8443/test
curl -i -X OPTIONS http://localhost:8080/test
curl -I http://localhost:8080/test
```

O status solicitado deve estar entre 100 e 599. Sem `status`, os padrões são GET/PATCH/HEAD: 200, POST: 201, PUT: 202 e DELETE/OPTIONS: 204.

## Logs

Cada requisição gera uma linha de log com método, URI, query string, status, duração, endereço remoto, User-Agent e corpos limitados a 2.000 caracteres. Para acompanhar:

```bash
docker compose logs -f datadog-load-test
```

## Limites da máquina

A JVM usa suporte a limites do container, 2 processadores ativos, heap inicial de 512 MB e máximo de 1,5 GB. O Compose limita o serviço a 2 CPUs e 4 GB. Ajuste `JAVA_OPTS` caso a máquina hospede outros serviços.

## Datadog

Use qualquer ferramenta de carga do Datadog apontando para `http://SEU_HOST:8080` ou `https://SEU_HOST:8443`. Para o certificado autoassinado em HTTPS, desative a verificação TLS apenas no ambiente de teste.

## Licença

Apache License 2.0. Consulte `LICENSE`.
