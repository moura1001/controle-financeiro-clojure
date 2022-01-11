(ns controle-financeiro.domain.transacao)

(defn eh-valida? [transacao]
  (and
    (contains? transacao :valor)
    (number? (:valor transacao))
    (pos? (:valor transacao))
    (contains? transacao :tipo)
    (or
      (= "despesa" (:tipo transacao))
      (= "receita" (:tipo transacao))
    )
  )
)