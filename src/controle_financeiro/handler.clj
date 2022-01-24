(ns controle-financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [controle-financeiro.service.transacoes-service :as transacoes]
            [compojure.coercions :refer [as-int]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/saldo" [] (transacoes/get-saldo))
  (POST "/transacoes" requisicao (transacoes/create-transacao requisicao))
  (GET "/transacoes" {filtros :params} (transacoes/get-transacoes filtros))
  (GET "/despesas" [] (transacoes/get-despesas))
  (GET "/receitas" [] (transacoes/get-receitas))
  (DELETE "/transacoes/:id" [id :<< as-int]
    (transacoes/remove-transacao id)
  )
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
    (wrap-json-body {:keywords? true :bigdecimals? true})
  )
)
