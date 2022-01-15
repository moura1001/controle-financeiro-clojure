(ns controle-financeiro.filtros-aceitacao-test
  (:require [midje.sweet :refer :all]
            [clj-http.client :as http]
            [controle-financeiro.auxiliares :refer :all]
            [controle-financeiro.infra.db-persistence :as db]))

(def transacoes-aleatorias
  '(
    {:valor 33.0M :tipo "despesa" :rotulos ["livro" "educação"]}
    {:valor 2700.0M :tipo "receita" :rotulos ["salário"]}
    {:valor 29.0M :tipo "despesa" :rotulos ["jogo" "entretenimento"]}
    {:valor 88.0M :tipo "despesa" :rotulos ["curso" "educação"]}
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
    
    (fact "Existe 1 receita com rótulo 'salário'" :aceitacao
      (count
        (:transacoes
          (parse-string-producing-keywords-as-keys
            (conteudo "/transacoes?rotulos=salário")
          )
        )
      ) => 1
    )
    
    (fact "Existem 2 despesas com rótulo 'livro' ou 'curso'"
      :aceitacao
      (count
        (:transacoes
          (parse-string-producing-keywords-as-keys
            (conteudo "/transacoes?rotulos=livro&rotulos=curso")
          )
        )
      ) => 2
    )
    
    (fact "Existem 2 despesas com rótulo 'educação'"
      :aceitacao
      (count
        (:transacoes
          (parse-string-producing-keywords-as-keys
            (conteudo "/transacoes?rotulos=educação")
          )
        )
      ) => 2
    )
  )
)
