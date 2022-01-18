(defproject controle-financeiro "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [clj-http "3.12.3"]
                 [cheshire "5.10.1"]
                 [ring/ring-json "0.5.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [clj-postgresql "0.7.0"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-midje "3.2.1"]
            [lein-cloverage "1.2.2"]]
  :ring {:handler controle-financeiro.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]
                        [midje "1.10.5"]
                        [ring/ring-core "1.9.4"]
                        [ring/ring-jetty-adapter "1.9.4"]]}}
  :test-paths ["test/unitarios" "test/aceitacao"]
)
