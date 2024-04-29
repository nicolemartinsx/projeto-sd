DROP DATABASE IF EXISTS projetosd;
CREATE DATABASE projetosd;
USE projetosd;

DROP TABLE IF EXISTS candidatocompetencia;
DROP TABLE IF EXISTS vagacompetencia;
DROP TABLE IF EXISTS vaga;
DROP TABLE IF EXISTS empresa;
DROP TABLE IF EXISTS competencia;
DROP TABLE IF EXISTS candidato;

CREATE TABLE candidato (
    id_candidato INT NOT NULL AUTO_INCREMENT,
    nome CHAR(50) NOT NULL,
    email CHAR(50) NOT NULL,
    senha CHAR(50) NOT NULL,
    PRIMARY KEY (id_candidato)
);

CREATE TABLE competencia (
    id_competencia INT NOT NULL AUTO_INCREMENT,
    competencia CHAR(50) NOT NULL,
    PRIMARY KEY (id_competencia)
);

CREATE TABLE candidatocompetencia (
    id_candidato_competencia INT NOT NULL AUTO_INCREMENT,
    id_candidato INT NOT NULL,
    id_competencia INT NOT NULL,
    tempo INT NOT NULL,
    PRIMARY KEY (id_candidato_competencia),
    FOREIGN KEY (id_candidato) REFERENCES candidato(id_candidato),
    FOREIGN KEY (id_competencia) REFERENCES competencia(id_competencia)
);

CREATE TABLE empresa (
    id_empresa INT NOT NULL AUTO_INCREMENT,
    razao_social CHAR(50) NOT NULL,
    email CHAR(50) NOT NULL,
    senha INT NOT NULL,
    ramo CHAR(50) NOT NULL,
    descricao CHAR(255) NOT NULL,
    PRIMARY KEY (id_empresa)
);

CREATE TABLE vaga (
    id_vaga INT NOT NULL AUTO_INCREMENT,
    id_empresa INT NOT NULL,
    faixa_salarial DOUBLE NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_vaga),
    FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa)
);

CREATE TABLE vagacompetencia (
    id_vaga_competencia INT NOT NULL AUTO_INCREMENT,
    id_vaga INT NOT NULL,
    id_competencia INT NOT NULL,
    PRIMARY KEY (id_vaga_competencia),
    FOREIGN KEY (id_vaga) REFERENCES vaga(id_vaga),
    FOREIGN KEY (id_competencia) REFERENCES competencia(id_competencia)
);
