-- Schema inicial para o projeto Ambiente-de-dados-Delivery
-- Ordenado para respeitar dependências e compatível com os DAOs do projeto

CREATE DATABASE IF NOT EXISTS delivery_system;
USE delivery_system;

-- =========================
-- TABELA CLIENTE
-- =========================
CREATE TABLE IF NOT EXISTS Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(15),
    endereco VARCHAR(150),
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABELA ADMIN
-- =========================
CREATE TABLE IF NOT EXISTS Admin (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABELA RESTAURANTE
-- =========================
CREATE TABLE IF NOT EXISTS Restaurante (
    id_restaurante INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo_cozinha VARCHAR(50),
    telefone VARCHAR(15),
    endereco VARCHAR(150)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABELA ENTREGADOR
-- =========================
CREATE TABLE IF NOT EXISTS Entregador (
    id_entregador INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    status ENUM('disponivel','ocupado','inativo') DEFAULT 'disponivel',
    veiculo VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABELA ITEM_PEDIDO (cardápio / itens do restaurante)
-- =========================
CREATE TABLE IF NOT EXISTS ItemPedido (
    id_item INT AUTO_INCREMENT PRIMARY KEY,
    id_restaurante INT NOT NULL,
    descricao VARCHAR(150) NOT NULL,
    quantidade INT DEFAULT 1,
    preco DECIMAL(10,2) NOT NULL,
    observacao VARCHAR(150),
    estoque INT DEFAULT 0,
    CONSTRAINT fk_item_restaurante FOREIGN KEY (id_restaurante)
        REFERENCES Restaurante(id_restaurante)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABELA PEDIDO
-- =========================
CREATE TABLE IF NOT EXISTS Pedido (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_restaurante INT NOT NULL,
    id_entregador INT NULL,
    data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pendente','em_preparo','enviado','entregue','cancelado') DEFAULT 'pendente',
    valor_total DECIMAL(10,2) DEFAULT 0.00,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente)
        REFERENCES Cliente(id_cliente)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_pedido_restaurante FOREIGN KEY (id_restaurante)
        REFERENCES Restaurante(id_restaurante)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_pedido_entregador FOREIGN KEY (id_entregador)
        REFERENCES Entregador(id_entregador)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABELA pedido_itens (itens associados a um pedido)
-- corresponde ao uso em `PedidoDAO.criarPedidoComItens`
-- =========================
CREATE TABLE IF NOT EXISTS pedido_itens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_item INT NOT NULL,
    descricao VARCHAR(150) NOT NULL,
    preco_unit DECIMAL(10,2) NOT NULL,
    quantidade INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_itens_pedido FOREIGN KEY (id_pedido)
        REFERENCES Pedido(id_pedido)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_itens_item FOREIGN KEY (id_item)
        REFERENCES ItemPedido(id_item)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Exemplo de inserções iniciais (opcional)
-- =========================

INSERT INTO Restaurante (nome, tipo_cozinha, telefone, endereco)
VALUES
('Seoul Taste', 'Coreana', '11-99999-0001', 'Rua A, 123'),
('Sushi House', 'Japonesa', '11-99999-0002', 'Rua B, 45');

-- Insere alguns itens de cardápio (ajuste id_restaurante conforme necessárias)
INSERT INTO ItemPedido (id_restaurante, descricao, quantidade, preco, observacao, estoque)
VALUES
(1, 'Bibimbap Clássico', 1, 35.50, 'Com ovo frito e molho apimentado médio', 10),
(1, 'Kimchi Jjigae', 1, 42.00, 'Picante, com carne de porco e tofu', 8),
(2, 'Japchae', 1, 28.90, 'Porção individual, sem carne', 15),
(2, 'Tteokbokki', 1, 32.00, 'Extra queijo, pouco apimentado', 12),
(1, 'Bulgogi (Porção para 2)', 1, 68.00, 'Carne de boi marinada, mal passada', 5);

-- Exemplo de uso: mostrar restaurantes
SELECT * FROM Restaurante;

-- Nota: abaixo há um exemplo de como dropar/recriar uma tabela evitando violação de FKs.
-- Use com cuidado em ambientes de produção.
--
-- SET FOREIGN_KEY_CHECKS = 0;
-- DROP TABLE IF EXISTS pedido_itens;
-- DROP TABLE IF EXISTS Pedido;
-- DROP TABLE IF EXISTS ItemPedido;
-- DROP TABLE IF EXISTS Entregador;
-- DROP TABLE IF EXISTS Restaurante;
-- SET FOREIGN_KEY_CHECKS = 1;

-- Fim do script
