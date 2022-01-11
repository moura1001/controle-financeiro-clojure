(ns controle-financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer :all]
            [controle-financeiro.auxiliares :refer :all]
            [clj-http.client :as http]
            [controle-financeiro.infra.db-persistence :as db]))

(against-background
  [
    (before :facts
      [
        (iniciar-servidor porta-padrao)
        (db/limpar-colecao)
      ]
    )
    (after :facts (parar-servidor))
  ]
  
  (fact "O saldo inicial é 0" :aceitacao
    (parse-string-producing-keywords-as-keys
      (conteudo "/saldo")
    ) => {:saldo 0}
  )
  
  (fact "O saldo é 10 quando a única transação é uma receita de 10"
    :aceitacao
    (http/post (endereco-para "/transacoes") (receita 10))
    
    (parse-string-producing-keywords-as-keys
      (conteudo "/saldo")
    ) => {:saldo 10}
  )
  
  (fact "O saldo é 1000 quando criamos duas receitas de 2000 e uma despesa de 3000"
    :aceitacao
    (http/post (endereco-para "/transacoes") (receita 2000))
    
    (http/post (endereco-para "/transacoes") (receita 2000))

    (http/post (endereco-para "/transacoes") (despesa 3000))
    
    (parse-string-producing-keywords-as-keys
      (conteudo "/saldo")
    ) => {:saldo 1000}
  )
)
