(ns controle-financeiro.transacao-aceitacao-test
  (:require [midje.sweet :refer :all]
            [controle-financeiro.auxiliares :refer :all]
            [clj-http.client :as http]
            [controle-financeiro.infra.db-postgres :as db]))

(def transacoes-aleatorias
  '(
    {:valor 33.0M :tipo "despesa" :rotulos ["livro" "educação"]}
    {:valor 2700.0M :tipo "receita" :rotulos ["salário"]}
    {:valor 29.0M :tipo "despesa" :rotulos ["jogo" "entretenimento"]}
    {:valor 88.0M :tipo "despesa" :rotulos ["curso" "educação"]}
    {:valor 200.0M :tipo "receita"}
  )
)

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
    
  (fact "Rejeita uma transação com rótulos em branco"
    :aceitacao
    (let [response
      (http/post
        (endereco-para "/transacoes")
        (conteudo-como-json
          {:valor 64 :tipo "despesa" :rotulos [" " "curso" ""]}
        )
      )]
      
      (:status response) => 422
    )
  )  
)

(facts "Remove uma transação com determinado id"
  (against-background
    [
      (before :facts
        [
          (parar-servidor)
          (iniciar-servidor porta-padrao)
        ]
      )
    ]    
  )
    
  (fact "A base começa com 5 transações" :aceitacao-remove
    (db/limpar-base)
    (doseq [transacao transacoes-aleatorias]
      (db/registrar transacao)
    )
    
    (count
      (:transacoes
        (parse-string-producing-keywords-as-keys
          (conteudo "/transacoes")
        )
      )
    ) => 5
  )
  
  (fact "Remove a transação de id 1" :aceitacao-remove
    (let
      [
        response (http/delete (endereco-para "/transacoes/1"))
      ]
        
      (:status response) => 200
          
      (count
        (:transacoes
          (parse-string-producing-keywords-as-keys
            (conteudo "/transacoes")
          )
        )
      ) => 4
    )
  )
  
  (fact "Não remove uma transação que não existe na base"
    :aceitacao-removes
    
    (against-background
      [
        (after :facts
          [
            (db/limpar-base)
            (parar-servidor)
          ]
        )
      ]    
    )
    
    (let
      [
        response (http/delete (endereco-para "/transacoes/10") {:throw-exceptions false})
      ]
        
      (:status response) => 404
          
      (count
        (:transacoes
          (parse-string-producing-keywords-as-keys
            (conteudo "/transacoes")
          )
        )
      ) => 4
    )
  )
)
