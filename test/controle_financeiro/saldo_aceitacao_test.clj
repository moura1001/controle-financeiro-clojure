(ns controle-financeiro.saldo-aceitacao-test
  (:require [controle-financeiro.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [midje.sweet :refer :all]))

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
