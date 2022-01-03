(ns controle-financeiro.saldo-test
  (:require [controle-financeiro.handler :refer [app]]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]))
  
(facts "Saldo inicial deve ser 0"
  (let [response (app (mock/request :get "/saldo"))]
    (fact "O status da resposta é 200"
      (:status response) => 200)
    (fact "O texto do corpo é '0'"
      (:body response) => "0")
  )
)
  