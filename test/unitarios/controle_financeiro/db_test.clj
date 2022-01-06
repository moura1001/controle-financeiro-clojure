(ns controle-financeiro.db-test
  (:require [midje.sweet :refer :all]
            [controle-financeiro.infra.db-persistence :refer :all]))
  
(facts "Guarda uma transação num átomo"
  (against-background
    [
      (before :facts (limpar-colecao))
    ]
  )
  
  (fact "A coleção de transações começa vazia"
    (count (transacoes)) => 0
  )
  
  (fact "A transação é o primeiro registro"
    (registrar {:valor 7 :tipo "receita"})
    => {:id 1 :valor 7 :tipo "receita"}
    
    (count (transacoes)) => 1
  )
)
  