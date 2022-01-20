(ns controle-financeiro.transacao-test
  (:require [controle-financeiro.handler :refer [app]]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [controle-financeiro.infra.db-postgres :as db]
            [controle-financeiro.domain.transacao :refer :all]))
  
(facts "Registra uma receita no valor de 10"
  (against-background
    (db/registrar {:valor 10 :tipo "receita"})
    => {:id 1 :valor 10 :tipo "receita" :rotulos []}
  )
  (let [response
    (app
      (
        -> (mock/request :post "/transacoes")
        (mock/json-body {:valor 10 :tipo "receita"})
      )
    )]
    
    (fact "O status da resposta é 201"
      (:status response) => 201)
      
    (fact "O texto do corpo é um JSON com o conteúdo enviado e um id"
      (:body response) => "{\"id\":1,\"valor\":10,\"tipo\":\"receita\",\"rotulos\":[]}")
  )
)

(fact "Uma transação sem valor não é válida"
  (eh-valida? {:tipo "receita"}) => false
)

(fact "Uma transação com valor negativo não é válida"
  (eh-valida? {:valor -4 :tipo "receita"}) => false
)

(fact "Uma transação com valor não númerico não é válida"
  (eh-valida? {:valor "dez" :tipo "receita"}) => false
)

(fact "Uma transação sem tipo não é válida"
  (eh-valida? {:valor 128}) => false
)

(fact "Uma transação com tipo desconhecido não é válida"
  (eh-valida? {:valor 512 :tipo "investimento"}) => false
)

(fact "Uma transação com rótulos em branco não é válida"
  (eh-valida? {:valor 32 :tipo "despesa" :rotulos [" " "curso" ""]}) => false
)

(fact "Uma transação com valor numérico positivo e com tipo conhecido é válida"
  (eh-valida? {:valor 64 :tipo "despesa"}) => true
)
  