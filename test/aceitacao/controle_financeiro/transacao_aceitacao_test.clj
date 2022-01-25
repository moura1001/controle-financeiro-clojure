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
  :aceitacao
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
    
  (fact "A base começa com 5 transações"
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
  
  (fact "Remove a transação de id 1"
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

(facts "Altera uma transação com determinado id"
  :aceitacao

  (def transacoes-alteradas
    '(
      {:id 1 :valor 33.0 :tipo "despesa" :rotulos ["livro" "educação"]}
      {:id 2 :valor 2700.0 :tipo "receita" :rotulos ["salário"]}
      {:id 3 :valor 29.0 :tipo "despesa" :rotulos ["entretenimento" "jogo"]}
      {:id 4 :valor 88.0 :tipo "despesa" :rotulos ["curso" "educação"]}
      {:id 5 :valor 200.0 :tipo "receita" :rotulos ["venda"]}
    )
  )

  (fact "Altera a transação de id 5"
    (against-background
      [
        (before :facts
          [
            (iniciar-servidor porta-padrao)
            (db/limpar-base)
            (doseq [transacao transacoes-aleatorias]
              (http/post
                (endereco-para "/transacoes")
                (conteudo-como-json transacao)
              )
            )
          ]
        )
      ]    
    )
        
    (let [response
      (http/put
        (endereco-para "/transacoes/5")
        (conteudo-como-json
          {:valor 200.0 :tipo "receita" :rotulos "venda"}
        )
      )]
      
      (:status response) => 200

      (:transacoes
        (parse-string-producing-keywords-as-keys
          (conteudo "/transacoes")
        )
      ) => transacoes-alteradas
    )
  )
  
  (fact "Não altera uma transação que não existe na base"
    
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
    
    (let [response
      (http/put
        (endereco-para "/transacoes/8")
        (conteudo-como-json
          {:valor 80.0 :tipo "despesa"}
        )
      )]
      
      (:status response) => 404

      (:transacoes
        (parse-string-producing-keywords-as-keys
          (conteudo "/transacoes")
        )
      ) => transacoes-alteradas
    )
  )
)
