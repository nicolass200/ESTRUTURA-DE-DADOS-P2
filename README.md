Sistema de Simulação de Coleta de Resíduos Urbanos

📌 Visão Geral
Este projeto é um sistema de simulação computacional desenvolvido em Java para modelar a coleta de resíduos sólidos urbanos na cidade de Teresina, Piauí, Brasil. O sistema utiliza uma abordagem baseada em agentes e simulação de eventos discretos para representar o fluxo de resíduos desde as zonas geradoras até o aterro sanitário, passando por estações de transferência. O objetivo é otimizar a logística de coleta, reduzir tempos de espera e mitigar impactos ambientais.

🚀 Funcionalidades Principais
Simulação de Coleta: Modela a coleta de resíduos por caminhões pequenos, transferência para estações e transporte final por caminhões grandes.

Configuração Flexível: Parâmetros ajustáveis, como número de caminhões, capacidades, tempos de viagem e limites de fila.

Interface Gráfica: Visualização intuitiva das zonas, caminhões e estações usando Java Swing.

Geração de Relatórios: Exportação de dados estatísticos sobre resíduos coletados, viagens realizadas e eficiência operacional.

Eventos em Tempo Real: Registro de eventos como coletas, transferências e descargas para análise posterior.

🛠️ Estrutura do Projeto
Classes Principais
Zona: Representa áreas da cidade que geram resíduos.

CaminhaoPequeno: Responsável pela coleta nas zonas e transporte para as estações.

CaminhaoGrande: Transporta resíduos das estações para o aterro.

EstacaoTransferencia: Gerencia filas de caminhões e a transferência de resíduos.

Simulador: Orquestra a simulação, avançando o tempo e coordenando interações.

Configuracao: Armazena parâmetros configuráveis da simulação.

JanelaPrincipal e PainelSimulacao: Interface gráfica para visualização e controle.

Estruturas de Dados
Filas (FIFO): Gerenciam caminhões em estações de transferência.

Listas: Armazenam histórico de eventos para relatórios.

Arrays: Monitoram resíduos coletados por zona e caminhão.

📋 Pré-requisitos
Java JDK 11 ou superior.

Ambiente de desenvolvimento compatível com Java Swing (ex: IntelliJ IDEA, Eclipse).

Git (opcional, para clonar o repositório).

🖥️ Como Executar
Clone o repositório:

bash
git clone [URL_DO_REPOSITORIO]
Importe o projeto na sua IDE Java favorita.

Execute a classe Main para iniciar a simulação.

Use a interface gráfica para:

Iniciar/pausar a simulação.

Ajustar parâmetros (se configurado).

Salvar relatórios.

⚙️ Parâmetros de Configuração
Os principais parâmetros ajustáveis incluem:

Número de caminhões pequenos (numCaminhoesPequenos).

Capacidades dos caminhões (capacidadeCaminhaoGrande, capacidadesCaminhoesPequenos).

Tempos de viagem (tempoViagemPicoMin/Max, tempoViagemForaPicoMin/Max).

Limites operacionais (limiteEsperaPequeno, limiteFilaEstacao).

📊 Exemplo de Saída
A simulação gera relatórios no arquivo simulacao.txt, contendo:

Resíduos coletados por zona.

Estatísticas de viagens dos caminhões.

Tempos médios de espera nas estações.

📌 Melhorias Futuras
Implementação de algoritmos de otimização de rotas.

Simulações multidárias com análise de acúmulo progressivo.

Ajustes de parâmetros em tempo real.

Integração com técnicas de aprendizado de máquina para roteamento dinâmico.

📚 Referências
Macal, C. M., & North, M. J. (2010). "Tutorial on agent-based modeling and simulation." Journal of Simulation.

Documentação oficial do Java: Oracle Java SE.

✉️ Contato
Para dúvidas ou contribuições, entre em contato com os autores:

João Carlos Vieira Galvão Almeida: joao_carlos.almeida@somosicev.com

Nicolas Rodrigues Ferreira de Carvalho: nicolas.carvalho@somosicev.com
