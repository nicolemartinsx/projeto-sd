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
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    senha VARCHAR(8) NOT NULL,
    PRIMARY KEY (id_candidato)
);

CREATE TABLE competencia (
    id_competencia INT NOT NULL AUTO_INCREMENT,
    competencia VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_competencia)
);

CREATE TABLE candidatocompetencia (
    id_candidato_competencia INT NOT NULL AUTO_INCREMENT,
    id_candidato INT NOT NULL,
    id_competencia INT NOT NULL,
    experiencia INT NOT NULL,
    PRIMARY KEY (id_candidato_competencia),
    FOREIGN KEY (id_candidato) REFERENCES candidato(id_candidato) ON DELETE CASCADE,
    FOREIGN KEY (id_competencia) REFERENCES competencia(id_competencia) ON DELETE CASCADE
);

CREATE TABLE empresa (
    id_empresa INT NOT NULL AUTO_INCREMENT,
    razao_social VARCHAR(50) NOT NULL,
    cnpj VARCHAR(14) NOT NULL,
    email VARCHAR(50) NOT NULL,
    senha VARCHAR(8) NOT NULL,
    ramo VARCHAR(50) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_empresa)
);

CREATE TABLE vaga (
    id_vaga INT NOT NULL AUTO_INCREMENT,
    id_empresa INT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    faixa_salarial DOUBLE NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_vaga),
    FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa) ON DELETE CASCADE
);

CREATE TABLE vagacompetencia (
    id_vaga_competencia INT NOT NULL AUTO_INCREMENT,
    id_vaga INT NOT NULL,
    id_competencia INT NOT NULL,
    PRIMARY KEY (id_vaga_competencia),
    FOREIGN KEY (id_vaga) REFERENCES vaga(id_vaga) ON DELETE CASCADE,
    FOREIGN KEY (id_competencia) REFERENCES competencia(id_competencia) ON DELETE CASCADE
);

CREATE TABLE candidatomensagem (
  id INT NOT NULL AUTO_INCREMENT,
  id_candidato INT NOT NULL,
  id_empresa INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (id_candidato) REFERENCES candidato(id_candidato) ON DELETE CASCADE,
  FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa) ON DELETE CASCADE
);

INSERT INTO competencia (competencia) VALUES ('Python'), ('C#'), ('C++'), ('JS'), ('PHP'), ('Swift'), ('Java'), ('Go'), ('SQL'), ('Ruby'), ('HTML'), ('CSS'), ('NOSQL'), ('Flutter'), ('TypeScript'), ('Perl'), ('Cobol'), ('dotNet'), ('Kotlin'), ('Dart');
