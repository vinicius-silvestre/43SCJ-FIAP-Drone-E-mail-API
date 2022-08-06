# Drone Track-er

Para todos os projetos:

1º
```bash
mvn clean install
```
2º
- Rodar o Application.java


## Fluxo:

![image](https://user-images.githubusercontent.com/39711228/183234004-b32cdcc9-8c53-4b77-942b-29239c3155e8.png)

### 1º Microsserviço
### drone-tracker-receptor [Porta:8080]
Serviço responsável por ser o receptor do drone


Aplicação web na qual podemos informar manualmente:
- id_drone (Identificador do Drone) 
-	Latitude e longitude (Precisamos de uma latitude e longitude validas.);
-	Temperatura (-25º até 40º);
-	Umidade (0% - 100%);
-	Ativar rastreamento (ligada-desligada)

Ex cadastro drone:

[ POST ] http://localhost:8080/drone-track/

```bash
{
    "id": 3,
    "latitude": "2",
    "longitude": "3",
    "temperatura": 34,
    "umidade": 15,
    "rastreamento": true
}
```

- Ao bater no endpoint, é criado o exchange direct "**exchange.drone**" que será usado daqui pra frente. 

![image](https://user-images.githubusercontent.com/39711228/183229346-5210d32f-c295-42f1-8e01-d3a980dd8be0.png)

- Ele terá bind com duas Routing Keys:

![image](https://user-images.githubusercontent.com/39711228/183229475-e869fbe5-e99e-4029-b89e-186a5e317df6.png)


- O drone recebido será enviado para a fila "**drone-tracker-receptor**" em bytes

![image](https://user-images.githubusercontent.com/39711228/183229759-9a60c9bf-83bc-4269-9a2a-575a151b5a76.png)
![image](https://user-images.githubusercontent.com/39711228/183229810-1a04f23b-bd08-4baf-8f6d-07c595c13588.png)



- E no próximo projeto, somente os drones validados serão enviados para a fila "**drone-tracker-alert**"


### 2º Microsserviço
### drone-tracker-validator [Porta:8082]
Serviço responsável por ser o validador do drone

- É um schedule que consome da fila "**drone-tracker-receptor**" a cada 10 segundos
- Verifica as seguintes condicionais: Temperatura (>= 35 ou <=0) ou (Umidade <= 15%)
- Caso sejam verdadeiras, ele envia para a fila de alerta: "**drone-tracker-alert**"

![image](https://user-images.githubusercontent.com/39711228/183229884-3f1a2f68-983a-48fa-b6b7-89c45d98885c.png)
![image](https://user-images.githubusercontent.com/39711228/183229907-4189524b-a523-479c-9eb6-3a91dfbb2d1a.png)

- Também é possível fazer o consumo via endpoint

Ex consumo via endpoint:

[ GET ] http://localhost:8082/drone-track/

Retorno:

```bash
{
    "id": 3,
    "latitude": "2",
    "longitude": "3",
    "temperatura": 34,
    "umidade": 15,
    "rastreamento": true
}
```

### 3º Microsserviço
### drone-tracker-alert [Porta:8083]
Serviço responsável por enviar o alerta (e-mail) em relação ao drones validados

- É um schedule que consome da fila "**drone-tracker-alert**" a cada 1 minuto
- Envia um e-mail com as informações de Temperatura e Umidade

