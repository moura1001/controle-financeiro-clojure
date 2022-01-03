(ns controle-financeiro.saldo-aceitacao-test
  (:require [controle-financeiro.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [midje.sweet :refer :all]
            [clj-http.client :as http]))

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
