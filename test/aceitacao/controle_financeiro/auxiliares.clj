(ns controle-financeiro.auxiliares
  (:require [controle-financeiro.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clj-http.client :as http]
            [cheshire.core :as json]))

(def servidor (atom nil))

(defn iniciar-servidor [porta]
  (swap!
    servidor
    (fn [_] (run-jetty app {:port porta :join? false}))  
  )
)
  
(defn parar-servidor []
  (.stop @servidor)
)
  
(def porta-padrao 3001)
  
(defn endereco-para [rota]
  (str "http://localhost:" porta-padrao rota)
)
  
(def requisicao-para (comp http/get endereco-para))
  
(defn conteudo [rota]
  (:body (requisicao-para rota))
)

(defn parse-string-producing-keywords-as-keys [string]
  (json/parse-string string true)
)