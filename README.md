# WeatherViewerApp

*   **Nome:** Thiago Alves Ramos Oliveira
*   **Curso:** Sistemas de Informação
*   **Período:** 6º Período
*   **Disciplina:** Programação III

### Descrição da Aplicação
O **WeatherViewerApp** é uma aplicação Android desenvolvida para consultar e exibir a previsão do tempo para uma determinada localidade.

Principais funcionalidades:
*   Busca de previsão do tempo por cidade (formato: Cidade,Estado,Pais).
*   Exibição de previsão para os próximos dias (data, temperatura mínima, temperatura máxima, umidade, descrição e ícone).
*   Persistência da última cidade pesquisada utilizando `SharedPreferences`.
*   Suporte a troca de tema (Modo Claro / Modo Escuro).
*   Consumo de API REST JSON utilizando `HttpURLConnection` e `AsyncTask`.

### Instruções para Execução
1.  **Pré-requisitos:** Android Studio instalado e um emulador ou dispositivo físico com Android 6.0 (API 23) ou superior.
2.  **Abrir o Projeto:** Abra o Android Studio e selecione o diretório `WeatherViewerApp`.
3.  **Compilar:** Aguarde a sincronização do Gradle e a indexação do projeto.
4.  **Executar:** Clique no botão "Run" (ícone de play verde) e selecione o dispositivo alvo.
5.  **Utilização:**
    *   Na tela inicial, o campo de texto pode vir preenchido com a última cidade pesquisada.
    *   Digite o local desejado no formato: `Cidade,Estado,Pais` (exemplo: `Passos,MG,BR`).
    *   Toque no botão flutuante (FAB) com ícone de nuvem para realizar a busca.
    *   Os dados climáticos serão carregados na lista abaixo.
    *   Utilize o menu na barra superior para alternar entre Modo Claro e Escuro.

### Exemplo da URL utilizada na requisição
A aplicação realiza uma requisição HTTP GET para a seguinte API:

**Base URL:**
`http://agent-weathermap-env-env.eba-6pzgqekp.us-east-2.elasticbeanstalk.com/api/weather`

**Parâmetros:**
*   `city`: Cidade codificada (URL Encoded)
*   `days`: Número de dias (fixo em 7 no código)
*   `APPID`: Chave da API

**Exemplo Completo:**
`http://agent-weathermap-env-env.eba-6pzgqekp.us-east-2.elasticbeanstalk.com/api/weather?city=Passos%2CMG%2CBR&days=7&APPID=AgentWeather2024_a8f3b9c1d7e2f5g6h4i9j0k1l2m3n4o5p6`
