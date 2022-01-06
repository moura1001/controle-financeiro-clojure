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

(facts "Calcula o saldo dada uma coleção de transações"
  (against-background
    [
      (before :facts (limpar-colecao))
    ]
  )
  
  (fact "Saldo é positivo quando só tem receitas"
    (registrar {:valor 1 :tipo "receita"})
    (registrar {:valor 2 :tipo "receita"})
    (registrar {:valor 4 :tipo "receita"})
    (registrar {:valor 8 :tipo "receita"})
    (saldo) => 15
  )
  
  (fact "Saldo é negativo quando só tem despesas"
    (registrar {:valor 16 :tipo "despesa"})
    (registrar {:valor 32 :tipo "despesa"})
    (registrar {:valor 64 :tipo "despesa"})
    (registrar {:valor 128 :tipo "despesa"})
    (saldo) => -240
  )
  
  (fact "Saldo é a soma das receitas menos a soma das despesas"
    (registrar {:valor 1 :tipo "despesa"})
    (registrar {:valor 16 :tipo "receita"})
    (registrar {:valor 8 :tipo "despesa"})
    (registrar {:valor 128 :tipo "receita"})
    (saldo) => 135
  )
)
  