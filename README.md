# DB Delivery - Sistema de Gestão para Lanchonetes e Restaurantes

Este projeto foi desenvolvido como parte do trabalho prático da disciplina de Programação Orientada a Objetos (POO) do curso de **Sistemas de Informação**. O objetivo principal é otimizar o fluxo de informações, controle de pedidos e gerenciamento de entregas de comandas para pequenos estabelecimentos comerciais.

## 📌 Escopo do Projeto
O sistema visa integrar as operações entre a **Balconista**, o **Gerente**, o **Cliente** e o **Entregador**. Ele permite realizar o fluxo completo desde o lançamento do pedido em memória, controle de estados da entrega até a geração simplificada de comandas e relatórios operacionais.

---

## 🛠️ Arquitetura e Estrutura de Classes

O projeto segue estritamente os conceitos de Orientação a Objetos, utilizando herança, encapsulamento e associações conforme o modelo conceitual definido pelo grupo.

### Principais Entidades:
* **Login (Classe Mãe):** Responsável pelo controle de acesso e credenciais de segurança do sistema, servindo de base para os usuários.
* **Admin & Gerente:** Classes filhas de Login que gerenciam produtos, usuários e relatórios operacionais.
* **Cliente:** Responsável por interagir com o cardápio, gerenciar o carrinho de compras, pagamentos e avaliações.
* **Entregador:** Gerencia os estados das rotas de entrega (Pendente, Em rota, Finalizado).
* **Pedido & Produto:** Classes centrais que encapsulam os dados da venda e os itens de consumo.
* **Restaurante:** Entidade de amarração responsável por lançar as comandas na cozinha.

---

## 💻 Tecnologias Utilizadas
* **Linguagem:** Java 
* **Interface Gráfica:** Java Swing (Visualização de Tabelas de Pedidos e Controle de Entregadores)
* **Persistência:** Em Memória (Listas estáticas estruturadas via `ArrayList` para fins acadêmicos)
* **Versionamento:** Git & GitHub

---

## 🚀 Como Executar o Projeto

1. Faça o clone ou baixe o ZIP deste repositório.
2. Importe o projeto no Eclipse através do menu: `File -> Import -> General -> Existing Projects into Workspace`.
3. Certifique-se de que o **Build Path** está configurado com a JRE do seu ambiente.
4. Localize a classe de inicialização (Driver/Main) no pacote correspondente.
5. Clique com o botão direito e selecione `Run As -> Java Application`.
