(ns controle-financeiro.transacao-test
  (:require [controle-financeiro.handler :refer [app]]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [controle-financeiro.infra.db-persistence :as db]))
  
(facts "Registra uma receita no valor de 10"
  (against-background
    (db/registrar {:valor 10 :tipo "receita"})
    => {:id 1 :valor 10 :tipo "receita"}
  )
  (let [response
    (app
      (
        -> (mock/request :post "/transacoes")
        (mock/json-body {:valor 10 :tipo "receita"})
      )
    )]
    
    (fact "O status da resposta é 201"
      (:status response) => 201)
      
    (fact "O texto do corpo é um JSON com o conteúdo enviado e um id"
      (:body response) => "{\"id\":1,\"valor\":10,\"tipo\":\"receita\"}")
  )
)
  