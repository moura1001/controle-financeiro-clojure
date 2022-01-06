(ns controle-financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [cheshire.core :as json]
            [controle-financeiro.infra.db-persistence :as db]
            [ring.middleware.json :refer [wrap-json-body]]))

(defn como-json [conteudo]
  {
    :headers {"Content-Type" "application/json; charset=utf-8"}
    :body (json/generate-string conteudo)
  }
)

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/saldo" [] (como-json {:saldo 0}))
  (POST "/transacoes" requisicao
    (-> (db/registrar (:body requisicao))
      (como-json)
    )
  )
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
    (wrap-json-body {:keywords? true :bigdecimals? true})
  )
)
