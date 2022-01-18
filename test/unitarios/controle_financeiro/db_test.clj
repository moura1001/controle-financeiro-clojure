(ns controle-financeiro.db-test
  (:require [midje.sweet :refer :all]
            [controle-financeiro.infra.db-persistence :refer :all]
            [controle-financeiro.infra.db-postgres :as pg]))
  
(facts "Guarda uma transação na base de dados"
  (against-background
    [
      (before :facts (pg/limpar-base))
    ]
  )
  
  (fact "A coleção de transações começa vazia"
    (count (pg/transacoes)) => 0
  )
  
  (fact "A transação é o primeiro registro"
    (pg/registrar {:valor 7 :tipo "receita"})
    => {:id 1 :valor 7M :tipo "receita" :rotulos []}
    
    (count (pg/transacoes)) => 1
  )
)

(facts "Calcula o saldo dada uma coleção de transações"
  (against-background
    [
      (before :facts (pg/limpar-base))
    ]
  )
  
  (fact "Saldo é positivo quando só tem receitas"
    (pg/registrar {:valor 1 :tipo "receita"})
    (pg/registrar {:valor 2 :tipo "receita"})
    (pg/registrar {:valor 4 :tipo "receita"})
    (pg/registrar {:valor 8 :tipo "receita"})
    (pg/saldo) => 15M
  )
  
  (fact "Saldo é negativo quando só tem despesas"
    (pg/registrar {:valor 16 :tipo "despesa"})
    (pg/registrar {:valor 32 :tipo "despesa"})
    (pg/registrar {:valor 64 :tipo "despesa"})
    (pg/registrar {:valor 128 :tipo "despesa"})
    (pg/saldo) => -240M
  )
  
  (fact "Saldo é a soma das receitas menos a soma das despesas"
    (pg/registrar {:valor 1 :tipo "despesa"})
    (pg/registrar {:valor 16 :tipo "receita"})
    (pg/registrar {:valor 8 :tipo "despesa"})
    (pg/registrar {:valor 128 :tipo "receita"})
    (pg/saldo) => 135M
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
      {:valor 150.0M :tipo "receita"}
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
    
    (fact "Encontra a transação sem rótulo"
      (transacoes-com-filtro {:rotulos ""})
        =>
        '(
          {:valor 150.0M :tipo "receita"}
        )
    )
  )
)
  