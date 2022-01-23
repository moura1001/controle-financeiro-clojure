(ns controle-financeiro.domain.transacao
  (:require [clojure.string :refer [blank?]]))

(defn rotulos-organizados [transacao]
  (->>
    (:rotulos transacao)
    (conj [])
    (flatten)
    (set)
  )
)

(defn rotulos-validos [transacao]
  (or
    (not (contains? transacao :rotulos))
    (empty? (:rotulos transacao))
    (let
      [
        rotulos (rotulos-organizados transacao)
      ]
        
      (not (some blank? rotulos))
    )
  )
)

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
    (rotulos-validos transacao)
  )
)