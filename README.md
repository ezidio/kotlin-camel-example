Exemplo funcionando apresentado na talk =)

Apresentação: https://www.youtube.com/watch?v=X_351K0WS4g

Slides: https://github.com/ezidio/kotlin-camel-example

#Docker

Na raiz do projeto se encontra um `docker-compose.yml` com os containeres utilizados no projeto;

# Endpoints

POST localhost:8080/products

```json
[
	{"sku": "2231", "price": 10, "exchange": "USD", "published": true },
	{"sku": "2922", "price": 299.9, "exchange": "USD", "published": true },
	{"sku": "2411", "price": 80, "exchange": "USD", "published": false },
	{"sku": "5311", "price": 22.8, "exchange": "USD", "published": false },
	{"sku": "9942", "price": 109.9, "exchange": "USD", "published": true }
]
```

PUT localhost:8080/products/2231

```json
{"sku": "2231", "price": 10, "exchange": "USD", "published": true }
```

# Mockoon

Baixe a aplicação em https://mockoon.com/ e importe o arquivo `mockoon.json` presente na raiz do projeto;

