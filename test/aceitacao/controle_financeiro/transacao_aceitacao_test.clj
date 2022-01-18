(ns controle-financeiro.transacao-aceitacao-test
  (:require [midje.sweet :refer :all]
            [controle-financeiro.auxiliares :refer :all]
            [clj-http.client :as http]
            [controle-financeiro.infra.db-postgres :as db]))

(against-background
  [
    (before :facts
      [
        (iniciar-servidor porta-padrao)
        (db/limpar-base)
      ]
    )
    (after :facts (parar-servidor))
  ]
  
  (fact "Rejeita uma transação sem valor"
    :aceitacao
    (let [response
      (http/post
        (endereco-para "/transacoes")
        (conteudo-como-json {:tipo "receita"})
      )]
      
      (:status response) => 422
    )
  )
  
  (fact "Rejeita uma transação com valor negativo"
    :aceitacao
    (let [response
      (http/post
        (endereco-para "/transacoes")
        (receita -64)
      )]
      
      (:status response) => 422
    )
  )
  
  (fact "Rejeita uma transação com valor que não é um número"
    :aceitacao
    (let [response
      (http/post
        (endereco-para "/transacoes")
        (receita "oito")
      )]
      
      (:status response) => 422
    )
  )
  
  (fact "Rejeita uma transação sem tipo"
    :aceitacao
    (let [response
      (http/post
        (endereco-para "/transacoes")
        (conteudo-como-json {:valor 16})
      )]
      
      (:status response) => 422
    )
  )
  
  (fact "Rejeita uma transação com tipo desconhecido"
    :aceitacao
    (let [response
      (http/post
        (endereco-para "/transacoes")
        (conteudo-como-json {:valor 256 :tipo "investimento"})
      )]
      
      (:status response) => 422
    )
  )
)
