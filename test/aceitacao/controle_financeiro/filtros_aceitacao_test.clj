(ns controle-financeiro.filtros-aceitacao-test
  (:require [midje.sweet :refer :all]
            [clj-http.client :as http]
            [controle-financeiro.auxiliares :refer :all]
            [controle-financeiro.infra.db-persistence :as db]))

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
