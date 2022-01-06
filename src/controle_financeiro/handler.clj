(ns controle-financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [cheshire.core :as json]
            [controle-financeiro.infra.db-persistence :as db]
            [ring.middleware.json :refer [wrap-json-body]]))

(defn como-json [conteudo & [status]]
  {
    :headers {"Content-Type" "application/json; charset=utf-8"}
    :body (json/generate-string conteudo)
    :status (or status 200)
  }
)

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/saldo" [] (como-json {:saldo (db/saldo)}))
  (POST "/transacoes" requisicao
    (-> (db/registrar (:body requisicao))
      (como-json 201)
    )
  )
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
    (wrap-json-body {:keywords? true :bigdecimals? true})
  )
)
