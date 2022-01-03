(ns controle-financeiro.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [controle-financeiro.handler :refer :all]))

(facts "'Hello World' na rota raiz"
  (let [response (app (mock/request :get "/"))]
    (fact "O status da resposta é 200"
      (:status response) => 200)
    (fact "O texto do corpo é 'Hello World'"
      (:body response) => "Hello World")
  )
)

(facts "Rota inválida não existe"
  (let [response (app (mock/request :get "/invalid"))]
    (fact "O código de erro é 400"
      (:status response) => 404)
    (fact "O texto do corpo é 'Not Found'"
      (:body response) => "Not Found")
  )
)
