# 🍔 Sistema de Gestão de Delivery v1.0

Este é um sistema desktop desenvolvido em **Java Swing** para a gestão simplificada de pequenos estabelecimentos de delivery. O projeto foi estruturado seguindo padrões de arquitetura em camadas para facilitar a manutenção e escalabilidade.

## 🚀 Funcionalidades

- **Gestão de Cardápio:** Cadastro, consulta e remoção de produtos com geração de ID automático.
- **Fluxo de Pedidos:** Registro de vendas com cálculo automático de totais e taxas de entrega.
- **Pagamento Digital:** Integração visual para pagamentos via Pix com exibição de QR Code.
- **Módulo de Logística:** Painel para o entregador gerir o status do pedido (Pendente, A caminho, Pago) e emissão de comandas.
- **Relatórios Gerenciais:** Resumo financeiro de faturamento diário com filtros por status.

## 🏗️ Arquitetura do Projeto

O sistema utiliza o padrão de separação de responsabilidades (SoC) organizado nos seguintes pacotes:

- `model`: Classes de entidade que representam o negócio (`Produto`, `Pedido`).
- `view`: Interfaces gráficas desenvolvidas com a biblioteca Swing.
- `repositorio`: Camada de persistência em memória utilizando coleções estáticas (`ArrayList`).

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java (JDK 11 ou superior)
- **Interface:** Java Swing / AWT
- **Gestão de Dependências:** Bibliotecas externas para layout (ex: MigLayout) e Driver JDBC (preparado para futura integração SQL).

---
*Projeto desenvolvido por Davi, Felipe Leroy e Arthur MArtins para a disciplina de Programação Oritentada a Objetos.
