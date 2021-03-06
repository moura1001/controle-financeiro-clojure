(ns controle-financeiro.infra.db-postgres
  (:require [clojure.java.jdbc :as sql]
            [clj-postgresql.core :as pg]
            [clojure.string :refer [blank?]]
            [controle-financeiro.domain.transacao :refer [rotulos-organizados]]))
  
(def db
  (pg/spec
    :host (System/getenv "POSTGRES_HOST")
    :user (System/getenv "POSTGRES_USER")
    :dbname (System/getenv "POSTGRES_DB")
    :password (System/getenv "POSTGRES_PASSWORD")
  )
)

(defn transacoes []
  (sql/query
    db
    ["SELECT * FROM transacoes"]
  )
)

(defn limpar-base []
  (sql/execute!
    db
    ["
      DROP TABLE IF EXISTS transacoes;
      CREATE TABLE IF NOT EXISTS transacoes(
        id BIGSERIAL PRIMARY KEY,
        valor NUMERIC NOT NULL,
        tipo VARCHAR(8) NOT NULL,
        rotulos VARCHAR[] DEFAULT '{}'
      )
    "]
  )
)

(defn registrar [transacao]
  (->
    (sql/insert!
      db
      :transacoes
      (if (contains? transacao :rotulos)
        {
          :valor (:valor transacao)
          :tipo (:tipo transacao)
          :rotulos (rotulos-organizados transacao)
        }

        {
          :valor (:valor transacao)
          :tipo (:tipo transacao)
        }
      )
    )

    first
  )
)

(defn saldo []
  (->
    (sql/query
      db
      ["SELECT
          COALESCE(
            SUM(
              CASE tipo
                WHEN 'receita' THEN valor
                ELSE valor*-1
              END
            )
            , 0
          ) AS saldo
        FROM transacoes"
      ]
    )
    
    first
    :saldo
  )
)

(defn transacoes-do-tipo [tipo]
  (sql/query
    db
    ["SELECT * FROM transacoes
      WHERE tipo = ?" tipo
    ]
  )
)

(defn transacoes-com-filtro [filtros]
  (let
    [
      rotulos (rotulos-organizados filtros)
    ]
    
    (if-not (every? blank? rotulos)
      
      (sql/query
        db
        ["SELECT * FROM transacoes
          WHERE rotulos && ?" rotulos
        ]
      )
      
      (sql/query
        db
        ["SELECT * FROM transacoes
          WHERE rotulos = '{}'"
        ]
      )
    )
  )
)

(defn remover-transacao [id]
  (->
    (sql/query
      db
      ["WITH deleted AS
        (DELETE FROM transacoes
          WHERE id = ? RETURNING *)
        SELECT COUNT(*) FROM deleted" id
      ]
    )
    
    first
    :count
  )
)

(defn alterar-transacao [id nova-transacao]
  (sql/update!
    db
    :transacoes
    (if (contains? nova-transacao :rotulos)
      {
        :valor (:valor nova-transacao)
        :tipo (:tipo nova-transacao)
        :rotulos (rotulos-organizados nova-transacao)
      }

      {
        :valor (:valor nova-transacao)
        :tipo (:tipo nova-transacao)
      }
    )

    ["id = ?" id]
    {:return-keys ["*"]}
  )
)
  