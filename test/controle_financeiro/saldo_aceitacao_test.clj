(ns controle-financeiro.saldo-aceitacao-test
  (:require [controle-financeiro.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [midje.sweet :refer :all]
            [clj-http.client :as http]
            [ring.mock.request :as mock]))

(def servidor (atom nil))

(defn iniciar-servidor [porta]
  (swap!
    servidor
    (fn [_] (run-jetty app {:port porta :join? false}))  
  )
)

(defn parar-servidor []
  (.stop @servidor)
)

(fact "O saldo inicial é 0"
  (iniciar-servidor 3001)
  (:body (http/get "http://localhost:3001/saldo")) => "0"
  (parar-servidor)
)

(facts "Saldo inicial deve ser 0"
  (let [response (app (mock/request :get "/saldo"))]
    (fact "O status da resposta é 200"
      (:status response) => 200)
    (fact "O texto do corpo é '0'"
      (:body response) => "0")
  )
)
