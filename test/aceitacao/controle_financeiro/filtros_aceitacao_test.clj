(ns controle-financeiro.filtros-aceitacao-test
  (:require [midje.sweet :refer :all]
            [clj-http.client :as http]
            [controle-financeiro.auxiliares :refer :all]
            [controle-financeiro.infra.db-persistence :as db]))

(def transacoes-aleatorias
  '(
    {:valor 33.0M :tipo "despesa"}
    {:valor 2700.0M :tipo "receita"}
    {:valor 29.0M :tipo "despesa"}
    {:valor 88.0M :tipo "despesa"}
  )
)

(against-background
  [
    (before :facts
      [
        (iniciar-servidor porta-padrao)
        (db/limpar-colecao)
      ]
    )
    (after :facts (parar-servidor))
  ]
  
  (fact "Não existem despesas" :aceitacao
    (parse-string-producing-keywords-as-keys
      (conteudo "/despesas")
    ) => {:transacoes '()}
  )
  
  (fact "Não existem receitas" :aceitacao
    (parse-string-producing-keywords-as-keys
      (conteudo "/receitas")
    ) => {:transacoes '()}
  )
  
  (fact "Não existem transações" :aceitacao
    (parse-string-producing-keywords-as-keys
      (conteudo "/transacoes")
    ) => {:transacoes '()}
  )
)

(against-background
  [
    (before :facts
      (doseq [transacao transacoes-aleatorias]
        (db/registrar transacao)
      )
    )
    (after :facts (db/limpar-colecao))
  ]
  
  (fact "Existem 3 despesas" :aceitacao
    (count
      (:transacoes
        (parse-string-producing-keywords-as-keys
          (conteudo "/despesas")
        )
      )
    ) => 3
  )
  
  (fact "Existe 1 receita" :aceitacao
    (count
      (:transacoes
        (parse-string-producing-keywords-as-keys
          (conteudo "/receitas")
        )
      )
    ) => 1
  )
  
  (fact "Existem 4 transações" :aceitacao
    (count
      (:transacoes
        (parse-string-producing-keywords-as-keys
          (conteudo "/transacoes")
        )
      )
    ) => 4
  )
)
