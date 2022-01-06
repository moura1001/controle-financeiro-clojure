(ns controle-financeiro.infra.db-persistence)
  
(def registros (atom []))

(defn transacoes []
  @registros
)

(defn limpar-colecao []
  (reset! registros [])
)

(defn registrar [transacao]
  (let [colecao-atualizada (swap! registros conj transacao)]
    (merge transacao {:id (count colecao-atualizada)})
  )
)

(declare saldo)
  