(ns controle-financeiro.filtro-tipo-test
  (:require [controle-financeiro.handler :refer [app]]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [controle-financeiro.infra.db-postgres :as db]))
  
(facts "Existe uma rota para lidar com filtro de transação por tipo"
  (against-background
    [
      (db/transacoes-do-tipo "receita")
        => '({:id 1 :valor 2000 :tipo "receita" :rotulos []})
      
      (db/transacoes-do-tipo "despesa")
        => '({:id 2 :valor 89 :tipo "despesa"  :rotulos []})

      (db/transacoes)
        => '(
            {:id 1 :valor 2000 :tipo "receita" :rotulos []}
            {:id 2 :valor 89 :tipo "despesa" :rotulos []}
          )
    ]

    (fact "Filtro por receita"
      (let [response (app (mock/request :get "/receitas"))]
        (:status response) => 200
        
        (:body response) =>
        (json/generate-string
          {
            :transacoes '({:id 1 :valor 2000 :tipo "receita" :rotulos []})
          }
        )
      )
    )
    
    (fact "Filtro por despesa"
      (let [response (app (mock/request :get "/despesas"))]
        (:status response) => 200
        
        (:body response) =>
        (json/generate-string
          {
            :transacoes '({:id 2 :valor 89 :tipo "despesa" :rotulos []})
          }
        )
      )
    )
    
    (fact "Sem filtro"
      (let [response (app (mock/request :get "/transacoes"))]
        (:status response) => 200
        
        (:body response) =>
        (json/generate-string
          {
            :transacoes '(
              {:id 1 :valor 2000 :tipo "receita" :rotulos []}
              {:id 2 :valor 89 :tipo "despesa" :rotulos []}
            )
          }
        )
      )
    )
  )
)
  