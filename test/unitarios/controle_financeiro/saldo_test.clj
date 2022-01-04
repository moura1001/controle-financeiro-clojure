(ns controle-financeiro.saldo-test
  (:require [controle-financeiro.handler :refer [app]]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]))
  
(facts "Saldo inicial deve ser 0"
  (against-background
    (json/generate-string {:saldo 0}) => "{\"saldo\":0}"
  )
  (let [response (app (mock/request :get "/saldo"))]
    (fact "O status da resposta é 200"
      (:status response) => 200)
      
    (fact "O texto do corpo é um JSON cuja chave é saldo e o valor é 0"
      (:body response) => "{\"saldo\":0}")
  )
)
  