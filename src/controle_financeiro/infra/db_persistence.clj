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

(defn- despesa? [transacao]
  (= (:tipo transacao) "despesa")
)

(defn- calcular [acumulado transacao]
  (let [valor (:valor transacao)]
  
    (if (despesa? transacao)
      (- acumulado valor)
      (+ acumulado valor)
    ) 
  )
)

(defn saldo []
  (reduce calcular 0 @registros)
)

(defn transacoes-do-tipo [tipo]
  (filter
    #(= tipo (:tipo %))
    (transacoes)
  )
)
  