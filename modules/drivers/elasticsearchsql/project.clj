(defproject metabase/elasticsearchsql-driver "0.1.0-SNAPSHOT"
  :min-lein-version "2.5.0"

  :profiles
  {:provided
   {:dependencies [[metabase-core "1.0.0-SNAPSHOT"]]}

   :uberjar
   {:auto-clean    true
    :aot           :all
    :javac-options ["-target" "1.8", "-source" "1.8"]
    :target-path   "target/%s"
    :uberjar-name  "elasticsearchsql.metabase-driver.jar"}})
