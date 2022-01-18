(ns controle-financeiro.service.transacoes-service
  (:require [cheshire.core :as json]
            [controle-financeiro.infra.db-persistence :as db]
            [controle-financeiro.domain.transacao :as transacao]
            [controle-financeiro.infra.db-postgres :as pg]))

(defn como-json [conteudo & [status]]
  {
    :headers {"Content-Type" "application/json; charset=utf-8"}
    :body (json/generate-string conteudo)
    :status (or status 200)
  }
)

(defn get-saldo []
  (como-json {:saldo (pg/saldo)})
)

(defn create-transacao [requisicao]
  (if (transacao/eh-valida? (:body requisicao))
    (-> (pg/registrar (:body requisicao))
      (como-json 201)
    )

    (como-json {:mensagem "Requisição inválida"} 422)
  )
)

(defn get-transacoes [filtros]
  (como-json
    {
      :transacoes
      (if (empty? filtros)
        (pg/transacoes)
        (db/transacoes-com-filtro filtros)
      )
    }
  )
)

(defn get-despesas []
  (como-json {:transacoes (pg/transacoes-do-tipo "despesa")})
)

(defn get-receitas []
  (como-json {:transacoes (pg/transacoes-do-tipo "receita")})
)
