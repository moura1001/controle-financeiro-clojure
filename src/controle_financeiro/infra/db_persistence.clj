(ns controle-financeiro.infra.db-persistence
  (:require [clojure.string :refer [blank?]]))
  
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

(defn- sem-rotulo [transacao]
  (or
    (not (contains? transacao :rotulos))
    (empty? (:rotulos transacao))
    (every? blank? (:rotulos transacao))
  )
)

(defn transacoes-com-filtro [filtros]
  (let
    [
      rotulos
      (->>
        (:rotulos filtros)
        (conj [])
        (flatten)
        (set)
      )
    ]
    
    (if-not (every? blank? rotulos)
      
      (filter
        #(some rotulos (:rotulos %))
        (transacoes)
      )
      
      (filter
        sem-rotulo
        (transacoes)
      )
    )
  )
)
  