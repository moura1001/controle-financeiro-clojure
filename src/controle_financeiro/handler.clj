(ns controle-financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [cheshire.core :as json]
            [controle-financeiro.infra.db-persistence :as db]
            [ring.middleware.json :refer [wrap-json-body]]
            [controle-financeiro.domain.transacao :as transacao]))

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
    (if (transacao/eh-valida? (:body requisicao))
      (-> (db/registrar (:body requisicao))
        (como-json 201)
      )

      (como-json {:mensagem "Requisição inválida"} 422)
    )
  )
  (GET "/transacoes" {filtros :params}
    (como-json
      {
        :transacoes
        (if (empty? filtros)
          (db/transacoes)
          (db/transacoes-com-filtro filtros)
        )
      }
    )
  )
  (GET "/despesas" []
    (como-json {:transacoes (db/transacoes-do-tipo "despesa")})
  )
  (GET "/receitas" []
    (como-json {:transacoes (db/transacoes-do-tipo "receita")})
  )
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
    (wrap-json-body {:keywords? true :bigdecimals? true})
  )
)
