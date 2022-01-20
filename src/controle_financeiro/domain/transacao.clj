(ns controle-financeiro.domain.transacao
  (:require [clojure.string :refer [blank?]]))

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
    (or
      (not (contains? transacao :rotulos))
      (empty? (:rotulos transacao))
      (not (some blank? (:rotulos transacao)))
    )
  )
)