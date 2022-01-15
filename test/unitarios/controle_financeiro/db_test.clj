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

(facts "Filtra transações por tipo"
  (def transacoes-aleatorias
    '(
      {:valor 8 :tipo "despesa"}
      {:valor 16 :tipo "receita"}
      {:valor 32 :tipo "despesa"}
      {:valor 64 :tipo "receita"}
    )
  )
  
  (against-background
    [
      (before :facts
        [
          (limpar-colecao)
          (doseq [transacao transacoes-aleatorias]
            (registrar transacao)
          )
        ]
      )
    ]
    
    (fact "Encontra apenas as receitas"
      (transacoes-do-tipo "receita")
        =>
        '(
          {:valor 16 :tipo "receita"}
          {:valor 64 :tipo "receita"}
        )
    )
    
    (fact "Encontra apenas as despesas"
      (transacoes-do-tipo "despesa")
        =>
        '(
          {:valor 8 :tipo "despesa"}
          {:valor 32 :tipo "despesa"}
        )
    )
  )
)

(facts "Filtra transações por rótulo"
  (def transacoes-aleatorias
    '(
      {:valor 33.0M :tipo "despesa" :rotulos ["livro" "educação"]}
      {:valor 3000.0M :tipo "receita" :rotulos ["salário"]}
      {:valor 29.0M :tipo "despesa" :rotulos ["jogo" "entretenimento"]}
      {:valor 88.0M :tipo "despesa" :rotulos ["curso" "educação"]}
    )
  )
  
  (against-background
    [
      (before :facts
        [
          (limpar-colecao)
          (doseq [transacao transacoes-aleatorias]
            (registrar transacao)
          )
        ]
      )
      
      (after :facts (limpar-colecao))
    ]
    
    (fact "Encontra a transação com rótulo 'salário'"
      (transacoes-com-filtro {:rotulos "salário"})
        =>
        '(
          {:valor 3000.0M :tipo "receita" :rotulos ["salário"]}
        )
    )
    
    (fact "Encontra as 2 transações com rótulo 'educação'"
      (transacoes-com-filtro {:rotulos ["educação"]})
        =>
        '(
          {:valor 33.0M :tipo "despesa" :rotulos ["livro" "educação"]}
          {:valor 88.0M :tipo "despesa" :rotulos ["curso" "educação"]}
        )
    )
    
    (fact "Encontra as 2 transações com rótulo 'livro' ou 'curso'"
      (transacoes-com-filtro {:rotulos ["livro" "curso"]})
        =>
        '(
          {:valor 33.0M :tipo "despesa" :rotulos ["livro" "educação"]}
          {:valor 88.0M :tipo "despesa" :rotulos ["curso" "educação"]}
        )
    )
  )
)
  