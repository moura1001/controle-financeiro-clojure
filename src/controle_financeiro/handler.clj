(ns controle-financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [cheshire.core :as json]
            [controle-financeiro.infra.db-persistence :as db]
            [ring.middleware.json :refer [wrap-json-body]]))

(defn saldo-como-json []
  {
    :headers {"Content-Type" "application/json; charset=utf-8"}
    :body (json/generate-string {:saldo 0})
  }
)

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/saldo" [] (saldo-como-json))
  (POST "/transacoes" requisicao (db/registrar (:body requisicao)))
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
    (wrap-json-body {:keywords? true :bigdecimals? true})
  )
)
