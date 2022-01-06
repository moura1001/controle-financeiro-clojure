(ns controle-financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [controle-financeiro.auxiliares :refer :all]
            [cheshire.core :as json]
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
    (http/post
      (endereco-para "/transacoes")
      {
        :content-type :json
        :body (json/generate-string {:valor 10 :tipo "receita"})
      }
    )
    
    (parse-string-producing-keywords-as-keys
      (conteudo "/saldo")
    ) => {:saldo 10}
  )
  
  (fact "O saldo é 1000 quando criamos duas receitas de 2000 e uma despesa de 3000"
    :aceitacao
    (http/post
      (endereco-para "/transacoes")
      {
        :content-type :json
        :body (json/generate-string {:valor 2000 :tipo "receita"})
      }
    )
    
    (http/post
      (endereco-para "/transacoes")
      {
        :content-type :json
        :body (json/generate-string {:valor 2000 :tipo "receita"})
      }
    )

    (http/post
      (endereco-para "/transacoes")
      {
        :content-type :json
        :body (json/generate-string {:valor 3000 :tipo "despesa"})
      }
    )
    
    (parse-string-producing-keywords-as-keys
      (conteudo "/saldo")
    ) => {:saldo 1000}
  )
)
