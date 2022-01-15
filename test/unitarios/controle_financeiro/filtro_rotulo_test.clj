(ns controle-financeiro.filtro-rotulo-test
  (:require [controle-financeiro.handler :refer [app]]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [controle-financeiro.infra.db-persistence :as db]))
  
(def livro
  {:id 1 :valor 33.0M :tipo "despesa" :rotulos ["livro" "educação"]}
)

(def curso
  {:id 2 :valor 88.0M :tipo "despesa" :rotulos ["curso" "educação"]}
)

(def salario
  {:id 3 :valor 2700.0M :tipo "receita" :rotulos ["salário"]}
)

(def transacao-sem-rotulo
  {:id 4 :valor 150.0M :tipo "receita"}
)

(def transacoes-aleatorias
  [livro curso salario transacao-sem-rotulo]
)

(facts "Filtra transações por parâmetros de busca na URL"
  (against-background
    [
      (before :facts
        [
          (db/limpar-colecao)
          (doseq [transacao transacoes-aleatorias]
            (db/registrar transacao)
          )
        ]
      )

      (db/transacoes-com-filtro {:rotulos ["livro" "curso"]})
        => [livro curso]
      
      (db/transacoes-com-filtro {:rotulos "salário"})
        => [salario]

      (db/transacoes-com-filtro {:rotulos ""})
        => [transacao-sem-rotulo]

      (after :facts (db/limpar-colecao))
    ]

    (fact "Filtro por múltiplos rótulos"
      (let
        [
          response
          (app
            (mock/request :get "/transacoes?rotulos=livro&rotulos=curso")
          )
        ]
        (:status response) => 200
        
        (:body response) =>
        (json/generate-string
          {
            :transacoes [livro curso]
          }
        )
      )
    )
    
    (fact "Filtro por único rótulo"
      (let
        [
          response
          (app
            (mock/request :get "/transacoes?rotulos=salário")
          )
        ]
        (:status response) => 200
        
        (:body response) =>
        (json/generate-string
          {
            :transacoes [salario]
          }
        )
      )
    )
    
    (fact "Filtro sem nenhum valor"
      (let
        [
          response
          (app
            (mock/request :get "/transacoes?rotulos=")
          )
        ]
        (:status response) => 200
        
        (:body response) =>
        (json/generate-string
          {
            :transacoes [transacao-sem-rotulo]
          }
        )
      )
    )
  )
)
  