(ns controle-financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [controle-financeiro.auxiliares :refer :all]))

(against-background
  [
    (before :facts (iniciar-servidor porta-padrao))
    (after :facts (parar-servidor))
  ]
  
  (fact "O saldo inicial é 0" :aceitacao
    (parse-string-producing-keywords-as-keys
      (conteudo "/saldo")
    ) => {:saldo 0}
  )
)
